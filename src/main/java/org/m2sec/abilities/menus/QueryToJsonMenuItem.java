package org.m2sec.abilities.menus;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import org.m2sec.core.common.Config;
import org.m2sec.core.common.Constants;
import org.m2sec.core.enums.ContentType;
import org.m2sec.core.enums.Method;
import org.m2sec.core.models.Query;
import org.m2sec.core.models.Request;
import org.m2sec.core.utils.JsonUtil;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class QueryToJsonMenuItem extends IItem {

    public QueryToJsonMenuItem(MontoyaApi api, Config config) {
        super(api, config);
    }

    public String displayName() {
        return "Query To Json";
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
        request.setMethod(Method.POST.toString());
        request.setContent(JsonUtil.toJsonStr(request.getQuery().toSimple()).getBytes()).updateContentLength().setQuery(new Query());
        request.getHeaders().put(Constants.HTTP_HEADER_CONTENT_TYPE, ContentType.JSON.getHeaderValuePrefix());
        messageEditorHttpRequestResponse.setRequest(request.toBurp());
    }
}
