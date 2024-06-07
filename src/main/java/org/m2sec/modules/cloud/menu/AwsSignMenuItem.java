package org.m2sec.modules.cloud.menu;

import burp.api.montoya.core.ToolType;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import org.m2sec.GalaxyMain;
import org.m2sec.burp.menu.AbstractMenuItem;
import org.m2sec.common.models.Request;
import org.m2sec.common.utils.CloudUtil;

/**
 * @author: outlaws-bai
 * @date: 2024/5/24 14:21
 * @description:
 */
public class AwsSignMenuItem extends AbstractMenuItem {
    @Override
    public String displayName() {
        return "AwsSign";
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
        CloudUtil.signAws(request, GalaxyMain.config.getCloudConfig().getAwsConfig());
        messageEditorHttpRequestResponse.setRequest(request.toBurp());
    }
}
