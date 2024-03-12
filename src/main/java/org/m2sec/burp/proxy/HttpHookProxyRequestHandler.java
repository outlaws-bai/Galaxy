package org.m2sec.burp.proxy;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;
import org.m2sec.common.models.Request;
import org.m2sec.modules.httphook.HttpHookTransfer;
import org.m2sec.modules.rulematch.RuleMatchTransfer;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class HttpHookProxyRequestHandler implements ProxyRequestHandler {

    /** 在客户端请求到达Burp时被调用 */
    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        // 处理Http Hook
        HttpRequest request = HttpHookTransfer.hookRequestToBurp(interceptedRequest);
        // 处理Rule Match
        Annotations annotations = RuleMatchTransfer.matchRequest(Request.of(request));
        return ProxyRequestReceivedAction.continueWith(request, annotations);
    }

    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        return ProxyRequestToBeSentAction.continueWith(interceptedRequest);
    }
}
