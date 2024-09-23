package org.m2sec.abilities.menus;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import org.m2sec.abilities.HttpHookHandler;
import org.m2sec.core.common.Config;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.Render;
import org.m2sec.core.common.SwingTools;
import org.m2sec.core.models.Headers;
import org.m2sec.core.models.Request;
import org.m2sec.core.models.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/8/2 11:45
 * @description:
 */

public class DecryptResponseItem extends IItem {
    public DecryptResponseItem(MontoyaApi api, Config config) {
        super(api, config);
    }

    @Override
    public String displayName() {
        return "Decrypt Response";
    }

    @Override
    public boolean isDisplay(ContextMenuEvent event) {
        return event.invocationType().containsHttpMessage()
            && event.messageEditorRequestResponse().isPresent()
            && event.messageEditorRequestResponse().get().selectionContext() == MessageEditorHttpRequestResponse.SelectionContext.RESPONSE
            && config.getOption().isHookStart()
            && config.getOption().isHookResponse()
            && HttpHookHandler.hooker != null;
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void action(ContextMenuEvent event) {
        MessageEditorHttpRequestResponse messageEditorHttpRequestResponse = event.messageEditorRequestResponse().get();
        HttpResponse httpResponse = messageEditorHttpRequestResponse.requestResponse().response();
        Response response = Response.of(httpResponse);
        Headers headers = response.getHeaders();
        HttpRequest httpRequest = messageEditorHttpRequestResponse.requestResponse().request();
        Request request = Request.of(httpRequest);
        if (headers.hasIgnoreCase(Constants.HTTP_HEADER_HOOK_HEADER_KEY)) {
            SwingTools.showInfoDialog("The response has been decrypted.");
            return;
        }
        String expression = config.getOption().getRequestCheckExpression();
        if (expression == null || expression.isBlank() || !(Boolean) Render.renderExpression(expression,
            new HashMap<>(Map.of("request", request)))) {
            SwingTools.showInfoDialog("The result of using this response's request to execute the check expression is" +
                " false. " +
                "Please check.");
            return;
        }
        HttpResponse newResponse = HttpHookHandler.hooker.tryHookResponseToBurp(httpResponse, 0, true);
        SwingTools.showResponse(api, newResponse, false);
    }
}
