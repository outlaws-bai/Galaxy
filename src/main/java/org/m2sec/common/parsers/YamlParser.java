package org.m2sec.common.parsers;

import org.yaml.snakeyaml.Yaml;

import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
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
}
