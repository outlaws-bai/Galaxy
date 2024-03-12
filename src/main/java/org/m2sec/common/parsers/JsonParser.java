package org.m2sec.common.parsers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class JsonParser {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String toJsonStr(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJsonStr(String jsonStr, Class<T> clazz) {
        return gson.fromJson(jsonStr, clazz);
    }

    public static <T> T fromMap(Map<String, Object> map, Class<T> clazz) {
        return fromJsonStr(toJsonStr(map), clazz);
    }
}
