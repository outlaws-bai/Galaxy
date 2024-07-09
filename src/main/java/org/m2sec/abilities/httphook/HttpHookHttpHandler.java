package org.m2sec.abilities.httphook;

import burp.api.montoya.http.handler.*;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 0:53
 * @description:
 */

public class HttpHookHttpHandler implements HttpHandler {
    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        return null;
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        return null;
    }
}
