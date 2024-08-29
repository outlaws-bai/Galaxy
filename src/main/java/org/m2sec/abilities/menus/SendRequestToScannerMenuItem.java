package org.m2sec.abilities.menus;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import org.m2sec.core.common.Config;
import org.m2sec.core.common.WorkExecutor;
import org.m2sec.core.models.Request;
import org.m2sec.core.outer.HttpClient;

/**
 * @author: outlaws-bai
 * @date: 2024/8/30 0:06
 * @description:
 */

public class SendRequestToScannerMenuItem extends IItem {
    public SendRequestToScannerMenuItem(MontoyaApi api, Config config) {
        super(api, config);
    }

    @Override
    public String displayName() {
        return "Send Request To Scanner";
    }

    @Override
    public boolean isDisplay(ContextMenuEvent event) {
        return event.messageEditorRequestResponse().isPresent()
            && event.messageEditorRequestResponse().get().selectionContext().equals(MessageEditorHttpRequestResponse.SelectionContext.REQUEST);
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void action(ContextMenuEvent event) {
        MessageEditorHttpRequestResponse messageEditorHttpRequestResponse = event.messageEditorRequestResponse().get();
        Request request = Request.of(messageEditorHttpRequestResponse.requestResponse().request());
        WorkExecutor.INSTANCE.execute(() -> HttpClient.send(request, config.getOption().getScannerConn()));
    }
}
