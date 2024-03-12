package org.m2sec.burp.http;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import org.m2sec.common.models.Response;
import org.m2sec.modules.httphook.HttpHookTransfer;
import org.m2sec.modules.rulematch.RuleMatchTransfer;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class HttpHookHttpHandler implements HttpHandler {

    /** 请求从Burp发送到服务端时被调用 */
    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(
            HttpRequestToBeSent httpRequestToBeSent) {
        // 处理Http Hook
        HttpRequest request = HttpHookTransfer.hookRequestToServer(httpRequestToBeSent);
        return RequestToBeSentAction.continueWith(request);
    }

    /** 响应从服务端返回给Burp时被调用 */
    @Override
    public ResponseReceivedAction handleHttpResponseReceived(
            HttpResponseReceived httpResponseReceived) {
        // 处理Http Hook
        HttpResponse response = HttpHookTransfer.hookResponseToBurp(httpResponseReceived);
        // 处理Rule Match
        Annotations annotations = RuleMatchTransfer.matchResponse(Response.of(response));
        return ResponseReceivedAction.continueWith(response, annotations);
    }
}
