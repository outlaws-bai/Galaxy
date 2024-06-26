package org.m2sec.burp.proxy;

import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.proxy.http.InterceptedResponse;
import burp.api.montoya.proxy.http.ProxyResponseHandler;
import burp.api.montoya.proxy.http.ProxyResponseReceivedAction;
import burp.api.montoya.proxy.http.ProxyResponseToBeSentAction;
import org.m2sec.GalaxyMain;
import org.m2sec.modules.traffic.hook.AbstractHttpHookService;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class HttpTrafficAutoModificationProxyResponseHandler implements ProxyResponseHandler {

    @Override
    public ProxyResponseReceivedAction handleResponseReceived(InterceptedResponse interceptedResponse) {
        return ProxyResponseReceivedAction.continueWith(interceptedResponse);
    }

    /**
     * 在响应从Burp返回到客户端时被调用
     */
    @Override
    public ProxyResponseToBeSentAction handleResponseToBeSent(InterceptedResponse interceptedResponse) {
        HttpResponse response;
        // 处理Http Hook
        if (AbstractHttpHookService.hookService != null)
            response = AbstractHttpHookService.hookService.tryHookResponseToClient(interceptedResponse,
                GalaxyMain.config.getHttpTrafficAutoModificationConfig().getHookConfig());
        else response = interceptedResponse;
        return ProxyResponseToBeSentAction.continueWith(response);
    }
}
