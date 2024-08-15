package org.m2sec.abilities.menus;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import org.m2sec.abilities.HttpHookHandler;
import org.m2sec.core.common.Config;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.SwingTools;
import org.m2sec.core.models.Headers;
import org.m2sec.core.models.Request;

/**
 * @author: outlaws-bai
 * @date: 2024/7/14 21:14
 * @description:
 */

public class EncryptRequestItem extends IItem {
    public EncryptRequestItem(MontoyaApi api, Config config) {
        super(api, config);
    }

    @Override
    public String displayName() {
        return "Encrypt Request";
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
        if (!headers.hasIgnoreCase(Constants.HTTP_HEADER_HOOK_HEADER_KEY)) {
            SwingTools.showInfoDialog("The request has been encrypted.");
            return;
        }
        HttpRequest newRequest = HttpHookHandler.hooker.tryHookRequestToServer(httpRequest, 0, true);
        if (event.isFromTool(ToolType.REPEATER)) {
            messageEditorHttpRequestResponse.setRequest(newRequest);
        } else {
            SwingTools.showRequest(api, newRequest, true);
        }
    }
}
