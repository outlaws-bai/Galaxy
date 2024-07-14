package org.m2sec.core.common;

import org.apache.commons.text.StringSubstitutor;
import javax.annotation.Nullable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class Render {

    public static String renderTemplate(String template, @Nullable Map<String, Object> env, Class<?>... classes) {
        StringSubstitutor stringSubstitutor = new StringSubstitutor(key -> (String) renderExpression(key, env,
            classes));
        return stringSubstitutor.replace(template);
    }

    public static Object renderExpression(String expression, @Nullable Map<String, Object> env, Class<?>... classes) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName(Constants.JAVA_SCRIPT_ENGINE_NAME);
            String importTemplate = "var %s = Java.type('%s')";

            // put params
            if (env != null) {
                for (Map.Entry<String, Object> entry : env.entrySet()) {
                    engine.put(entry.getKey(), entry.getValue());
                }
            }

            // import classes
            for (Class<?> clazz : classes) {
                String importCode = String.format(importTemplate, clazz.getSimpleName(), clazz.getName());
                engine.eval(importCode);
            }
            return engine.eval(expression);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
