package org.m2sec.modules.httphook;

import burp.api.montoya.http.handler.HttpRequestToBeSent;
import burp.api.montoya.http.handler.HttpResponseReceived;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.proxy.http.InterceptedRequest;
import com.googlecode.aviator.AviatorEvaluator;
import org.m2sec.GalaxyMain;
import org.m2sec.common.Constants;
import org.m2sec.common.Log;
import org.m2sec.common.models.Request;
import org.m2sec.common.models.Response;
import org.m2sec.common.rpc.RpcClient;
import org.m2sec.common.utils.HttpUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class HttpHookTransfer {

    public static RpcClient client;

    private static final HashSet<Integer> hookedIds = new HashSet<>();

    private static final Log log = new Log(HttpHookTransfer.class);

    /** 该函数在客户端请求到达Burp时被调用 */
    public static HttpRequest hookRequestToBurp(InterceptedRequest httpRequest) {
        try {
            if (client != null
                    && HttpUtil.isInvalidUrl(httpRequest.url())
                    && GalaxyMain.config.getHttpHook().isHookRequestToBurp()) {
                Request request = Request.of(httpRequest).normalize();
                if (GalaxyMain.config.getHttpHook().getResponseMatcher() != null
                        && !GalaxyMain.config.getHttpHook().getRequestMatcher().isEmpty()
                        && (Boolean)
                                AviatorEvaluator.execute(
                                        GalaxyMain.config.getHttpHook().getRequestMatcher(),
                                        new HashMap<>(Map.of("request", request)))) {
                    // 添加标记头
                    request.addHeader(Constants.HTTP_HOOK_HEADER_KEY, "HttpHook");
                    return Request.of(client.blockingStub.hookRequestToBurp(request.toRpc()))
                            .toBurp();
                }
            }
        } catch (Exception e) {
            log.exception(e, "hookRequestToBurp execute error.");
        }
        return httpRequest;
    }

    /** 该函数在请求从Burp发送到服务端时被调用 */
    public static HttpRequest hookRequestToServer(HttpRequestToBeSent httpRequest) {
        try {
            if (client != null
                    && HttpUtil.isInvalidUrl(httpRequest.url())
                    && GalaxyMain.config.getHttpHook().isHookRequestToServer()
                    && httpRequest.hasHeader(Constants.HTTP_HOOK_HEADER_KEY)) {
                Request request = Request.of(httpRequest).normalize();
                // 移除标记头
                request.removeHeader(Constants.HTTP_HOOK_HEADER_KEY);
                if (GalaxyMain.config.getHttpHook().isHookResponseToBurp()) {
                    hookedIds.add(httpRequest.messageId());
                }
                return Request.of(client.blockingStub.hookRequestToServer(request.toRpc()))
                        .toBurp();
            }
        } catch (Exception e) {
            log.exception(e, "hookRequestToServer execute error.");
        }
        return httpRequest;
    }

    /** 该函数在响应从服务端刚到达Burp时被调用 */
    public static HttpResponse hookResponseToBurp(HttpResponseReceived httpResponse) {
        try {
            if (client != null
                    && GalaxyMain.config.getHttpHook().isHookResponseToBurp()
                    && hookedIds.contains(httpResponse.messageId())) {
                hookedIds.remove(httpResponse.messageId());
                Response response = Response.of(httpResponse);
                if (GalaxyMain.config.getHttpHook().getResponseMatcher() != null
                        && !GalaxyMain.config.getHttpHook().getResponseMatcher().isEmpty()
                        && (Boolean)
                                AviatorEvaluator.execute(
                                        GalaxyMain.config.getHttpHook().getResponseMatcher(),
                                        new HashMap<>(Map.of("response", response)))) {
                    response.addHeader(Constants.HTTP_HOOK_HEADER_KEY, "HttpHook");
                    return Response.of(client.blockingStub.hookResponseToBurp(response.toGRpc()))
                            .toBurp();
                }
            }

        } catch (Exception e) {
            log.exception(e, "hookResponseToBurp execute error.");
        }
        return httpResponse;
    }

    /** 该函数在响应从Burp发送到客户端时被调用 */
    public static HttpResponse hookResponseToClient(HttpResponse httpResponse) {
        try {
            if (client != null
                    && GalaxyMain.config.getHttpHook().isHookResponseToClient()
                    && httpResponse.hasHeader(Constants.HTTP_HOOK_HEADER_KEY)) {
                Response response = Response.of(httpResponse);
                response.removeHeader(Constants.HTTP_HOOK_HEADER_KEY);
                return Response.of(client.blockingStub.hookResponseToClient(response.toGRpc()))
                        .toBurp();
            }

        } catch (Exception e) {
            log.exception(e, "hookResponseToClient execute error.");
        }
        return httpResponse;
    }
}
