package org.m2sec.burp.proxy;

import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;
import org.m2sec.GalaxyMain;
import org.m2sec.common.models.Request;
import org.m2sec.modules.traffic.decorate.DecorateService;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class HttpTrafficAutoModificationProxyRequestHandler implements ProxyRequestHandler {

    /** 在客户端请求到达Burp时被调用 */
    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        HttpRequest request;
        // 处理Http Hook
        if (GalaxyMain.httpHookService != null)
            request =
                    GalaxyMain.httpHookService.tryHookRequestToBurp(
                            interceptedRequest,
                            GalaxyMain.config
                                    .getHttpTrafficAutoModificationConfig()
                                    .getHookConfig());
        else request = interceptedRequest;
        // 处理Decorate
        String decorateConfig =
                GalaxyMain.config
                        .getHttpTrafficAutoModificationConfig()
                        .getDecorateConfig()
                        .getRequestDecorate();
        if (!decorateConfig.isBlank())
            request = DecorateService.decorateRequest(Request.of(request), decorateConfig).toBurp();
        return ProxyRequestReceivedAction.continueWith(request);
    }

    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        return ProxyRequestToBeSentAction.continueWith(interceptedRequest);
    }
}
