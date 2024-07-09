package org.m2sec.core.common;

import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class YamlParser {

    private static final Yaml yaml = new Yaml();

    public static String toYamlStr(Object obj) {
        return yaml.dumpAsMap(obj);
    }

    public static <T> T fromYamlStr(String yamlStr, Class<T> clazz) {
        return JsonParser.fromJsonStr(JsonParser.toJsonStr(yaml.loadAs(yamlStr, Map.class)), clazz);
    }

    public static <T> T fromYamlStr(String yamlStr, Type type) {
        return JsonParser.fromJsonStr(JsonParser.toJsonStr(yaml.loadAs(yamlStr, Map.class)), type);
    }
}
