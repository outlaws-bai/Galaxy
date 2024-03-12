package org.m2sec.modules.bypass.menu;

import burp.api.montoya.core.ToolType;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import org.m2sec.burp.menu.AbstractMenuItem;
import org.m2sec.common.models.Request;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class BypassIPMenuItem extends AbstractMenuItem {
    @Override
    public String displayName() {
        return "Bypass IP";
    }

    @Override
    public boolean isDisplay(ContextMenuEvent event) {
        return event.isFromTool(ToolType.REPEATER)
                && event.messageEditorRequestResponse().isPresent()
                && event.messageEditorRequestResponse()
                        .get()
                        .selectionContext()
                        .equals(MessageEditorHttpRequestResponse.SelectionContext.REQUEST);
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void action(ContextMenuEvent event) {
        MessageEditorHttpRequestResponse messageEditorHttpRequestResponse =
                event.messageEditorRequestResponse().get();
        Request request = Request.of(messageEditorHttpRequestResponse.requestResponse().request());
        String ip = "127.0.0.1";
        // from https://book.hacktricks.xyz/network-services-pentesting/pentesting-web/403-and-401-bypasses
        request.updateHeader("X-Forwarded-For", ip)
                .updateHeader("X-Originating-IP", ip)
                .updateHeader("X-Remote-IP", ip)
                .updateHeader("X-Remote-Addr", ip)
                .updateHeader("X-Real-IP", ip)
                .updateHeader("X-Forwarded", ip)
                .updateHeader("X-Forwarded-Host", ip)
                .updateHeader("X-Client-IP", ip)
                .updateHeader("X-ProxyUser-Ip", ip)
                .updateHeader("X-Original-URL", ip)
                .updateHeader("Client-IP", ip)
                .updateHeader("True-Client-IP", ip)
                .updateHeader("Cluster-Client-IP", ip)
                .updateHeader("X-Host", ip);
        messageEditorHttpRequestResponse.setRequest(request.toBurp());
    }
}
