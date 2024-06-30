package org.m2sec.modules.cloud.menu.checkers;

import lombok.AllArgsConstructor;
import org.m2sec.common.Tuple;
import org.m2sec.common.models.Request;
import org.m2sec.common.models.Response;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author: outlaws-bai
 * @date: 2024/6/30 20:46
 * @description:
 */
@AllArgsConstructor
public abstract class CloudStorageChecker {
    private String cloud;
    private String visitUrl;

    public Map<String, Method> getAllCheckerFunctions() {
        Map<String, Method> retVal = new LinkedHashMap<>();
        Method[] methods = this.getClass().getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            if (method.getName().startsWith("check")) {
                retVal.put(methodName.replace("check", ""), method);
            }
        }
        return retVal;
    }

    public Map<String, Tuple<Request, Response>> runAllCheckerFunctions(Request request, Map<String, Method> func) {
        Map<String, Tuple<Request, Response>> retVal = new HashMap<>();
        try {
            for (Map.Entry<String, Method> entry : func.entrySet()) {
                @SuppressWarnings("unchecked")
                Tuple<Request, Response> requestResponse = (Tuple<Request, Response>) entry.getValue().invoke(this,
                    request);
                if (requestResponse != null) retVal.put(entry.getKey(), requestResponse);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return retVal;
    }
}
