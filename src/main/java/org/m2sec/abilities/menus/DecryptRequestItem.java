package org.m2sec.abilities.menus;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import org.m2sec.abilities.HttpHookHandler;
import org.m2sec.core.common.*;
import org.m2sec.core.models.Headers;
import org.m2sec.core.models.Request;

import java.util.HashMap;
import java.util.Map;


/**
 * @author: outlaws-bai
 * @date: 2024/7/14 20:53
 * @description:
 */

public class DecryptRequestItem extends IItem {
    public DecryptRequestItem(MontoyaApi api, Config config) {
        super(api, config);
    }

    @Override
    public String displayName() {
        return "Decrypt Request";
    }

    @Override
    public boolean isDisplay(ContextMenuEvent event) {
        return event.invocationType().containsHttpMessage()
            && event.messageEditorRequestResponse().isPresent()
            && event.messageEditorRequestResponse().get().selectionContext() == MessageEditorHttpRequestResponse.SelectionContext.REQUEST
            && config.getOption().isHookStart()
            && HttpHookHandler.hooker != null;
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void action(ContextMenuEvent event) {
        MessageEditorHttpRequestResponse messageEditorHttpRequestResponse = event.messageEditorRequestResponse().get();
        HttpRequest httpRequest = messageEditorHttpRequestResponse.requestResponse().request();
        Request request = Request.of(httpRequest);
        Headers headers = request.getHeaders();
        if (headers.hasIgnoreCase(Constants.HTTP_HEADER_HOOK_HEADER_KEY)) {
            SwingTools.showInfoDialog(api, "The request has been decrypted.");
            return;
        }
        String expression = config.getOption().getRequestCheckExpression();
        if (expression == null || expression.isBlank() || !(Boolean) Render.renderExpression(expression,
            new HashMap<>(Map.of("request", request)))) {
            SwingTools.showInfoDialog(api, "The result of using this request to execute the check expression is false. " +
                "Please check.");
            return;
        }
        HttpRequest newRequest = HttpHookHandler.hooker.tryHookRequestToBurp(httpRequest, false, true);
        if (event.isFromTool(ToolType.REPEATER)) {
            messageEditorHttpRequestResponse.setRequest(newRequest);
        } else {
            SwingTools.showRequest(api, newRequest, false);
        }
        HttpHookThreadData.clear();
    }
}
