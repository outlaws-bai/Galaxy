package org.m2sec.core.utils;

import burp.api.montoya.http.HttpService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.extern.slf4j.Slf4j;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.Tuple;
import org.m2sec.core.enums.ContentType;
import org.m2sec.core.enums.Method;
import org.m2sec.core.enums.Protocol;
import org.m2sec.core.models.*;
import org.python.jline.internal.Log;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Slf4j
public class HttpUtil {

    public static URL parseUrl(String urlStr) {
        try {
            return new URL(urlStr);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean urlIsSecure(URL url) {
        return url.getProtocol().equals(Protocol.HTTPS.toRaw());
    }

    public static String getDomainUrl(HttpService httpService) {
        return getDomainUrl(httpService.secure(), httpService.host(), httpService.port());
    }

    public static String getDomainUrl(boolean secure, String host, int port) {
        return Protocol.of(secure).toRaw() + Constants.HTTP_PROTOCOL_DOMAIN_SEP + getFullHost(secure, host, port);
    }

    public static int defaultPort(boolean isSecure) {
        if (!isSecure) return 80;
        else return 443;
    }

    public static int getUrlPort(URL url) {
        int port = url.getPort();
        if (port != -1) return port;
        else return urlIsSecure(url) ? 443 : 80;
    }

    public static Tuple<String, String> parseFullPath(String fullPath) {
        String path;
        String queryStr;
        if (fullPath.contains("?")) {
            String[] parts = fullPath.split("\\?", 2);
            path = parts[0];
            queryStr = parts[1];
        } else {
            path = fullPath;
            queryStr = "";
        }
        return new Tuple<>(path, queryStr);
    }

    public static String getFullHost(boolean secure, String host, int port) {
        if ((secure && port == 443) || (!secure && port == 80)) {
            return host;
        }
        return host + ":" + port;
    }

    public static String getFullPath(String path, Query query) {
        return getFullPath(path, query.toRawString());
    }

    public static String getFullPath(String path, String queryStr) {
        if (queryStr == null || queryStr.isEmpty()) {
            return path;
        } else {
            return path + "?" + queryStr;
        }
    }

    public static String getUrl(boolean isSecure, String host, int port, String path) {
        return getDomainUrl(isSecure, host, port) + path;
    }

    public static String getFullUrl(boolean isSecure, String host, int port, String path, Query query) {
        return getFullUrl(isSecure, host, port, path, query.toRawString());
    }

    public static String getFullUrl(boolean isSecure, String host, int port, String path, String queryString) {
        return getDomainUrl(isSecure, host, port) + getFullPath(path, queryString);
    }

    public static String normalizePath(String path) {
        String prefix = "/";
        if (path.startsWith(Protocol.HTTP.name().toLowerCase())) {
            URL url = HttpUtil.parseUrl(path);
            prefix = url.getProtocol() + "://" + url.getAuthority() + "/";
            path = url.getPath().isEmpty() ? "/" : url.getPath();
        }
        if (path.isEmpty()) return prefix;
        String[] pathParts = path.split("/");
        if (pathParts.length == 0) return prefix;
        String suffix = path.endsWith("/") ? "/" : ""; // spring在3.0后，声明为/a/b/c/使用/a/b/c访问无法匹配，该后缀做兼容
        ArrayList<String> normalizedPathParts = normalizePathParts(pathParts);
        return prefix + String.join("/", normalizedPathParts) + suffix;
    }

    public static String getCleanUrl(Request request) {
        if (request.getPath().startsWith(Protocol.HTTP.toRaw())) return request.getPath();
        String protocol = Protocol.of(request.isSecure()).toRaw();
        return protocol + "://" + request.getHost() + ":" + request.getPort() + request.getPath();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private static ArrayList<String> normalizePathParts(String[] pathParts) {
        ArrayList<String> normalizedPathParts = new ArrayList<>();
        for (String part : pathParts) {
            if ("".equals(part)) {
            } else if (part.startsWith(";")) {
            } else if (part.startsWith("..")) {
                if (!normalizedPathParts.isEmpty()) {
                    normalizedPathParts.remove(normalizedPathParts.size() - 1);
                }
            } else if (part.startsWith(".")) {
            } else if (part.contains(";")) {
                normalizedPathParts.add(part.split(";")[0]);
            } else {
                normalizedPathParts.add(part);
            }
        }
        return normalizedPathParts;
    }

    public static <T extends Parameters<String>> T strToParameters(String str, String sep, String conn, Class<?
        extends T> clazz, boolean urlDecode) {
        try {
            T retVal = clazz.getDeclaredConstructor().newInstance();
            if (str != null && !str.isEmpty()) {
                String[] pairs = str.split(sep);
                for (String pair : pairs) {
                    String[] keyValue = pair.split(conn, 2);
                    String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                    String value;
                    if (keyValue.length > 1) {
                        if (urlDecode) {
                            value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                        } else {
                            value = keyValue[1];
                        }
                    } else {
                        value = "";
                    }
                    retVal.add(key, value);
                }
            }
            return retVal;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends Parameters<String>> String parametersToStr(T parameters, String sep, String conn,
                                                                        boolean urlEncodeValue) {
        if (parameters.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
            for (String value : entry.getValue()) {
                sb.append(entry.getKey()).append(conn).append(urlEncodeValue ? URLEncoder.encode(value, StandardCharsets.UTF_8) : value).append(sep);
            }
        }
        sb.delete(sb.length() - sep.length(), sb.length());
        return sb.toString();
    }



    public static boolean isCorrectUrl(String urlStr) {
        try {
            new URL(urlStr);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static ContentType getContentType(String method, @Nullable String contentTypeHeaderValue) {
        if (method != null && (Method.GET.toString().equalsIgnoreCase(method) || Method.OPTIONS.toString().equalsIgnoreCase(method) || Method.HEAD.toString().equalsIgnoreCase(method))) {
            return ContentType.NON_BODY;
        }
        if (contentTypeHeaderValue != null) {
            for (ContentType contentType : ContentType.values()) {
                if (contentType.getHeaderValuePrefix() == null) continue;
                if (contentTypeHeaderValue.startsWith(contentType.getHeaderValuePrefix())) {
                    return contentType;
                }
            }
        }
        return ContentType.TEXT;
    }

    public static String extractBoundary(String contentType) {
        if (contentType != null && contentType.contains("multipart/form-data")) {
            String[] parts = contentType.split(";[ ]*");
            for (String part : parts) {
                part = part.trim();
                if (part.startsWith("boundary=")) {
                    return part.substring("boundary=".length()).replace("\"", "");
                }
            }
        }
        return null; // 如果没有找到 boundary，返回 null
    }

    public static FormData<Object> parseContentFormDataContent(byte[] content, String boundary) {
        FormData<Object> formData = new FormData<>();

        // 定义边界分隔符
        String boundaryString = "--" + boundary;
        byte[] boundaryBytes = boundaryString.getBytes(StandardCharsets.UTF_8);
        byte[] endBoundaryBytes = (boundaryString + "--").getBytes(StandardCharsets.UTF_8);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
        ByteArrayOutputStream partBuffer = new ByteArrayOutputStream();

        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                partBuffer.write(buffer, 0, bytesRead);
            }

            byte[] allParts = partBuffer.toByteArray();
            int startPos = 0;

            while (startPos < allParts.length) {
                // 查找边界的位置
                int boundaryPos = findBoundary(allParts, startPos, boundaryBytes);
                if (boundaryPos == -1) {
                    break; // 未找到更多的边界
                }

                // 下一个边界的开始
                int nextBoundaryPos = findBoundary(allParts, boundaryPos + boundaryBytes.length, boundaryBytes);
                if (nextBoundaryPos == -1) {
                    nextBoundaryPos = findBoundary(allParts, boundaryPos + boundaryBytes.length, endBoundaryBytes);
                    if (nextBoundaryPos == -1) {
                        break; // 最后一个边界已经处理完毕
                    }
                }

                // 获取该部分内容
                int partStart = boundaryPos + boundaryBytes.length + 2; // 跳过边界和换行
                int partEnd = nextBoundaryPos - 2; // 去掉换行符
                byte[] part = Arrays.copyOfRange(allParts, partStart, partEnd);

                // 处理该部分
                processPart(part, formData, boundary);

                startPos = nextBoundaryPos;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return formData;
    }

    private static int findBoundary(byte[] content, int startPos, byte[] boundary) {
        for (int i = startPos; i <= content.length - boundary.length; i++) {
            boolean match = true;
            for (int j = 0; j < boundary.length; j++) {
                if (content[i + j] != boundary[j]) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return i;
            }
        }
        return -1;
    }

    private static void processPart(byte[] part, FormData<Object> formData, String boundary) {
        ByteArrayInputStream partStream = new ByteArrayInputStream(part);
        ByteArrayOutputStream contentBuffer = new ByteArrayOutputStream();
        StringBuilder contentDisposition = new StringBuilder();
        Headers headers = new Headers();  // 创建 Headers 实例
        boolean isFile = false;
        String name = null;
        String filename = null;

        // 读取 Content-Disposition 和其他头
        int b;
        while ((b = partStream.read()) != -1) {
            contentDisposition.append((char) b);
            if (contentDisposition.toString().endsWith("\r\n\r\n")) {
                break;
            }
        }

        // 解析 Content-Disposition 头
        String[] headersArray = contentDisposition.toString().split("\r\n");
        for (String header : headersArray) {
            String trimmedHeader = header.trim();
            if (trimmedHeader.startsWith("Content-Disposition:")) {
                String[] parts = trimmedHeader.split(";");
                for (String partHeader : parts) {
                    partHeader = partHeader.trim();
                    if (partHeader.startsWith("name=\"")) {
                        name = partHeader.substring(6, partHeader.length() - 1);
                    } else if (partHeader.startsWith("filename=\"")) {
                        filename = partHeader.substring(10, partHeader.length() - 1);
                        isFile = true;
                    }
                }
            } else if (!trimmedHeader.equals(boundary)) {
                String[] keyValue = trimmedHeader.split(": ", 2);
                if (keyValue.length == 2) {
                    headers.add(keyValue[0], keyValue[1]); // 假设 Headers 类有 add 方法
                }
            }
        }

        // 读取内容
        while ((b = partStream.read()) != -1) {
            contentBuffer.write(b);
        }

        // 将内容添加到 FormData
        if (isFile) {
            byte[] fileContent = contentBuffer.toByteArray();
            UploadFile uploadFile = new UploadFile(filename, headers, fileContent);
            uploadFile.setFilename(filename);
            uploadFile.setContent(fileContent);
            uploadFile.setHeaders(headers); // 设置文件头信息

            formData.add(name, uploadFile);
        } else {
            String value = contentBuffer.toString();
            formData.add(name, value);
        }
    }

    private static boolean endsWith(byte[] array, byte[] suffix) {
        if (array.length < suffix.length) {
            return false;
        }
        for (int i = 0; i < suffix.length; i++) {
            if (array[array.length - suffix.length + i] != suffix[i]) {
                return false;
            }
        }
        return true;
    }



    public static String generateBoundary() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static byte[] generateContentFormDataContent(String boundary, FormData<String> formData,
                                                        FormData<UploadFile> files) {

        if (formData.isEmpty() && files.isEmpty()) {
            return new byte[]{};
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream), true);
            // 1、处理form data
            for (Map.Entry<String, List<String>> entry : formData.entrySet()) {
                String name = entry.getKey();
                for (String value : entry.getValue()) {
                    writer.append("--").append(boundary).append("\r\n").append("Content-Disposition: form-data; " +
                        "name=\"").append(name).append("\"").append("\r\n").append("\r\n").append(value).append("\r\n"
                    ).flush();
                }
            }

            // 2、处理 file
            for (Map.Entry<String, List<UploadFile>> entry : files.entrySet()) {
                String name = entry.getKey();
                for (UploadFile uploadFile : entry.getValue()) {
                    writer.append("--").append(boundary).append("\r\n").append("Content-Disposition: form-data; " +
                        "name=\"").append(name).append("\"; filename=\"").append(uploadFile.getFilename()).append(
                        "\"").append("\r\n").flush();
                    // 处理 file header
                    for (Map.Entry<String, List<String>> header : uploadFile.getHeaders().entrySet()) {
                        for (String value : header.getValue()) {
                            writer.append(header.getKey()).append(": ").append(value).append("\r\n").flush();
                        }
                    }
                    writer.append("\r\n").flush();
                    outputStream.write(uploadFile.getContent());
                    outputStream.flush();
                    writer.append("\r\n");
                    writer.flush();
                }
            }

            writer.append("--").append(boundary).append("--").append("\r\n");
            writer.close();

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<?, ?> updateJsonValuesByMap(Map<?, ?> map, String suffix) {
        JsonElement jsonElement = JsonUtil.toJsonElement(map);
        updateJsonValuesByJson(jsonElement, suffix);
        return JsonUtil.fromJsonStr(jsonElement.toString(), Map.class);
    }

    public static void updateJsonValuesByJson(JsonElement jsonElement, String suffix) {
        if (jsonElement.isJsonObject()) {
            updateJsonValuesByJsonObject(jsonElement.getAsJsonObject(), suffix);
        } else if (jsonElement.isJsonArray()) {
            updateJsonValuesByJsonArray(jsonElement.getAsJsonArray(), suffix);
        }
    }

    public static void updateJsonValuesByJsonObject(JsonObject jsonObject, String suffix) {
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            JsonElement jsonElement = entry.getValue();
            if (jsonElement.isJsonObject()) {
                updateJsonValuesByJsonObject(jsonElement.getAsJsonObject(), suffix);
            } else if (jsonElement.isJsonArray()) {
                updateJsonValuesByJsonArray(jsonElement.getAsJsonArray(), suffix);
            } else {
                jsonObject.addProperty(entry.getKey(), jsonElement.getAsString() + suffix);
            }
        }
    }

    public static void updateJsonValuesByJsonArray(JsonArray jsonArray, String suffix) {
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonElement jsonElement = jsonArray.get(i);
            if (jsonElement.isJsonObject()) {
                updateJsonValuesByJsonObject(jsonElement.getAsJsonObject(), suffix);
            } else if (jsonElement.isJsonArray()) {
                updateJsonValuesByJsonArray(jsonElement.getAsJsonArray(), suffix);
            } else {
                jsonArray.set(i, new JsonPrimitive(jsonElement.getAsString() + suffix));
            }
        }
    }

    public static Object bodyToJson(String body) {
        try {
            JsonElement jsonElement = JsonUtil.toJson(body);
            if (jsonElement.isJsonObject()) {
                return JsonUtil.toMap(jsonElement);
            } else if (jsonElement.isJsonArray()) {
                return JsonUtil.toList(jsonElement);
            }
        } catch (Exception ignored) {
        }
        throw new UnsupportedOperationException("Body must be json");
    }

}
