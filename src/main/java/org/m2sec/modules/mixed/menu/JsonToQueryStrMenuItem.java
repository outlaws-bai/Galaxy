package org.m2sec.modules.mixed.menu;

import burp.api.montoya.core.ToolType;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;

import org.m2sec.burp.menu.AbstractMenuItem;
import org.m2sec.common.Constants;
import org.m2sec.common.enums.HttpMethod;
import org.m2sec.common.models.Request;
import org.m2sec.common.utils.HttpUtil;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class JsonToQueryStrMenuItem extends AbstractMenuItem {

    public String displayName() {
        return "Json To QueryStr";
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
        String queryStr = HttpUtil.jsonStrToQueryStr(new String(request.getContent()));
        if (!queryStr.isEmpty()) {
            if (!request.getQueryStr().isEmpty()) {
                request.setQueryStr(request.getQueryStr() + "&" + queryStr);
            } else {
                request.setQueryStr(queryStr);
            }
        }
        request.setMethod(HttpMethod.GET.toString())
                .setContent(new byte[] {})
                .removeHeader(Constants.HTTP_HEADER_CONTENT_LENGTH)
                .removeHeader(Constants.HTTP_HEADER_CONTENT_TYPE);
        messageEditorHttpRequestResponse.setRequest(request.toBurp());
    }
}
