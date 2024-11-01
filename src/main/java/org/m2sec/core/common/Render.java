package org.m2sec.core.common;

import org.apache.commons.text.StringSubstitutor;
import org.mvel2.MVEL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class Render {

    public static String renderTemplate(String template, Map<String, Object> env, Class<?>... classes) {
        StringSubstitutor stringSubstitutor = new StringSubstitutor(key -> (String) renderExpression(key, env,
            classes));
        return stringSubstitutor.replace(template);
    }

    public static Object renderExpression(String expression, Map<String, Object> env, Class<?>... classes) {
        if (env == null) env = new HashMap<>();
        for (Class<?> clz : classes) {
            env.put(clz.getSimpleName(), clz);
        }
        return MVEL.eval(expression, env);
    }
}
