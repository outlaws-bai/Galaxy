package org.m2sec.abilities.menus;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import org.m2sec.abilities.HttpHookHandler;
import org.m2sec.core.common.*;
import org.m2sec.core.models.Request;
import org.m2sec.core.outer.HttpClient;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class SendDecryptedRequestToScannerMenuItem extends IItem {


    public SendDecryptedRequestToScannerMenuItem(MontoyaApi api, Config config) {
        super(api, config);
    }

    public String displayName() {
        return "Send Decrypted Request To Scanner";
    }

    @Override
    public boolean isDisplay(ContextMenuEvent event) {
        return event.messageEditorRequestResponse().isPresent()
            && event.messageEditorRequestResponse().get().selectionContext().equals(MessageEditorHttpRequestResponse.SelectionContext.REQUEST)
            && config.getOption().isHookStart()
            && HttpHookHandler.hooker != null;
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void action(ContextMenuEvent event) {
        MessageEditorHttpRequestResponse messageEditorHttpRequestResponse = event.messageEditorRequestResponse().get();
        Request request = Request.of(messageEditorHttpRequestResponse.requestResponse().request());
        if (!request.getHeaders().hasIgnoreCase(Constants.HTTP_HEADER_HOOK_HEADER_KEY)) {
            SwingTools.showInfoDialog("The request is not decrypted.");
            return;
        }
        WorkExecutor.INSTANCE.execute(() -> HttpClient.send(request, config.getOption().getScannerConn()));
    }

}
