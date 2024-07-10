package org.m2sec.core.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.m2sec.core.common.JsonParser;
import org.m2sec.core.common.Tuple;
import org.m2sec.core.enums.ContentType;
import org.m2sec.core.utils.HttpUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class FuzzDict {
    private String host;

    /**
     * 所有的路径
     */
    private List<String> paths;

    /**
     * 路径的最后一段, 称之为action, 例如detail、list等
     */
    private List<String> actions;

    /**
     * 所有的参数名，包括query、json、form等
     */
    private List<String> params;

    /**
     * 所有的header头key
     */
    private List<String> headers;

    /**
     * 所有cookie的key
     */
    private List<String> cookies;

    public static FuzzDict of(List<Tuple<Request, Response>> requestResponses) {
        String host = requestResponses.get(0).getFirst().getHost();
        Set<String> paths =
            requestResponses.stream().map(Tuple::getFirst).map(Request::getPath).collect(Collectors.toSet());
        Set<String> actions =
            requestResponses.stream().map(Tuple::getFirst).map(FuzzDict::getAction).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<String> params =
            requestResponses.stream().flatMap(x -> getParamsNames(x).stream()).collect(Collectors.toSet());
        Set<String> headers =
            requestResponses.stream().map(Tuple::getFirst).flatMap(request -> request.getHeaders().keySet().stream()).collect(Collectors.toSet());
        Set<String> cookies =
            requestResponses.stream().map(Tuple::getFirst).map(Request::getCookies).filter(Objects::nonNull).map(Cookies::keySet).flatMap(Set::stream).collect(Collectors.toSet());
        return new FuzzDict(host, paths.stream().toList(), actions.stream().toList(), params.stream().toList(),
            headers.stream().toList(), cookies.stream().toList());
    }

    public static String getAction(Request request) {
        String path = HttpUtil.normalizePath(request.getPath());
        String[] pathParts = path.split("/");
        if (pathParts.length > 0) {
            return pathParts[pathParts.length - 1];
        }
        return null;
    }

    public static Set<String> getParamsNames(Tuple<Request, Response> requestResponse) {
        Set<String> params = new HashSet<>();
        Request request = requestResponse.getFirst();
        Response response = requestResponse.getSecond();
        if (!request.getQuery().isEmpty()) {
            extractParamNamesFromMap(params, request.getQuery());
        }
        ContentType contentType = request.getContentType();
        try {
            if (contentType == ContentType.FORM) {
                extractParamNamesFromMap(params, Form.of(new String(request.getContent())));
            } else if (contentType == ContentType.JSON) {
                JsonObject bodyJsonObject = JsonParser.fromJsonStr(new String(request.getContent()), JsonObject.class);
                extractParamNamesFromJson(params, bodyJsonObject);
            }
        } catch (Exception e) {
            // ...
        }
        try {
            if (response != null) {
                JsonObject bodyJsonObject = JsonParser.fromJsonStr(new String(response.getContent()), JsonObject.class);
                extractParamNamesFromJson(params, bodyJsonObject);
            }
        } catch (Exception e) {
            // ...
        }
        return params;
    }

    public static void extractParamNamesFromMap(Set<String> params, Map<?, ?> map) {
        extractParamNamesFromJson(params, JsonParser.toJsonElement(map));
    }

    public static void extractParamNamesFromJson(Set<String> params, JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            extractParamNamesFromJsonObject(params, jsonElement.getAsJsonObject());
        } else if (jsonElement.isJsonArray()) {
            extractParamNamesFromJsonArray(params, jsonElement.getAsJsonArray());
        }
    }

    public static void extractParamNamesFromJsonObject(Set<String> params, JsonObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            params.add(key); // 将当前层级的键添加到参数集合中
            JsonElement jsonElement = jsonObject.get(key);
            if (jsonElement.isJsonObject()) {
                extractParamNamesFromJsonObject(params, jsonElement.getAsJsonObject()); // 递归处理内部的JsonObject
            } else if (jsonElement.isJsonArray()) {
                extractParamNamesFromJsonArray(params, jsonElement.getAsJsonArray());
            }
        }
    }

    public static void extractParamNamesFromJsonArray(Set<String> params, JsonArray jsonArray) {
        for (JsonElement jsonElement : jsonArray) {
            if (jsonElement.isJsonObject()) {
                extractParamNamesFromJsonObject(params, jsonElement.getAsJsonObject()); // 递归处理内部的JsonObject
            } else if (jsonElement.isJsonArray()) {
                extractParamNamesFromJsonArray(params, jsonElement.getAsJsonArray());
            }
        }
    }

    public void merge(FuzzDict fuzzDict) {
        if (!host.equals(fuzzDict.getHost())) {
            throw new RuntimeException("Operation not allowed");
        }
        paths = mergeElement(paths, fuzzDict.getPaths());
        actions = mergeElement(actions, fuzzDict.getActions());
        params = mergeElement(params, fuzzDict.getParams());
        headers = mergeElement(headers, fuzzDict.getHeaders());
        cookies = mergeElement(cookies, fuzzDict.getCookies());
    }

    public static List<String> mergeElement(List<String> l1, List<String> l2) {
        Set<String> s1 = new HashSet<>();
        s1.addAll(l1);
        s1.addAll(l2);
        return s1.stream().toList();
    }
}
