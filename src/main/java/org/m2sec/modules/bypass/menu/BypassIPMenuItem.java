package org.m2sec.modules.bypass.menu;

import burp.api.montoya.core.ToolType;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import org.m2sec.GalaxyMain;
import org.m2sec.burp.menu.AbstractMenuItem;
import org.m2sec.common.models.Headers;
import org.m2sec.common.models.Request;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class BypassIPMenuItem extends AbstractMenuItem {
    @Override
    public String displayName() {
        return "BypassIP";
    }

    @Override
    public boolean isDisplay(ContextMenuEvent event) {
        return event.isFromTool(ToolType.REPEATER)
            && event.messageEditorRequestResponse().isPresent()
            && event.messageEditorRequestResponse().get().selectionContext().equals(MessageEditorHttpRequestResponse.SelectionContext.REQUEST);
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void action(ContextMenuEvent event) {
        MessageEditorHttpRequestResponse messageEditorHttpRequestResponse = event.messageEditorRequestResponse().get();
        Request request = Request.of(messageEditorHttpRequestResponse.requestResponse().request());
        String ip = GalaxyMain.config.getBypassConfig().getBypassIPDefaultValue();
        // from
        // https://book.hacktricks.xyz/network-services-pentesting/pentesting-web/403-and-401-bypasses
        Headers headers = request.getHeaders();
        headers.put("X-Forwarded-For", ip).put("X-Originating-IP", ip).put("X-Remote-IP", ip).put("X-Remote-Addr",
            ip).put("X-Real-IP", ip).put("X-Forwarded", ip).put("X-Forwarded-Host", ip).put("X-Client-IP", ip).put("X"
            + "-ProxyUser-Ip", ip).put("X-Original-URL", ip).put("Client-IP", ip).put("True-Client-IP", ip).put(
                "Cluster-Client-IP", ip).put("X-Host", ip);
        messageEditorHttpRequestResponse.setRequest(request.toBurp());
    }
}
