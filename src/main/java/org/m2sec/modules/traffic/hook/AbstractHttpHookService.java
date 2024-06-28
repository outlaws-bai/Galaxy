package org.m2sec.modules.traffic.hook;

import burp.api.montoya.http.handler.HttpRequestToBeSent;
import burp.api.montoya.http.handler.HttpResponseReceived;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.InterceptedResponse;
import lombok.extern.slf4j.Slf4j;
import org.m2sec.GalaxyMain;
import org.m2sec.common.Constants;
import org.m2sec.common.Render;
import org.m2sec.common.config.HttpTrafficAutoModificationConfig;
import org.m2sec.common.enums.HttpHookService;
import org.m2sec.common.models.Request;
import org.m2sec.common.models.Response;
import org.m2sec.common.utils.HttpUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Slf4j
public abstract class AbstractHttpHookService {
    static final HashSet<Integer> hookedIds = new HashSet<>();

    public static AbstractHttpHookService hookService;

    public abstract void init();

    public abstract void destroy();

    public static void trySetService() {
        trySetService(GalaxyMain.config.getHttpTrafficAutoModificationConfig().getHookConfig());
    }

    public static void trySetService(HttpTrafficAutoModificationConfig.HookConfig hookConfig) {
        if (hookConfig.isStart() && hookConfig.getHookService() != null) {
            if (HttpHookService.RPC.equals(hookConfig.getHookService())) {
                hookService = new RpcService();
            } else if (HttpHookService.JAVA.equals(hookConfig.getHookService())) {
                hookService = new JavaFileService();
            } else {
                throw new RuntimeException("hookService is error! please choose RPC or JAVA");
            }
            hookService.init();
        } else {
            if (hookService != null) hookService.destroy();
            hookService = null;
        }
    }

    public HttpRequest tryHookRequestToBurp(InterceptedRequest httpRequest,
                                            HttpTrafficAutoModificationConfig.HookConfig hookConfig) {
        log.debug("exec method: tryHookRequestToBurp");
        HttpRequest retVal = httpRequest;
        try {
            if (HttpUtil.isCorrectUrl(httpRequest.url()) && hookConfig.isRequestIsNeedHook()) {
                Request request = Request.of(httpRequest).normalize();
                if (hookConfig.getRequestMatchExpression() != null && !hookConfig.getRequestMatchExpression().isEmpty() && (Boolean) Render.renderExpression(hookConfig.getRequestMatchExpression(), new HashMap<>(Map.of("request", request)))) {
                    log.debug("exec method: hookRequestToBurp with " + this.getClass().getSimpleName());
                    request = hookRequestToBurp(request);
                    if (request == null) {
                        return retVal;
                    }
                    // 添加标记头
                    request.getHeaders().put(Constants.HTTP_HEADER_HOOK_HEADER_KEY, "HttpHook");
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
    public HttpRequest tryHookRequestToServer(HttpRequestToBeSent httpRequest,
                                              HttpTrafficAutoModificationConfig.HookConfig hookConfig) {
        HttpRequest retVal = httpRequest;
        log.debug("exec method: tryHookRequestToServer");
        try {
            if (HttpUtil.isCorrectUrl(httpRequest.url()) && httpRequest.hasHeader(Constants.HTTP_HEADER_HOOK_HEADER_KEY)) {
                Request request = Request.of(httpRequest).normalize();
                request.getHeaders().remove(Constants.HTTP_HEADER_HOOK_HEADER_KEY);
                log.debug("exec method: hookRequestToServer with " + this.getClass().getSimpleName());
                request = hookRequestToServer(request);
                // 移除标记头
                if (hookConfig.isResponseIsNeedHook()) {
                    hookedIds.add(httpRequest.messageId());
                }
                if (request == null) {
                    return retVal;
                }
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
    public HttpResponse tryHookResponseToBurp(HttpResponseReceived httpResponse,
                                              HttpTrafficAutoModificationConfig.HookConfig hookConfig) {
        log.debug("exec method: tryHookResponseToBurp");
        try {
            if (hookConfig.isResponseIsNeedHook() && hookedIds.contains(httpResponse.messageId())) {
                hookedIds.remove(httpResponse.messageId());
                Response response = Response.of(httpResponse);
                response.getHeaders().put(Constants.HTTP_HEADER_HOOK_HEADER_KEY, "HttpHook");
                log.debug("exec method: hookResponseToBurp with " + this.getClass().getSimpleName());
                Response result = hookResponseToBurp(response);
                if (result == null) {
                    return httpResponse;
                }
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
    public HttpResponse tryHookResponseToClient(InterceptedResponse httpResponse,
                                                HttpTrafficAutoModificationConfig.HookConfig hookConfig) {
        log.debug("exec method: tryHookResponseToClient");
        try {
            if (httpResponse.hasHeader(Constants.HTTP_HEADER_HOOK_HEADER_KEY)) {
                Response response = Response.of(httpResponse);
                response.getHeaders().remove(Constants.HTTP_HEADER_HOOK_HEADER_KEY);
                log.debug("exec method: hookResponseToClient with " + this.getClass().getSimpleName());
                Response result = hookResponseToClient(response);
                if (result == null) {
                    return httpResponse;
                }
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
