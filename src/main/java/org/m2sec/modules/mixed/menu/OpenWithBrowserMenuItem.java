package org.m2sec.modules.mixed.menu;

import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import org.m2sec.burp.menu.AbstractMenuItem;
import org.m2sec.common.utils.CompatUtil;

/**
 * @author: outlaws-bai
 * @date: 2024/5/13 18:25
 * @description:
 */
public class OpenWithBrowserMenuItem extends AbstractMenuItem {
    @Override
    public String displayName() {
        return "OpenWithBrowser";
    }

    @Override
    public boolean isDisplay(ContextMenuEvent event) {
        return event.invocationType().containsHttpMessage()
                && event.messageEditorRequestResponse().isPresent();
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void action(ContextMenuEvent event) {
        MessageEditorHttpRequestResponse messageEditorHttpRequestResponse =
                event.messageEditorRequestResponse().get();
        String url = messageEditorHttpRequestResponse.requestResponse().request().url();
        CompatUtil.openWithBrowser(url);
    }
}
