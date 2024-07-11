package org.m2sec.core.utils;

import burp.api.montoya.http.HttpService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.m2sec.core.common.Tuple;
import org.m2sec.core.enums.ContentType;
import org.m2sec.core.enums.Method;
import org.m2sec.core.enums.Protocol;
import org.m2sec.core.models.FormData;
import org.m2sec.core.models.Parameters;
import org.m2sec.core.models.UploadFile;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class HttpUtil {

    public static URL parseUrl(String urlStr) {
        try {
            return new URL(urlStr);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean urlIsSecure(URL url) {
        return url.getProtocol().equals("https");
    }

    public static String getProtocol(boolean secure) {
        return secure ? "https" : "http";
    }

    public static String getDomainUrl(HttpService httpService) {
        return getDomainUrl(httpService.secure(), httpService.host(), httpService.port());
    }

    public static String getDomainUrl(boolean secure, String host, int port) {
        return getProtocol(secure) + "://" + getFullHost(secure, host, port);
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

    public static String toFullPath(String path, String queryStr) {
        if (queryStr.isEmpty()) {
            return path;
        } else {
            return path + "?" + queryStr;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public static String normalizePath(String path) {
        String prefix = "/";
        if (path.startsWith(Protocol.HTTP.name().toLowerCase())) {
            URL url = HttpUtil.parseUrl(path);
            prefix = url.getProtocol() + "://" + url.getAuthority() + "/";
            path = url.getPath().isEmpty() ? "/" : url.getPath();
        }
        if (path.isEmpty()) return "/";
        String[] pathParts = path.split("/");
        if (pathParts.length == 0) return "/";
        String suffix = path.endsWith("/") ? "/" : ""; // spring在3.0后，声明为/a/b/c/使用/a/b/c访问无法匹配，该后缀做兼容
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
        return prefix + String.join("/", normalizedPathParts) + suffix;
    }

    public static <T extends Parameters<String>> T strToParameters(String str, String sep, String conn, Class<?
        extends T> clazz) {
        try {
            T retVal = clazz.getDeclaredConstructor().newInstance();
            if (str != null && !str.isEmpty()) {
                String[] pairs = str.split(sep);
                for (String pair : pairs) {
                    String[] keyValue = pair.split(conn, 2);
                    String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                    String value = (keyValue.length > 1) ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8) : "";
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
                sb.append(entry.getKey()).append(conn).append(urlEncodeValue ? URLEncoder.encode(value,
                    StandardCharsets.UTF_8) : value).append(sep);
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
        if (Method.GET.toString().equalsIgnoreCase(method) || Method.OPTIONS.toString().equalsIgnoreCase(method) || Method.HEAD.toString().equalsIgnoreCase(method)) {
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

    public static String getPathFromRaw(byte[] raw) {
        return ByteUtil.getWrappedText(raw, ' ');
    }

}
