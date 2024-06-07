package org.m2sec.modules.traffic.hook;

import burp.api.montoya.http.handler.HttpRequestToBeSent;
import burp.api.montoya.http.handler.HttpResponseReceived;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.InterceptedResponse;
import org.m2sec.common.Constants;
import org.m2sec.common.Log;
import org.m2sec.common.config.HttpTrafficAutoModificationConfig;
import org.m2sec.common.models.Request;
import org.m2sec.common.models.Response;
import org.m2sec.common.utils.HttpUtil;
import org.mvel2.MVEL;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/5/10 15:31
 * @description:
 */
public abstract class AbstractHttpHookService {
    static final HashSet<Integer> hookedIds = new HashSet<>();

    private static final Log log = new Log(AbstractHttpHookService.class);

    public abstract void init();

    public abstract void destroy();

    public HttpRequest tryHookRequestToBurp(
            InterceptedRequest httpRequest, HttpTrafficAutoModificationConfig.HookConfig hookConfig) {
        log.debug("exec method: tryHookRequestToBurp");
        try {
            if (HttpUtil.isCorrectUrl(httpRequest.url()) && hookConfig.isHookRequestToBurp()) {
                Request request = Request.of(httpRequest).normalize();
                if (hookConfig.getResponseMatcher() != null
                        && !hookConfig.getRequestMatcher().isEmpty()
                        && (Boolean)
                                MVEL.eval(
                                        hookConfig.getRequestMatcher(),
                                        new HashMap<>(Map.of("request", request)))) {
                    // 添加标记头
                    request.getHeaders().put(Constants.HTTP_HOOK_HEADER_KEY, "HttpHook");
                    log.debug(
                            "exec method: hookRequestToBurp with "
                                    + this.getClass().getSimpleName());
                    return hookRequestToBurp(request).toBurp();
                }
            }
        } catch (Exception e) {
            log.exception(e, "hookRequestToBurp execute error.");
        }
        return httpRequest;
    }

    /** 该函数在请求从Burp发送到服务端时被调用 */
    public HttpRequest tryHookRequestToServer(
            HttpRequestToBeSent httpRequest, HttpTrafficAutoModificationConfig.HookConfig hookConfig) {
        log.debug("exec method: tryHookRequestToServer");
        try {
            if (HttpUtil.isCorrectUrl(httpRequest.url())
                    && hookConfig.isHookRequestToServer()
                    && httpRequest.hasHeader(Constants.HTTP_HOOK_HEADER_KEY)) {
                Request request = Request.of(httpRequest).normalize();
                // 移除标记头
                request.getHeaders().remove(Constants.HTTP_HOOK_HEADER_KEY);
                if (hookConfig.isHookResponseToBurp()) {
                    hookedIds.add(httpRequest.messageId());
                }
                log.debug(
                        "exec method: hookRequestToServer with " + this.getClass().getSimpleName());
                return hookRequestToServer(request).toBurp();
            }
        } catch (Exception e) {
            log.exception(e, "hookRequestToServer execute error.");
        }
        return httpRequest;
    }

    /** 该函数在响应从服务端刚到达Burp时被调用 */
    public HttpResponse tryHookResponseToBurp(
            HttpResponseReceived httpResponse, HttpTrafficAutoModificationConfig.HookConfig hookConfig) {
        log.debug("exec method: tryHookResponseToBurp");
        try {
            if (hookConfig.isHookResponseToBurp() && hookedIds.contains(httpResponse.messageId())) {
                hookedIds.remove(httpResponse.messageId());
                Response response = Response.of(httpResponse);
                if (hookConfig.getResponseMatcher() != null
                        && !hookConfig.getResponseMatcher().isEmpty()
                        && (Boolean)
                                MVEL.eval(
                                        hookConfig.getResponseMatcher(),
                                        new HashMap<>(Map.of("response", response)))) {
                    response.getHeaders().put(Constants.HTTP_HOOK_HEADER_KEY, "HttpHook");
                    log.debug(
                            "exec method: hookResponseToBurp with "
                                    + this.getClass().getSimpleName());
                    return hookResponseToBurp(response).toBurp();
                }
            }

        } catch (Exception e) {
            log.exception(e, "hookResponseToBurp execute error.");
        }
        return httpResponse;
    }

    /** 该函数在响应从Burp发送到客户端时被调用 */
    public HttpResponse tryHookResponseToClient(
            InterceptedResponse httpResponse, HttpTrafficAutoModificationConfig.HookConfig hookConfig) {
        log.debug("exec method: tryHookResponseToClient");
        try {
            if (hookConfig.isHookResponseToClient()
                    && httpResponse.hasHeader(Constants.HTTP_HOOK_HEADER_KEY)) {
                Response response = Response.of(httpResponse);
                response.getHeaders().remove(Constants.HTTP_HOOK_HEADER_KEY);
                log.debug(
                        "exec method: hookResponseToClient with "
                                + this.getClass().getSimpleName());
                return hookResponseToClient(response).toBurp();
            }
        } catch (Exception e) {
            log.exception(e, "hookResponseToClient execute error.");
        }
        return httpResponse;
    }

    /** 该函数在客户端请求到达Burp时被调用 */
    public abstract Request hookRequestToBurp(Request request);

    /** 该函数在请求从Burp发送到服务端时被调用 */
    public abstract Request hookRequestToServer(Request request);

    /** 该函数在响应从服务端刚到达Burp时被调用 */
    public abstract Response hookResponseToBurp(Response response);

    /** 该函数在响应从Burp发送到客户端时被调用 */
    public abstract Response hookResponseToClient(Response httpResponse);
}
