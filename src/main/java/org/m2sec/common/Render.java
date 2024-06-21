package org.m2sec.common;

import org.apache.commons.text.StringSubstitutor;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ExpressionEvaluator;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

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
            ExpressionEvaluator evaluator = new ExpressionEvaluator();
            evaluator.setParentClassLoader(Render.class.getClassLoader());
            evaluator.setDefaultImports(Arrays.stream(classes).map(Class::getName).toArray(String[]::new));
            if (env == null) {
                evaluator.cook(expression);
                return evaluator.evaluate();
            }
            List<String> keyList = new ArrayList<>();
            List<Object> valueList = new ArrayList<>();
            List<Class<?>> valueClassList = new ArrayList<>();
            for (Map.Entry<String, Object> entry : env.entrySet()) {
                keyList.add(entry.getKey());
                valueList.add(entry.getValue());
                valueClassList.add(entry.getValue().getClass());
            }
            evaluator.setParameters(keyList.toArray(new String[0]), valueClassList.toArray(new Class<?>[0]));
            evaluator.cook(expression);
            return evaluator.evaluate(valueList.toArray());
        } catch (InvocationTargetException | CompileException e) {
            throw new RuntimeException(e);
        }
    }
}
