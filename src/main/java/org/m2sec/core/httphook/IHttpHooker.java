package org.m2sec.core.httphook;

import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.m2sec.core.common.*;
import org.m2sec.core.models.Request;
import org.m2sec.core.models.Response;
import org.m2sec.core.outer.HttpClient;
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
public abstract class IHttpHooker {
    static final HashSet<Integer> hookedIds = new HashSet<>();

    protected static Option option;

    public abstract void init(Option opt);

    public abstract void destroy();


    public HttpRequest tryHookRequestToBurp(HttpRequest httpRequest, boolean isCheckExpression,
                                            boolean throwException) {
        String name = Constants.HOOK_FUNC_1;
        HttpRequest retVal = httpRequest;
        try {
            if (HttpUtil.isCorrectUrl(httpRequest.url())) {
                Request request = Request.of(httpRequest);
                // 如果请求已经带有标记头，认为是从扫描器过来的流量，不需要解密
                HttpHookThreadData.setRequestIsFromScanner(request.getHeaders().hasIgnoreCase(Constants.HTTP_HEADER_HOOK_HEADER_KEY));
                if (HttpHookThreadData.requestIsFromScanner()) {
                    log.debug("[{}] This is hooked request: {}. pass hookRequestToBurp", name, request);
                    return retVal;
                }
                log.debug("[{}] before hook: {}", name, request);
                String expression = option.getRequestCheckExpression();
                if (!isCheckExpression || (expression != null && !expression.isEmpty() && (Boolean) Render.renderExpression(expression, new HashMap<>(Map.of("request", request))))) {
                    request = hookRequestToBurp(request);
                    log.debug("[{}] after hook: {}", name, request);
                    if (request == null) {
                        return retVal;
                    }
                    // 添加标记头
                    request.getHeaders().put(Constants.HTTP_HEADER_HOOK_HEADER_KEY, "ProcessedRequest");
                    log.debug("exec method: {} with {} success.", name, this.getClass().getSimpleName());
                    // 当前线程中的请求不是来自扫描器 && 开启了联动扫描器
                    if (option.isLinkageScanner() && !HttpHookThreadData.requestIsFromScanner()) {
                        log.debug("[{}] Send request and proxy by scanner. request: {}", name, request);
                        Request finalRequest = request;
                        WorkExecutor.INSTANCE.execute(() -> HttpClient.send(finalRequest, option.getScannerConn()));
                    }
                    retVal = request.toBurp();
                }
            }
        } catch (Exception e) {
            log.error("{} execute error.", name, e);
            if (throwException) throw e;
        }
        return retVal;
    }

    /**
     * 该函数在请求从Burp发送到服务端时被调用
     */
    public HttpRequest tryHookRequestToServer(HttpRequest httpRequest, int messageId, boolean throwException) {
        String name = Constants.HOOK_FUNC_2;
        HttpRequest retVal = httpRequest;
        try {
            if (HttpUtil.isCorrectUrl(httpRequest.url()) && httpRequest.hasHeader(Constants.HTTP_HEADER_HOOK_HEADER_KEY)) {
                Request request = Request.of(httpRequest);
                log.debug("[{}] before hook: {}", name, request);
                request.getHeaders().remove(Constants.HTTP_HEADER_HOOK_HEADER_KEY);
                request = hookRequestToServer(request);
                log.debug("[{}] after hook: {}", name, request);
                // 添加标记头
                if (option.isHookResponse() && messageId != 0) {
                    hookedIds.add(messageId);
                }
                if (request == null) {
                    return retVal;
                }
                log.debug("exec method: {} with {} success.", name, this.getClass().getSimpleName());
                retVal = request.toBurp();
            }
        } catch (Exception e) {
            log.error("{} execute error.", name, e);
            if (throwException) throw e;
        }
        return retVal;
    }

    /**
     * 该函数在响应从服务端刚到达Burp时被调用
     */
    public HttpResponse tryHookResponseToBurp(HttpResponse httpResponse, int messageId, boolean throwException) {
        String name = Constants.HOOK_FUNC_3;
        try {
            if (option.isHookResponse() && (messageId == 0 || hookedIds.contains(messageId))) {
                if (messageId != 0) hookedIds.remove(messageId);
                Response response = Response.of(httpResponse);
                log.debug("[{}] before hook: {}", name, response);
                response.getHeaders().put(Constants.HTTP_HEADER_HOOK_HEADER_KEY, "ProcessedResponse");
                Response result = hookResponseToBurp(response);
                log.debug("[{}] after hook: {}", name, result);
                if (result == null) {
                    return httpResponse;
                }
                log.debug("exec method: {} with {} success.", name, this.getClass().getSimpleName());

                return result.toBurp();
            }

        } catch (Exception e) {
            log.error("{} execute error.", name, e);
            if (throwException) throw e;
        }
        return httpResponse;
    }

    /**
     * 该函数在响应从Burp发送到客户端时被调用
     */
    public HttpResponse tryHookResponseToClient(HttpResponse httpResponse, boolean throwException) {
        String name = Constants.HOOK_FUNC_4;
        try {
            Response response = Response.of(httpResponse);
            if (response.getHeaders().hasIgnoreCase(Constants.HTTP_HEADER_HOOK_HEADER_KEY)) {
                if (HttpHookThreadData.requestIsFromScanner()) {
                    log.debug("[{}] This is in Scanner. return decrypted response. response: {}", name, response);
                    return httpResponse;
                }
                log.debug("[{}] before hook: {}", name, response);
                response.getHeaders().remove(Constants.HTTP_HEADER_HOOK_HEADER_KEY);
                Response result = hookResponseToClient(response);
                log.debug("[{}] after hook: {}", name, result);
                if (result == null) {
                    return httpResponse;
                }
                log.debug("exec method: {} with {} success.", name, this.getClass().getSimpleName());
                return result.toBurp();
            }
        } catch (Exception e) {
            log.error("{} execute error.", name, e);
            if (throwException) throw e;
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
