package org.m2sec.burp.http;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import org.m2sec.GalaxyMain;
import org.m2sec.common.models.Request;
import org.m2sec.common.models.Response;
import org.m2sec.modules.traffic.decorate.DecorateService;
import org.m2sec.modules.traffic.hook.AbstractHttpHookService;
import org.m2sec.modules.traffic.match.SpecialRuleMatchService;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class HttpTrafficAutoModificationHttpHandler implements HttpHandler {

    /**
     * 请求从Burp发送到服务端时被调用
     */
    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent httpRequestToBeSent) {
        HttpRequest request;
        // 处理Http Hook
        if (AbstractHttpHookService.hookService != null)
            request = AbstractHttpHookService.hookService.tryHookRequestToServer(httpRequestToBeSent,
                GalaxyMain.config.getHttpTrafficAutoModificationConfig().getHookConfig());
        else request = httpRequestToBeSent;
        // 处理Rule Match
        SpecialRuleMatchService.matchRequest(Request.of(request), httpRequestToBeSent.messageId(),
            GalaxyMain.config.getHttpTrafficAutoModificationConfig().getRuleMatchConfig());
        return RequestToBeSentAction.continueWith(request);
    }

    /**
     * 响应从服务端返回给Burp时被调用
     */
    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived httpResponseReceived) {
        HttpResponse response;
        // 处理Http Hook
        if (AbstractHttpHookService.hookService != null)
            response = AbstractHttpHookService.hookService.tryHookResponseToBurp(httpResponseReceived,
                GalaxyMain.config.getHttpTrafficAutoModificationConfig().getHookConfig());
        else response = httpResponseReceived;
        // 处理Rule Match
        Annotations annotations = SpecialRuleMatchService.matchResponse(Response.of(response),
            httpResponseReceived.messageId(),
            GalaxyMain.config.getHttpTrafficAutoModificationConfig().getRuleMatchConfig());
        // 处理Decorate
        String decorateConfig =
            GalaxyMain.config.getHttpTrafficAutoModificationConfig().getDecorateConfig().getResponseModifyExpression();
        if (!decorateConfig.isBlank())
            response = DecorateService.decorateResponse(Response.of(response), decorateConfig).toBurp();
        return ResponseReceivedAction.continueWith(response, annotations);
    }
}
