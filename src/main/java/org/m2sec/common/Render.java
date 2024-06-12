package org.m2sec.common;

import org.m2sec.common.utils.FileUtil;
import org.mvel2.MVEL;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.templates.TemplateRuntime;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/6/5 19:42
 * @description:
 */
public class Render {

    public static String renderTemplate(
            String template, Map<String, Object> env, Class<?>... classes) {
        patchEnv(env, classes);
        return (String) TemplateRuntime.eval(template, env);
    }

    public static Object renderExpression(
            String expression, Map<String, Object> env, Class<?>... classes) {
        patchEnv(env, classes);
        return MVEL.eval(expression, env);
    }

    public static MapVariableResolverFactory compileScript(String scriptPath, Class<?>... classes) {
        HashMap<String, Object> env = new HashMap<>();
        patchEnv(env, classes);
        MapVariableResolverFactory factory = new MapVariableResolverFactory(env);
        MVEL.eval(FileUtil.readFileAsString(scriptPath), factory);
        return factory;
    }

    public static Object callScriptFunction(
            String expression,
            MapVariableResolverFactory factory,
            Map<String, Object> env,
            Class<?>... classes) {
        patchEnv(env, classes);
        return MVEL.eval(expression, env, factory);
    }

    public static Object compileScriptAndCallFunction(
            String scriptPath, String expression, Map<String, Object> env, Class<?>... classes) {
        patchEnv(env, classes);
        MapVariableResolverFactory factory = new MapVariableResolverFactory(env);
        MVEL.eval(FileUtil.readFileAsString(scriptPath), factory);
        return MVEL.eval(expression, factory);
    }

    private static void patchEnv(Map<String, Object> env, Class<?>... classes) {
        for (Class<?> clz : classes) {
            env.put(clz.getSimpleName(), clz);
        }
    }
}
