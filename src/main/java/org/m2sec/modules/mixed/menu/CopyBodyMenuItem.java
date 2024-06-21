package org.m2sec.modules.mixed.menu;

import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import org.m2sec.burp.menu.AbstractMenuItem;
import org.m2sec.common.models.Request;
import org.m2sec.common.models.Response;
import org.m2sec.common.utils.CompatUtil;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class CopyBodyMenuItem extends AbstractMenuItem {
    @Override
    public String displayName() {
        return "CopyBody";
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
            CompatUtil.copyToClipboard(response.getContent());
        } else {
            Request request = Request.of(messageEditorHttpRequestResponse.requestResponse().request());
            CompatUtil.copyToClipboard(request.getContent());
        }
    }
}
