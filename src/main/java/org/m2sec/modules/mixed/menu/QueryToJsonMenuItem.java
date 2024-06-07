package org.m2sec.modules.mixed.menu;

import burp.api.montoya.core.ToolType;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;

import org.m2sec.burp.menu.AbstractMenuItem;
import org.m2sec.common.Constants;
import org.m2sec.common.enums.Method;
import org.m2sec.common.models.Query;
import org.m2sec.common.models.Request;
import org.m2sec.common.parsers.JsonParser;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class QueryToJsonMenuItem extends AbstractMenuItem {

    public String displayName() {
        return "QueryToJson";
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
        Request request =
                Request.of(messageEditorHttpRequestResponse.requestResponse().request());
        request.setMethod(Method.POST.toString());
        request.setContent(JsonParser.toJsonStr(request.getQuery().toSimple()).getBytes())
                .updateContentLength()
                .setQuery(new Query());
        request.getHeaders().put(Constants.HTTP_HEADER_CONTENT_TYPE, "application/json");
        messageEditorHttpRequestResponse.setRequest(request.toBurp());
    }
}
