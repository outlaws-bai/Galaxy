package org.m2sec.core.common;

import org.apache.commons.text.StringSubstitutor;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

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

    public static Object renderExpression2(String expression, @Nullable Map<String, Object> env, Class<?>... classes) {
        try(Context rhinoContext = Context.enter()) {
            // 初始化脚本上下文
            Scriptable scope = rhinoContext.initStandardObjects();
            String importTemplate = "var %s = Java.type('%s')";

            // put params
            if (env != null) {
                for (Map.Entry<String, Object> entry : env.entrySet()) {
                    Object wrappedJavaVariable = Context.javaToJS(entry.getValue(), scope);
                    ScriptableObject.putProperty(scope, entry.getKey(), wrappedJavaVariable);
                }
            }

            // import classes
            for (Class<?> clazz : classes) {
                String importCode = String.format(importTemplate, clazz.getSimpleName(), clazz.getName());
                rhinoContext.evaluateString(scope, importCode, "<import>", 1, null);
            }
            return rhinoContext.evaluateString(scope, expression, "<render>", 1, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
