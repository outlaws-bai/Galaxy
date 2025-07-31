package org.m2sec.abilities;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.proxy.http.*;
import org.m2sec.core.common.HttpHookThreadData;
import org.m2sec.core.httphook.IHttpHooker;
import org.m2sec.core.models.Request;

/**
 * @author: outlaws-bai
 * @date: 2024/8/15 22:30
 * @description:
 */

public class HttpHookHandler implements HttpHandler, ProxyRequestHandler, ProxyResponseHandler {

    public static IHttpHooker hooker;


    /**
     * 在客户端请求到达Burp时被调用
     */
    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        HttpHookThreadData.clear();
        Annotations annotations = interceptedRequest.annotations();
        HttpRequest request = hooker.tryHookRequestToBurp(interceptedRequest, true, false);
        if (HttpHookThreadData.requestIsFromScanner())
            annotations.setNotes("HookedByGalaxy\r\n" + annotations.notes());
        return ProxyRequestReceivedAction.continueWith(request, annotations);
    }

    /**
     * 请求从Burp发送到服务端时被调用
     */
    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        Request decryptedRequest = Request.of(requestToBeSent);
        HttpHookThreadData.setRequest(decryptedRequest);
        return RequestToBeSentAction.continueWith(hooker.tryHookRequestToServer(requestToBeSent,
            requestToBeSent.messageId(), false), requestToBeSent.annotations());
    }

    /**
     * 响应从服务端返回给Burp时被调用
     */
    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        return ResponseReceivedAction.continueWith(hooker.tryHookResponseToBurp(responseReceived,
            responseReceived.messageId(), false), responseReceived.annotations());
    }

    /**
     * 在响应从Burp返回到客户端时被调用
     */
    @Override
    public ProxyResponseToBeSentAction handleResponseToBeSent(InterceptedResponse interceptedResponse) {
        ProxyResponseToBeSentAction result =
            ProxyResponseToBeSentAction.continueWith(hooker.tryHookResponseToClient(interceptedResponse, false),
            interceptedResponse.annotations());
        HttpHookThreadData.clear();
        return result;
    }

    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        return ProxyRequestToBeSentAction.continueWith(interceptedRequest);
    }

    @Override
    public ProxyResponseReceivedAction handleResponseReceived(InterceptedResponse interceptedResponse) {
        return ProxyResponseReceivedAction.continueWith(interceptedResponse);
    }
}
