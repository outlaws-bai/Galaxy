package org.m2sec.modules.mixed.menu;

import burp.api.montoya.core.Range;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import org.m2sec.GalaxyMain;
import org.m2sec.burp.menu.AbstractMenuItem;
import org.m2sec.common.Constants;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class UrlToRepeaterMenuItem extends AbstractMenuItem {

    public String displayName() {
        return "UrlToRepeater";
    }

    @Override
    public boolean isDisplay(ContextMenuEvent event) {
        return event.invocationType().containsHttpMessage()
            && event.messageEditorRequestResponse().isPresent()
            && event.messageEditorRequestResponse().get().selectionOffsets().isPresent();
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void action(ContextMenuEvent event) {
        // 已check，因此不用关心是否以判断preset
        MessageEditorHttpRequestResponse messageEditorHttpRequestResponse = event.messageEditorRequestResponse().get();
        Range selectRange = messageEditorHttpRequestResponse.selectionOffsets().get();
        String selectText;
        if (messageEditorHttpRequestResponse.selectionContext() == MessageEditorHttpRequestResponse.SelectionContext.REQUEST) {
            selectText =
                new String(messageEditorHttpRequestResponse.requestResponse().request().toByteArray().subArray(selectRange).getBytes());
        } else {
            selectText =
                new String(messageEditorHttpRequestResponse.requestResponse().response().toByteArray().subArray(selectRange).getBytes());
        }
        HttpRequest httpRequest = HttpRequest.httpRequestFromUrl(URLDecoder.decode(selectText, StandardCharsets.UTF_8));
        GalaxyMain.burpApi.repeater().sendToRepeater(httpRequest, Constants.BURP_SUITE_EXT_NAME);
    }
}
