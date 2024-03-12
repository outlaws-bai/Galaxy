package org.m2sec.common.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.m2sec.common.Log;
import org.m2sec.common.Tuple;
import org.m2sec.common.enums.HttpContentType;
import org.m2sec.common.enums.HttpMethod;
import org.m2sec.common.models.ApiInfo;

import java.util.*;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class SwaggerUtil {
    private static final Log log = new Log(SwaggerUtil.class);

    public static List<ApiInfo> extractApiInfoFromSwagger(String docs) {
        JsonObject jsonObject = JsonParser.parseString(docs).getAsJsonObject();
        if (jsonObject.has("swagger") && jsonObject.get("swagger").getAsString().startsWith("2")) {
            return extractApiInfoFromSwaggerV2(jsonObject);
        } else if (jsonObject.has("openapi")
                && jsonObject.get("openapi").getAsString().startsWith("3")) {
            return extractApiInfoFromSwaggerV3(jsonObject);
        } else {
            throw new RuntimeException("Unsupported doc content.");
        }
    }

    private static List<ApiInfo> extractApiInfoFromSwaggerV2(JsonObject swaggerObject) {
        log.debug("try extract api info from swagger 2.0");
        ArrayList<ApiInfo> apiInfoList = new ArrayList<>();
        ApiInfo.Version version = ApiInfo.Version.V2;
        Map<String, String> tags = new HashMap<>();
        if (swaggerObject.has("tags") && swaggerObject.get("tags").isJsonArray()) {
            for (JsonElement jsonElement : swaggerObject.get("tags").getAsJsonArray()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                tags.put(
                        jsonObject.has("name") ? jsonObject.get("name").getAsString() : "",
                        jsonObject.has("description")
                                ? jsonObject.get("description").getAsString()
                                : "");
            }
        }
        if (swaggerObject.has("paths") && swaggerObject.get("paths") != null) {
            JsonObject pathsObject = swaggerObject.getAsJsonObject("paths");

            for (String path : pathsObject.keySet()) {
                JsonObject pathObject = pathsObject.getAsJsonObject(path);

                for (String method : pathObject.keySet()) {
                    JsonObject methodObject = pathObject.getAsJsonObject(method);
                    if (methodObject != null) {
                        HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());
                        Tuple<List<ApiInfo.Param>, Map<String, Object>> temp =
                                extractParamsAndRequestBody(methodObject, version);
                        // 处理params
                        List<ApiInfo.Param> params = temp.getFirst();
                        // 处理requestBody
                        Map<String, Object> requestBody = temp.getSecond();
                        // 处理notes
                        LinkedHashMap<String, String> notes = new LinkedHashMap<>();
                        if (methodObject.has("tags") && methodObject.get("tags").isJsonArray()) {
                            JsonArray methodTagNames = methodObject.get("tags").getAsJsonArray();
                            for (int i = 0; i < methodTagNames.size(); i++) {
                                String tagName = methodTagNames.get(i).getAsString();
                                notes.put("tag-name-" + i, tagName);
                                notes.put("tag-description-" + i, tags.getOrDefault(tagName, ""));
                            }
                        }
                        // 处理content type
                        HttpContentType contentType;
                        if (httpMethod == HttpMethod.GET
                                || httpMethod == HttpMethod.HEAD
                                || httpMethod == HttpMethod.OPTIONS) {
                            contentType = HttpContentType.NON_BODY;
                        } else {
                            if (methodObject.has("consumes")
                                    && methodObject.get("consumes") != null
                                    && !methodObject.getAsJsonArray("consumes").isEmpty()) {
                                contentType =
                                        HttpContentType.of(
                                                methodObject
                                                        .get("consumes")
                                                        .getAsJsonArray()
                                                        .get(0)
                                                        .getAsString());
                            } else {
                                contentType = HttpContentType.FORM;
                            }
                        }
                        ApiInfo apiInfo =
                                new ApiInfo(
                                        ApiInfo.Version.V2,
                                        httpMethod,
                                        path,
                                        contentType,
                                        params,
                                        requestBody,
                                        notes);
                        apiInfoList.add(apiInfo);
                    }
                }
            }
        }

        return apiInfoList;
    }

    private static List<ApiInfo> extractApiInfoFromSwaggerV3(JsonObject swaggerObject) {
        log.debug("try extract api info from swagger 3.0");
        ArrayList<ApiInfo> apiInfoList = new ArrayList<>();
        ApiInfo.Version version = ApiInfo.Version.V3;
        if (swaggerObject.has("paths") && swaggerObject.get("paths") != null) {
            JsonObject pathsObject = swaggerObject.getAsJsonObject("paths");

            for (String path : pathsObject.keySet()) {
                JsonObject pathObject = pathsObject.getAsJsonObject(path);

                for (String method : pathObject.keySet()) {
                    JsonObject methodObject = pathObject.getAsJsonObject(method);
                    if (methodObject != null) {
                        HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());
                        Tuple<List<ApiInfo.Param>, Map<String, Object>> temp =
                                extractParamsAndRequestBody(methodObject, version);
                        // 处理params
                        List<ApiInfo.Param> params = temp.getFirst();
                        // 处理requestBody
                        Map<String, Object> requestBody = temp.getSecond();
                        // 处理notes
                        LinkedHashMap<String, String> notes = new LinkedHashMap<>();
                        if (methodObject.has("tags")) {
                            notes.put("tags", methodObject.get("tags").toString());
                        }
                        if (methodObject.has("summary")) {
                            notes.put("summary", methodObject.get("summary").getAsString());
                        }
                        if (methodObject.has("operationId")) {
                            notes.put("operationId", methodObject.get("operationId").getAsString());
                        }
                        // 处理content type
                        HttpContentType contentType;
                        if (httpMethod == HttpMethod.GET || httpMethod == HttpMethod.HEAD) {
                            contentType = HttpContentType.NON_BODY;
                        } else {
                            if (methodObject.has("requestBody")) {
                                contentType =
                                        HttpContentType.of(
                                                methodObject
                                                        .get("requestBody")
                                                        .getAsJsonObject()
                                                        .get("content")
                                                        .getAsJsonObject()
                                                        .keySet()
                                                        .iterator()
                                                        .next());
                            } else {
                                contentType = HttpContentType.FORM;
                            }
                        }
                        ApiInfo apiInfo =
                                new ApiInfo(
                                        version,
                                        httpMethod,
                                        path,
                                        contentType,
                                        params,
                                        requestBody,
                                        notes);
                        apiInfoList.add(apiInfo);
                    }
                }
            }
        }

        return apiInfoList;
    }

    private static Tuple<List<ApiInfo.Param>, Map<String, Object>> extractParamsAndRequestBody(
            JsonObject methodObject, ApiInfo.Version version) {
        List<ApiInfo.Param> params = new ArrayList<>();
        Map<String, Object> requestBody = new HashMap<>();
        if (methodObject.has("parameters") && methodObject.get("parameters").isJsonArray()) {
            JsonArray parametersArray = methodObject.getAsJsonArray("parameters");
            for (JsonElement paramElement : parametersArray) {
                JsonObject paramObject = paramElement.getAsJsonObject();
                String name = paramObject.get("name").getAsString();
                Object value = getDefaultValue(paramObject, version);
                String position =
                        paramObject.has("in")
                                        && !paramObject.get("in").isJsonNull()
                                        && !paramObject.get("in").getAsString().isEmpty()
                                ? paramObject.get("in").getAsString()
                                : "query";
                if (!"body".equals(position)) {
                    params.add(new ApiInfo.Param(name, value, position));
                }
            }
        }
        return new Tuple<>(params, requestBody);
    }

    private static Object getDefaultValue(JsonObject paramObject, ApiInfo.Version version) {
        if (paramObject.has("default")) {
            return paramObject.get("default").getAsString();
        } else if (version == ApiInfo.Version.V2 && paramObject.has("type")) {
            return getDefault(paramObject.get("type").getAsString());
        } else if (version == ApiInfo.Version.V3
                && paramObject.has("schema")
                && !paramObject.get("schema").isJsonNull()) {
            JsonObject schemaObject = paramObject.getAsJsonObject("schema");
            if (schemaObject.has("default")) {
                return schemaObject.get("default").getAsString();
            } else if (schemaObject.has("type")) {
                return getDefault(schemaObject.get("type").getAsString());
            } else {
                return "1";
            }
        } else {
            return "1";
        }
    }

    private static Object getDefault(String type) {
        if (type == null) {
            return "1";
        }
        switch (type) {
            case "number", "integer" -> {
                return 1;
            }
            case "boolean" -> {
                return true;
            }
            case "array" -> {
                return new ArrayList<>();
            }
            case "object" -> {
                return new HashMap<>();
            }
                // "string","date","dateTime","file"...
            default -> {
                return "1";
            }
        }
    }
}
