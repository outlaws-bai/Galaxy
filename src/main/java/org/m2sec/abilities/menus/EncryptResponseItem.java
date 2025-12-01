package org.m2sec.abilities.menus;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import org.m2sec.abilities.HttpHookHandler;
import org.m2sec.core.common.Config;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.HttpHookThreadData;
import org.m2sec.core.common.SwingTools;
import org.m2sec.core.models.Headers;
import org.m2sec.core.models.Request;
import org.m2sec.core.models.Response;

/**
 * @author: outlaws-bai
 * @date: 2024/8/2 11:45
 * @description:
 */
public class EncryptResponseItem extends IItem {
    public EncryptResponseItem(MontoyaApi api, Config config) {
        super(api, config);
    }

    @Override
    public String displayName() {
        return "Encrypt Response";
    }

    @Override
    public boolean isDisplay(ContextMenuEvent event) {
        return event.invocationType().containsHttpMessage()
                && event.messageEditorRequestResponse().isPresent()
                && event.messageEditorRequestResponse().get().selectionContext()
                        == MessageEditorHttpRequestResponse.SelectionContext.RESPONSE
                && config.getOption().isHookStart()
                && config.getOption().isHookResponse()
                && HttpHookHandler.hooker != null;
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void action(ContextMenuEvent event) {
        MessageEditorHttpRequestResponse messageEditorHttpRequestResponse =
                event.messageEditorRequestResponse().get();
        HttpResponse httpResponse = messageEditorHttpRequestResponse.requestResponse().response();
        Response response = Response.of(httpResponse);
        Headers headers = response.getHeaders();
        HttpRequest httpRequest = messageEditorHttpRequestResponse.requestResponse().request();
        Request request = Request.of(httpRequest);
        if (!headers.hasIgnoreCase(Constants.HTTP_HEADER_HOOK_HEADER_KEY)) {
            SwingTools.showInfoDialog(api, "The response has been encrypted.");
            return;
        }
        HttpHookThreadData.setRequest(request);
        runAsync(
                () -> HttpHookHandler.hooker.tryHookResponseToClient(httpResponse, true),
                (HttpResponse newResponse) -> {
                    SwingTools.showResponse(api, newResponse, true);
                    HttpHookThreadData.clear();
                });
    }
}
