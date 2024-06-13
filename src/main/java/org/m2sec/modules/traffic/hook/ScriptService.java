package org.m2sec.modules.traffic.hook;

import org.bouncycastle.util.encoders.Hex;
import org.m2sec.GalaxyMain;
import org.m2sec.common.Render;
import org.m2sec.common.crypto.CryptoUtil;
import org.m2sec.common.crypto.HashUtil;
import org.m2sec.common.crypto.MacUtil;
import org.m2sec.common.models.Request;
import org.m2sec.common.models.Response;
import org.m2sec.common.parsers.JsonParser;
import org.mvel2.integration.impl.MapVariableResolverFactory;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/6/12 20:01
 * @description:
 */
public class ScriptService extends AbstractHttpHookService {

    private MapVariableResolverFactory factory;

    @Override
    public void init() {
        init(
                GalaxyMain.config
                        .getHttpTrafficAutoModificationConfig()
                        .getHookConfig()
                        .getScriptPath());
    }

    public void init(String scriptPath) {
        factory =
                Render.compileScript(
                        scriptPath,
                        CryptoUtil.class,
                        HashUtil.class,
                        MacUtil.class,
                        JsonParser.class,
                        Base64.class,
                        Hex.class);
    }

    @Override
    public void destroy() {
        factory.clear();
    }

    @Override
    public Request hookRequestToBurp(Request request) {
        Map<String, Object> env = new HashMap<>();
        env.put("request", request);
        env.put("log", log);
        Render.callScriptFunction("hookRequestToBurp(request)", factory, env);
        return request;
    }

    @Override
    public Request hookRequestToServer(Request request) {
        Map<String, Object> env = new HashMap<>();
        env.put("request", request);
        env.put("log", log);
        Render.callScriptFunction("hookRequestToServer(request)", factory, env);
        return request;
    }

    @Override
    public Response hookResponseToBurp(Response response) {
        Map<String, Object> env = new HashMap<>();
        env.put("response", response);
        env.put("log", log);
        Render.callScriptFunction("hookResponseToBurp(response)", factory, env);
        return response;
    }

    @Override
    public Response hookResponseToClient(Response response) {
        Map<String, Object> env = new HashMap<>();
        env.put("response", response);
        env.put("log", log);
        Render.callScriptFunction("hookResponseToClient(response)", factory, env);
        return response;
    }
}
