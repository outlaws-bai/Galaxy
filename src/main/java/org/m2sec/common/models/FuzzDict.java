package org.m2sec.common.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.m2sec.common.Tuple;
import org.m2sec.common.enums.HttpContentType;
import org.m2sec.common.parsers.JsonParser;
import org.m2sec.common.utils.HttpUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class FuzzDict {
    private String host;

    /** 所有的路径 */
    private List<String> paths;

    /** 路径的最后一段, 称之为action, 例如detail、list等 */
    private List<String> actions;

    /** 所有的参数名，包括query、json、form等 */
    private List<String> params;

    /** 所有的header头key */
    private List<String> headers;

    /** 所有cookie的key */
    private List<String> cookies;

    public static FuzzDict of(List<Tuple<Request, Response>> requestResponses) {
        String host = requestResponses.get(0).getFirst().getTarget().getHost();
        Set<String> paths =
                requestResponses.stream()
                        .map(Tuple::getFirst)
                        .map(Request::getPath)
                        .collect(Collectors.toSet());
        Set<String> actions =
                requestResponses.stream()
                        .map(Tuple::getFirst)
                        .map(FuzzDict::getAction)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
        Set<String> params =
                requestResponses.stream()
                        .flatMap(x -> getParams(x).stream())
                        .collect(Collectors.toSet());
        Set<String> headers =
                requestResponses.stream()
                        .map(Tuple::getFirst)
                        .flatMap(request -> request.getHeaders().stream())
                        .map(Header::getName)
                        .collect(Collectors.toSet());
        Set<String> cookies =
                requestResponses.stream()
                        .map(Tuple::getFirst)
                        .map(request -> request.getHeader("cookie"))
                        .filter(Objects::nonNull)
                        .map(
                                cookieHeader ->
                                        HttpUtil.convertCookieStrToMap(cookieHeader.getValue())
                                                .keySet())
                        .flatMap(Set::stream)
                        .collect(Collectors.toSet());
        return new FuzzDict(
                host,
                paths.stream().toList(),
                actions.stream().toList(),
                params.stream().toList(),
                headers.stream().toList(),
                cookies.stream().toList());
    }

    public static String getAction(Request request) {
        String path = HttpUtil.normalizePath(request.getPath());
        String[] pathParts = path.split("/");
        if (pathParts.length > 0) {
            return pathParts[pathParts.length - 1];
        }
        return null;
    }

    public static Set<String> getParams(Tuple<Request, Response> requestResponse) {
        Set<String> params = new HashSet<>();
        Request request = requestResponse.getFirst();
        Response response = requestResponse.getSecond();
        if (!request.getQueryStr().isEmpty()) {
            JsonObject queryJsonObject =
                    JsonParser.fromJsonStr(
                            HttpUtil.queryStrToJsonStr(request.getQueryStr()), JsonObject.class);
            updateParams(params, queryJsonObject);
        }
        HttpContentType httpContentType = request.getContentType();
        try {
            if (httpContentType == HttpContentType.FORM) {
                JsonObject formJsonObject =
                        JsonParser.fromJsonStr(
                                HttpUtil.queryStrToJsonStr(new String(request.getContent())),
                                JsonObject.class);
                updateParams(params, formJsonObject);
            } else if (httpContentType == HttpContentType.JSON) {
                JsonObject bodyJsonObject =
                        JsonParser.fromJsonStr(new String(request.getContent()), JsonObject.class);
                updateParams(params, bodyJsonObject);
            }
        } catch (Exception e) {
            // ...
        }
        try {
            if (response != null) {
                JsonObject bodyJsonObject =
                        JsonParser.fromJsonStr(new String(response.getContent()), JsonObject.class);
                updateParams(params, bodyJsonObject);
            }
        } catch (Exception e) {
            // ...
        }
        return params;
    }

    public static void updateParams(Set<String> params, JsonObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            params.add(key); // 将当前层级的键添加到参数集合中
            JsonElement element = jsonObject.get(key);
            if (element.isJsonObject()) {
                updateParams(params, element.getAsJsonObject()); // 递归处理内部的JsonObject
            } else if (element.isJsonArray()) {
                element.getAsJsonArray()
                        .forEach(
                                x -> {
                                    if (x.isJsonObject()) {
                                        updateParams(params, x.getAsJsonObject());
                                    }
                                });
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
