package org.m2sec.modules.traffic.decorate;

import org.m2sec.common.Render;
import org.m2sec.common.models.Request;
import org.m2sec.common.models.Response;

import java.util.HashMap;

/**
 * @author: outlaws-bai
 * @date: 2024/6/7 14:32
 * @description:
 */
public class DecorateService {

    public static Request decorateRequest(Request request, String decorateConfig) {
        HashMap<String, Object> env = new HashMap<>();
        env.put("request", request);
        Render.renderExpression(decorateConfig, env);
        return request;
    }

    public static Response decorateResponse(Response response, String decorateConfig) {
        HashMap<String, Object> env = new HashMap<>();
        env.put("response", response);
        Render.renderExpression(decorateConfig, env);
        return response;
    }
}
