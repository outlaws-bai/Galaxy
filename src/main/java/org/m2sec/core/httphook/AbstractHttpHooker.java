package org.m2sec.core.httphook;

import burp.api.montoya.http.handler.HttpRequestToBeSent;
import burp.api.montoya.http.handler.HttpResponseReceived;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.InterceptedResponse;
import lombok.extern.slf4j.Slf4j;
import org.m2sec.core.common.Config;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.Render;
import org.m2sec.core.enums.HttpHookWay;
import org.m2sec.core.models.Request;
import org.m2sec.core.models.Response;
import org.m2sec.core.utils.HttpUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Slf4j
public abstract class AbstractHttpHooker {
    static final HashSet<Integer> hookedIds = new HashSet<>();

    protected static Config config;


    public abstract void init(Config config);

    public abstract void destroy();


    public static AbstractHttpHooker getHooker(Config config1) {
        config = config1;
        if (config.getCacheOption().isHookStart()) {
            AbstractHttpHooker hooker;
            if (config.getCacheOption().getHookWay().equals(HttpHookWay.GRPC)) {
                hooker = new GRpcHooker();
            } else if (config.getCacheOption().getHookWay().equals(HttpHookWay.JAVA)) {
                hooker = new JavaFileHooker();
            } else {
                throw new RuntimeException("hookService is error! please choose RPC or JAVA");
            }
            log.info("HTTP Hooker - {} start. ", config.getCacheOption().getHookWay());
            hooker.init(config);
            return hooker;
        } else {
            throw new RuntimeException("Hook is not start");
        }
    }

    public HttpRequest tryHookRequestToBurp(InterceptedRequest httpRequest) {
        String name = "hookRequestToBurp";
        HttpRequest retVal = httpRequest;
        try {
            if (HttpUtil.isCorrectUrl(httpRequest.url()) && config.getCacheOption().isHookRequest()) {
                Request request = Request.of(httpRequest).normalize();
                log.debug("[{}] before hook: {}", name, request);
                String expression = config.getCacheOption().getRequestCheckExpression();
                if (expression != null && !expression.isEmpty() && (Boolean) Render.renderExpression(expression,
                    new HashMap<>(Map.of("request", request)))) {
                    request = hookRequestToBurp(request);
                    log.debug("[{}] after hook: {}", name, request);
                    if (request == null) {
                        return retVal;
                    }
                    // 添加标记头
                    request.getHeaders().put(Constants.HTTP_HEADER_HOOK_HEADER_KEY, "HttpHook");
                    log.debug("exec method: {} with {} success.", name, this.getClass().getSimpleName());
                    retVal = request.toBurp();
                }
            }
        } catch (Exception e) {
            log.error("hookRequestToBurp execute error.", e);
        }
        return retVal;
    }

    /**
     * 该函数在请求从Burp发送到服务端时被调用
     */
    public HttpRequest tryHookRequestToServer(HttpRequestToBeSent httpRequest) {
        String name = "hookRequestToServer";
        HttpRequest retVal = httpRequest;
        try {
            if (HttpUtil.isCorrectUrl(httpRequest.url()) && httpRequest.hasHeader(Constants.HTTP_HEADER_HOOK_HEADER_KEY)) {
                Request request = Request.of(httpRequest).normalize();
                log.debug("[{}] before hook: {}", name, request);
                request.getHeaders().remove(Constants.HTTP_HEADER_HOOK_HEADER_KEY);
                request = hookRequestToServer(request);
                log.debug("[{}] after hook: {}", name, request);
                // 添加标记头
                if (config.getCacheOption().isHookResponse()) {
                    hookedIds.add(httpRequest.messageId());
                }
                if (request == null) {
                    return retVal;
                }
                log.debug("exec method: hookRequestToServer with {} success.", this.getClass().getSimpleName());
                retVal = request.toBurp();
            }
        } catch (Exception e) {
            log.error("hookRequestToServer execute error.", e);
        }
        return retVal;
    }

    /**
     * 该函数在响应从服务端刚到达Burp时被调用
     */
    public HttpResponse tryHookResponseToBurp(HttpResponseReceived httpResponse) {
        String name = "hookResponseToBurp";
        try {
            if (config.getCacheOption().isHookResponse() && hookedIds.contains(httpResponse.messageId())) {
                hookedIds.remove(httpResponse.messageId());
                Response response = Response.of(httpResponse);
                log.debug("[{}] before hook: {}", name, response);
                response.getHeaders().put(Constants.HTTP_HEADER_HOOK_HEADER_KEY, "HttpHook");
                Response result = hookResponseToBurp(response);
                log.debug("[{}] after hook: {}", name, result);
                if (result == null) {
                    return httpResponse;
                }
                log.debug("exec method: hookResponseToBurp with {} success.", this.getClass().getSimpleName());

                return result.toBurp();
            }

        } catch (Exception e) {
            log.error("hookResponseToBurp execute error.", e);
        }
        return httpResponse;
    }

    /**
     * 该函数在响应从Burp发送到客户端时被调用
     */
    public HttpResponse tryHookResponseToClient(InterceptedResponse httpResponse) {
        String name = "hookResponseToClient";
        try {
            if (httpResponse.hasHeader(Constants.HTTP_HEADER_HOOK_HEADER_KEY)) {
                Response response = Response.of(httpResponse);
                log.debug("[{}] before hook: {}", name, response);
                response.getHeaders().remove(Constants.HTTP_HEADER_HOOK_HEADER_KEY);
                Response result = hookResponseToClient(response);
                log.debug("[{}] after hook: {}", name, result);
                if (result == null) {
                    return httpResponse;
                }
                log.debug("exec method: {} with {} success.",name, this.getClass().getSimpleName());
                return result.toBurp();
            }
        } catch (Exception e) {
            log.error("hookResponseToClient execute error.", e);
        }
        return httpResponse;
    }

    /**
     * 该函数在客户端请求到达Burp时被调用
     */
    public abstract Request hookRequestToBurp(Request request);

    /**
     * 该函数在请求从Burp发送到服务端时被调用
     */
    public abstract Request hookRequestToServer(Request request);

    /**
     * 该函数在响应从服务端刚到达Burp时被调用
     */
    public abstract Response hookResponseToBurp(Response response);

    /**
     * 该函数在响应从Burp发送到客户端时被调用
     */
    public abstract Response hookResponseToClient(Response response);
}
