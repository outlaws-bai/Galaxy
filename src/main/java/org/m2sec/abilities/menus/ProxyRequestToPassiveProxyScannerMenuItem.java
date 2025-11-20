package org.m2sec.abilities.menus;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import org.m2sec.core.common.Config;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.SwingTools;
import org.m2sec.core.common.WorkExecutor;
import org.m2sec.core.models.Request;
import org.m2sec.core.outer.HttpClient;

/**
 * @author: outlaws-bai
 * @date: 2024/8/30 0:06
 * @description:
 */

public class ProxyRequestToPassiveProxyScannerMenuItem extends IItem {
    public ProxyRequestToPassiveProxyScannerMenuItem(MontoyaApi api, Config config) {
        super(api, config);
    }

    @Override
    public String displayName() {
        return "Proxy Request To Passive Proxy Scanner";
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
        if (request.getHeaders().hasIgnoreCase(Constants.HTTP_HEADER_HOOK_HEADER_KEY)) {
            SwingTools.showInfoDialog(api, "The request is decrypted.");
            return;
        }
        WorkExecutor.INSTANCE.execute(() -> HttpClient.send(request, config.getSetting().getScannerConn()));
    }
}
