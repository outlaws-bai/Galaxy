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
import org.m2sec.common.utils.CompatUtil;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class JsonToQueryMenuItem extends AbstractMenuItem {

    public String displayName() {
        return "JsonToQuery";
    }

    @Override
    public boolean isDisplay(ContextMenuEvent event) {
        return event.isFromTool(ToolType.REPEATER)
            && event.messageEditorRequestResponse().isPresent()
            && event.messageEditorRequestResponse().get().selectionContext().equals(MessageEditorHttpRequestResponse.SelectionContext.REQUEST);
    }

    @Override
    @SuppressWarnings({"OptionalGetWithoutIsPresent", "unchecked"})
    public void action(ContextMenuEvent event) {
        MessageEditorHttpRequestResponse messageEditorHttpRequestResponse = event.messageEditorRequestResponse().get();
        Request request = Request.of(messageEditorHttpRequestResponse.requestResponse().request());
        Map<String, Object> bodyMap = JsonParser.fromJsonStr(new String(request.getContent()), Map.class);
        Query query = new Query();
        query.putAll(CompatUtil.mapToMultiMap(bodyMap));
        request.getQuery().merge(query);
        request.setMethod(Method.GET.toString()).setContent(new byte[]{});
        request.getHeaders().removeIgnoreCase(Constants.HTTP_HEADER_CONTENT_LENGTH);
        request.getHeaders().removeIgnoreCase(Constants.HTTP_HEADER_CONTENT_TYPE);
        messageEditorHttpRequestResponse.setRequest(request.toBurp());
    }
}
