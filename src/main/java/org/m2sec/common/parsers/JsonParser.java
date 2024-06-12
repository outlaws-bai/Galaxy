package org.m2sec.common.parsers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class JsonParser {
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

    public static Map<?, ?> jsonStrToMap(String jsonStr) {
        return fromJsonStr(jsonStr, HashMap.class);
    }
}
