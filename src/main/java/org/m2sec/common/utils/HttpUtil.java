package org.m2sec.common.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.m2sec.common.Tuple;
import org.m2sec.common.parsers.JsonParser;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class HttpUtil {

    public static Tuple<String, String> parseFullPath(@Nonnull String fullPath) {
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

    public static String toFullPath(@Nonnull String path, @Nonnull String queryStr) {
        if (queryStr.isEmpty()) {
            return path;
        } else {
            return path + "?" + queryStr;
        }
    }

    public static String normalizePath(String path) {
        String[] pathParts = path.split("/");
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
        return "/" + String.join("/", normalizedPathParts);
    }

    public static String jsonStrToQueryStr(String jsonStr) {
        JsonObject jsonObject = JsonParser.fromJsonStr(jsonStr, JsonObject.class);

        StringBuilder sb = new StringBuilder();
        if (jsonObject != null) {
            for (String key : jsonObject.keySet()) {
                JsonElement value = jsonObject.get(key);
                if (!sb.isEmpty()) {
                    sb.append("&");
                }
                String valueStr;
                if (value.isJsonObject() || value.isJsonArray()) {
                    valueStr = value.toString();
                } else {
                    valueStr = value.getAsString();
                }
                sb.append(URLEncoder.encode(key, StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(valueStr, StandardCharsets.UTF_8));
            }
        }
        return sb.toString();
    }

    public static String queryStrToJsonStr(String queryStr) {
        return JsonParser.toJsonStr(QueryStrToMap(queryStr));
    }

    public static Map<String, Object> QueryStrToMap(String queryStr) {
        Map<String, Object> queryParams = new HashMap<>();
        String[] pairs = queryStr.split("&");
        if (!queryStr.isEmpty()) {
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value =
                        (keyValue.length > 1)
                                ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8)
                                : "";
                queryParams.put(key, value);
            }
        }
        return queryParams;
    }

    public static String mapToQueryStr(Map<String, String> map) {
        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!queryString.isEmpty()) {
                queryString.append("&");
            }
            queryString
                    .append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return queryString.toString();
    }

    public static String convertMapToCookieStr(Map<String, String> cookies) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("; ");
        }

        // Remove the trailing "; " if there are cookies
        if (stringBuilder.length() > 2) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }

        return stringBuilder.toString();
    }

    public static Map<String, String> convertCookieStrToMap(String cookieStr) {
        Map<String, String> cookieMap = new HashMap<>();
        Arrays.stream(cookieStr.split("; "))
                .map(String::trim)
                .map(part -> part.split("=", 2))
                .filter(parts -> parts.length == 2)
                .forEach(parts -> cookieMap.put(parts[0], parts[1]));
        return cookieMap;
    }

    public static boolean isInvalidUrl(String urlStr) {
        try {
            new URL(urlStr);
            return false;
        } catch (MalformedURLException e) {
            return true;
        }
    }
}
