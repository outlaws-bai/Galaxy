package org.m2sec.core.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class JsonUtil {
    private static final Gson gson = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.BIG_DECIMAL).disableHtmlEscaping().create();

    public static String toJsonStr(Object obj) {
        return gson.toJson(obj);
    }

    public static JsonElement toJsonElement(Object obj) {
        return gson.toJsonTree(obj);
    }

    public static <T> T fromJsonStr(String jsonStr, Class<T> clazz) {
        return gson.fromJson(jsonStr, clazz);
    }

    public static <T> T fromJsonStr(String jsonStr, Type type) {
        return gson.fromJson(jsonStr, type);
    }

    public static Map<?, ?> jsonStrToMap(String jsonStr) {
        if (jsonStr == null || jsonStr.isBlank()) return new HashMap<>();
        return fromJsonStr(jsonStr, HashMap.class);
    }

    public static List<?> jsonStrToList(String jsonStr) {
        if (jsonStr == null || jsonStr.isBlank()) return new ArrayList<>();
        return fromJsonStr(jsonStr, ArrayList.class);
    }

    public static JsonElement toJson(String jsonStr) {
        return JsonParser.parseString(jsonStr);
    }

    public static Map<String, Object> toMap(JsonElement jsonElement) {
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        return gson.fromJson(jsonElement, mapType);
    }

    public static List<Object> toList(JsonElement jsonElement) {
        Type listType = new TypeToken<List<Object>>() {
        }.getType();
        return gson.fromJson(jsonElement, listType);
    }

    public static <T> T mapToObject(Map<?, ?> map, Class<T> clazz) {
        return gson.fromJson(gson.toJson(map), clazz);
    }
}
