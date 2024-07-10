package org.m2sec.core.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

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
    private static final Gson gson = new GsonBuilder().create();

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
        return fromJsonStr(jsonStr, HashMap.class);
    }

    public static List<?> jsonStrToList(String jsonStr) {
        return fromJsonStr(jsonStr, ArrayList.class);
    }
}
