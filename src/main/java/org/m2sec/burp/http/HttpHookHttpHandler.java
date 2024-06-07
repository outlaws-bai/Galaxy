package org.m2sec.burp.http;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import org.m2sec.GalaxyMain;
import org.m2sec.common.models.Request;
import org.m2sec.common.models.Response;
import org.m2sec.modules.traffic.decorate.DecorateService;
import org.m2sec.modules.traffic.match.SpecialRuleMatchService;

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
        HttpRequest request;
        // 处理Http Hook
        if (GalaxyMain.httpHookService != null)
            request =
                    GalaxyMain.httpHookService.tryHookRequestToServer(
                            httpRequestToBeSent,
                            GalaxyMain.config
                                    .getHttpTrafficAutoModificationConfig()
                                    .getHookConfig());
        else request = httpRequestToBeSent;
        // 处理Rule Match
        SpecialRuleMatchService.matchRequest(
                Request.of(request),
                httpRequestToBeSent.messageId(),
                GalaxyMain.config
                        .getHttpTrafficAutoModificationConfig()
                        .getSpecialRuleMatchConfig());
        return RequestToBeSentAction.continueWith(request);
    }

    /** 响应从服务端返回给Burp时被调用 */
    @Override
    public ResponseReceivedAction handleHttpResponseReceived(
            HttpResponseReceived httpResponseReceived) {
        HttpResponse response;
        // 处理Http Hook
        if (GalaxyMain.httpHookService != null)
            response =
                    GalaxyMain.httpHookService.tryHookResponseToBurp(
                            httpResponseReceived,
                            GalaxyMain.config
                                    .getHttpTrafficAutoModificationConfig()
                                    .getHookConfig());
        else response = httpResponseReceived;
        // 处理Rule Match
        Annotations annotations =
                SpecialRuleMatchService.matchResponse(
                        Response.of(response),
                        httpResponseReceived.messageId(),
                        GalaxyMain.config
                                .getHttpTrafficAutoModificationConfig()
                                .getSpecialRuleMatchConfig());
        // 处理Decorate
        String decorateConfig =
                GalaxyMain.config
                        .getHttpTrafficAutoModificationConfig()
                        .getDecorateConfig()
                        .getRequestDecorate();
        if (!decorateConfig.isBlank())
            response =
                    DecorateService.decorateResponse(Response.of(response), decorateConfig)
                            .toBurp();
        return ResponseReceivedAction.continueWith(response, annotations);
    }
}
