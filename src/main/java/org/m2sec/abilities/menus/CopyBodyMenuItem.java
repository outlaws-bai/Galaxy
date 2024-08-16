package org.m2sec.abilities.menus;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import org.m2sec.core.common.CompatTools;
import org.m2sec.core.common.Config;
import org.m2sec.core.models.Request;
import org.m2sec.core.models.Response;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class CopyBodyMenuItem extends IItem {
    public CopyBodyMenuItem(MontoyaApi api, Config config) {
        super(api, config);
    }

    @Override
    public String displayName() {
        return "Copy Body";
    }

    @Override
    public boolean isDisplay(ContextMenuEvent event) {
        return event.invocationType().containsHttpMessage() && event.messageEditorRequestResponse().isPresent();
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void action(ContextMenuEvent event) {
        MessageEditorHttpRequestResponse messageEditorHttpRequestResponse = event.messageEditorRequestResponse().get();
        if (event.messageEditorRequestResponse().get().selectionContext() == MessageEditorHttpRequestResponse.SelectionContext.RESPONSE) {
            Response response = Response.of(messageEditorHttpRequestResponse.requestResponse().response());
            CompatTools.copyToClipboard(response.getContent());
        } else {
            Request request = Request.of(messageEditorHttpRequestResponse.requestResponse().request());
            CompatTools.copyToClipboard(request.getContent());
        }
    }
}
