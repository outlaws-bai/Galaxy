package org.m2sec.common.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.m2sec.common.Constants;
import org.m2sec.common.Log;
import org.m2sec.common.enums.ContentType;
import org.m2sec.common.enums.Method;
import org.m2sec.common.models.ApiInfo;
import org.m2sec.common.models.Headers;
import org.m2sec.common.models.UploadFile;
import org.m2sec.common.parsers.JsonParser;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class SwaggerUtil {
    private static final Log log = new Log(SwaggerUtil.class);

    public static List<ApiInfo> extractApiInfoFromSwagger(String docs) {
        JsonObject jsonObject = JsonParser.fromJsonStr(docs, JsonObject.class);
        String swaggerVersion = jsonObject.has("swagger") ? jsonObject.get("swagger").getAsString() : null;
        String openApiVersion = jsonObject.has("openapi") ? jsonObject.get("openapi").getAsString() : null;
        if (swaggerVersion != null) {
            if (swaggerVersion.startsWith("2")) return extractV2(jsonObject);
            else return extractV3(jsonObject);
        } else if (openApiVersion != null) {
            if (openApiVersion.startsWith("2")) return extractV2(jsonObject);
            else return extractV3(jsonObject);
        } else {
            throw new RuntimeException("Unsupported doc content.");
        }
    }

    private static List<ApiInfo> extractV2(JsonObject swaggerObject) {
        log.debug("try extract api info from swagger 2.0");
        ArrayList<ApiInfo> apiInfoList = new ArrayList<>();
        ApiInfo.Version version = ApiInfo.Version.V2;
        // 创建tags, 用于向ApiInfo传入一些提示信息.
        Map<String, String> tags = new HashMap<>();
        if (swaggerObject.has("tags") && swaggerObject.get("tags").isJsonArray()) {
            for (JsonElement jsonElement : swaggerObject.get("tags").getAsJsonArray()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                tags.put(jsonObject.has("name") ? jsonObject.get("name").getAsString() : "", jsonObject.has(
                    "description") ? jsonObject.get("description").getAsString() : "");
            }
        }
        // 获取引用对象
        JsonObject refs = swaggerObject.has("definitions") ? swaggerObject.getAsJsonObject("definitions") :
            new JsonObject();
        // 解析path结构
        if (swaggerObject.has("paths") && swaggerObject.get("paths") != null) {
            JsonObject pathsObject = swaggerObject.getAsJsonObject("paths");

            for (String path : pathsObject.keySet()) {
                JsonObject pathObject = pathsObject.getAsJsonObject(path);
                // 解析method结构
                for (String methodStr : pathObject.keySet()) {
                    JsonObject methodObject = pathObject.getAsJsonObject(methodStr);
                    if (methodObject != null) {
                        Method method = Method.valueOf(methodStr.toUpperCase());
                        // 获取ContentType
                        String contentTypeHeaderValue = null;
                        if (methodObject.has("consumes") && methodObject.get("consumes") != null && !methodObject.getAsJsonArray("consumes").isEmpty())
                            contentTypeHeaderValue = methodObject.getAsJsonArray("consumes").get(0).getAsString();
                        ContentType contentType = HttpUtil.getContentType(methodStr, contentTypeHeaderValue);
                        ApiInfo apiInfo = new ApiInfo(version, method, path, contentType);
                        apiInfoList.add(apiInfo);
                        // 处理notes
                        if (methodObject.has("tags") && methodObject.get("tags").isJsonArray()) {
                            JsonArray methodTagNames = methodObject.getAsJsonArray("tags");
                            for (int i = 0; i < methodTagNames.size(); i++) {
                                String tagName = methodTagNames.get(i).getAsString();
                                apiInfo.getNotes().put("tag-name-" + i, tagName);
                                apiInfo.getNotes().put("tag-description-" + i, tags.getOrDefault(tagName, ""));
                            }
                        }
                        if (methodObject.has("summary") && methodObject.get("summary") != null)
                            apiInfo.getNotes().put("summary", methodObject.get("summary").getAsString());
                        if (methodObject.has("description") && methodObject.get("description") != null)
                            apiInfo.getNotes().put("description", methodObject.get("description").getAsString());
                        if (methodObject.has("operationId") && methodObject.get("operationId") != null)
                            apiInfo.getNotes().put("operationId", methodObject.get("operationId").getAsString());
                        // 处理参数
                        if (methodObject.has("parameters"))
                            methodObject.getAsJsonArray("parameters").forEach((parameter) -> updateApiInfoByParameters(apiInfo, parameter.getAsJsonObject(), refs));
                    }
                }
            }
        }

        return apiInfoList;
    }

    private static List<ApiInfo> extractV3(JsonObject swaggerObject) {
        log.debug("try extract api info from swagger 3.0");
        ArrayList<ApiInfo> apiInfoList = new ArrayList<>();
        ApiInfo.Version version = ApiInfo.Version.V3;
        // 获取引用对象
        JsonObject refs = swaggerObject.has("components") ? swaggerObject.getAsJsonObject("components") :
            new JsonObject();
        // 解析path结构
        if (swaggerObject.has("paths") && swaggerObject.get("paths") != null) {
            JsonObject pathsObject = swaggerObject.getAsJsonObject("paths");
            for (String path : pathsObject.keySet()) {
                JsonObject pathObject = pathsObject.getAsJsonObject(path);
                // 解析method结构
                for (String methodStr : pathObject.keySet()) {
                    JsonObject methodObject = pathObject.getAsJsonObject(methodStr);
                    if (methodObject != null) {
                        Method method = Method.valueOf(methodStr.toUpperCase());
                        // 获取ContentType
                        String contentTypeHeaderValue = null;
                        JsonObject requestBodyJsonObject = methodObject.has("requestBody") ?
                            methodObject.getAsJsonObject("requestBody") : new JsonObject();
                        JsonObject contentJsonObject = requestBodyJsonObject.has("content") ?
                            requestBodyJsonObject.getAsJsonObject("content") : new JsonObject();
                        if (!contentJsonObject.isJsonNull()) {
                            if (contentJsonObject.keySet().contains("application/json"))
                                contentTypeHeaderValue = "application/json";
                            else if (!contentJsonObject.keySet().isEmpty())
                                contentTypeHeaderValue = contentJsonObject.keySet().iterator().next();
                        }
                        ContentType contentType = HttpUtil.getContentType(methodStr, contentTypeHeaderValue);
                        ApiInfo apiInfo = new ApiInfo(version, method, path, contentType);
                        apiInfoList.add(apiInfo);
                        // 处理notes
                        if (methodObject.has("tags") && methodObject.get("tags") != null) {
                            apiInfo.getNotes().put("tags", methodObject.getAsJsonArray("tags").toString());
                        }
                        if (methodObject.has("summary") && methodObject.get("summary") != null) {
                            apiInfo.getNotes().put("summary", methodObject.get("summary").getAsString());
                        }
                        if (methodObject.has("operationId") && methodObject.get("operationId") != null) {
                            apiInfo.getNotes().put("operationId", methodObject.get("operationId").getAsString());
                        }
                        // 处理参数
                        if (methodObject.has("parameters"))
                            methodObject.getAsJsonArray("parameters").forEach((parameter) -> updateApiInfoByParameters(apiInfo, parameter.getAsJsonObject(), refs));
                        // 处理requestBody
                        if (contentTypeHeaderValue != null) {
                            if (contentJsonObject.has(contentTypeHeaderValue) && contentJsonObject.get(contentTypeHeaderValue).isJsonObject()) {
                                JsonObject bodyJsonObject = contentJsonObject.getAsJsonObject(contentTypeHeaderValue);
                                updateApiInfoByRequestBody(apiInfo, bodyJsonObject, refs);
                            }
                        }
                    }
                }
            }
        }

        return apiInfoList;
    }

    private static void updateApiInfoByParameters(ApiInfo apiInfo, JsonObject parameter, JsonObject refs) {
        String position = parameter.has("in") ? parameter.get("in").getAsString() : null;
        String name = parameter.has("name") ? parameter.get("name").getAsString() : null;
        String description = parameter.has("description") ? parameter.get("description").getAsString() : null;
        String type = parameter.has("type") ? parameter.get("type").getAsString() : null;
        if (type == null && parameter.has("schema") && parameter.get("schema") != null && apiInfo.getVersion() == ApiInfo.Version.V3) {
            JsonObject schemaJsonObject = parameter.getAsJsonObject("schema");
            type = schemaJsonObject.has("type") ? schemaJsonObject.get("type").getAsString() : null;
        }
        if (position == null || name == null) return;
        switch (position) {
            case "path" ->
                apiInfo.setPath(apiInfo.getPath().replace("{" + name + "}", getValueByType(type).toString()));
            case "query" -> apiInfo.getQuery().add(name, getValueByType(type).toString());
            case "header" -> apiInfo.getHeaders().add(name, getValueByType(type).toString());
            case "cookie" -> apiInfo.getCookies().add(name, getValueByType(type).toString());
            case "formData" -> { // swagger formData包含form和form_data
                if (apiInfo.getContentType().equals(ContentType.FORM)) {
                    apiInfo.getForm().add(name, getValueByType(type).toString());
                } else if (apiInfo.getContentType().equals(ContentType.FORM_DATA)) {
                    if ("file".equals(type)) {
                        apiInfo.getFiles().add(name, (UploadFile) getValueByType(type));
                    } else {
                        apiInfo.getFormDatas().add(name, getValueByType(type).toString());
                    }
                }
            }
            case "body" -> updateApiInfoByRequestBody(apiInfo, parameter, refs);
        }
        if (description != null)
            apiInfo.getNotes().put("parameter-description-" + position + "-" + name + "-" + type, description);
    }

    private static void updateApiInfoByRef(ApiInfo apiInfo, JsonObject jsonObject, JsonObject refs, boolean isArray) {
        String refStr = jsonObject.has("$ref") ? jsonObject.get("$ref").getAsString() : null;
        if (refStr != null) {
            JsonObject model = getRefModel(apiInfo.getVersion(), refStr, refs);
            if (model != null) {
                if (isArray) {
                    apiInfo.getRequestBody2().add(getRequestByModel(apiInfo, model, refs, refStr));
                } else {
                    apiInfo.setRequestBody(getRequestByModel(apiInfo, model, refs, refStr));
                }
            }
        }
    }

    private static HashMap<String, Object> getRequestByModel(ApiInfo apiInfo, JsonObject model, JsonObject refs,
                                                             String key) {
        HashMap<String, Object> retVal = new HashMap<>();
        String type = model.has("type") ? model.get("type").getAsString() : null;
        if (type == null || !type.equalsIgnoreCase("object")) return retVal;
        JsonObject properties = model.has("properties") ? model.get("properties").getAsJsonObject() : null;
        if (properties == null) return retVal;
        for (String propertyName : properties.keySet()) {
            JsonObject property = properties.getAsJsonObject(propertyName);
            String refStr = property.has("$ref") ? property.get("$ref").getAsString() : null;
            if (refStr != null) {
                JsonObject subModel = getRefModel(apiInfo.getVersion(), refStr, refs);
                if (subModel != null) retVal.put(propertyName, getRequestByModel(apiInfo, subModel, refs, refStr));
            } else {
                JsonArray enums = property.has("enum") ? property.get("enum").getAsJsonArray() : null;
                String description = property.has("description") ? property.get("description").getAsString() : null;
                String subType = property.has("type") ? property.get("type").getAsString() : null;
                String example = property.has("example") ? property.get("example").getAsString() : null;
                if (description != null)
                    apiInfo.getNotes().put("model-description-body-" + key + "-" + subType, description);
                if (enums != null) {
                    retVal.put(propertyName, enums.get(0));
                    continue;
                }
                if (example != null) {
                    retVal.put(propertyName, example);
                    continue;
                }
                retVal.put(propertyName, getValueByType(subType));
            }
        }
        return retVal;
    }

    private static void updateApiInfoByRequestBody(ApiInfo apiInfo, JsonObject requestBody, JsonObject refs) {
        if (requestBody.has("schema") && requestBody.get("schema").isJsonObject()) {
            JsonObject schemaObject = requestBody.getAsJsonObject("schema");
            String schemaType = schemaObject.has("type") ? schemaObject.get("type").getAsString() : null;
            if (schemaType == null) {
                updateApiInfoByRef(apiInfo, schemaObject, refs, false);
            } else if (schemaType.equals("array")) {
                JsonObject itemsObject = schemaObject.has("items") ? schemaObject.getAsJsonObject("items") : null;
                if (itemsObject != null) {
                    updateApiInfoByRef(apiInfo, itemsObject, refs, true);
                }
            }
        }
    }

    private static JsonObject getRefModel(ApiInfo.Version version, String refStr, JsonObject refs) {
        String[] parts = refStr.split("/");
        if (version == ApiInfo.Version.V2) {
            String key = parts[parts.length - 1];
            return refs.has(key) ? refs.get(key).getAsJsonObject() : null;
        } else {
            if (parts.length <= 2) return null;
            JsonObject temp = refs;
            for (int i = 2; i < parts.length; i++) {
                String key = parts[i];
                if (temp.has(key) && temp.get(key).isJsonObject()) {
                    temp = temp.getAsJsonObject(key);
                }
            }
            return temp;
        }
    }

    private static Object getValueByType(@Nullable String type) {
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
                return new ArrayList<>(List.of("1"));
            }
            case "object" -> {
                return new HashMap<>(Map.of("key", "value"));
            }
            case "date", "dateTime" -> {
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return now.format(formatter);
            }
            case "file" -> {
                Headers headers = new Headers();
                headers.putAll(new HashMap<>(Map.of(Constants.HTTP_HEADER_CONTENT_TYPE,
                    new ArrayList<>(List.of("image/jpg")))));
                return new UploadFile("a.jpg", headers, new byte[]{0x61});
            }
            default -> { // "string"
                return "1";
            }
        }
    }
}
