package org.m2sec.abilities.menus;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import org.m2sec.core.common.CompatTools;
import org.m2sec.core.common.Config;
import org.m2sec.core.common.Constants;
import org.m2sec.core.enums.Method;
import org.m2sec.core.models.Query;
import org.m2sec.core.models.Request;
import org.m2sec.core.utils.JsonUtil;

import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class JsonToQueryMenuItem extends IItem {

    public JsonToQueryMenuItem(MontoyaApi api, Config config) {
        super(api, config);
    }

    public String displayName() {
        return "Json To Query";
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
        Map<String, Object> bodyMap = JsonUtil.fromJsonStr(new String(request.getContent()), Map.class);
        Query query = new Query();
        query.putAll(CompatTools.mapToMultiMap(bodyMap));
        request.getQuery().merge(query);
        request.setMethod(Method.GET.toString()).setContent(new byte[]{});
        request.getHeaders().removeIgnoreCase(Constants.HTTP_HEADER_CONTENT_LENGTH);
        request.getHeaders().removeIgnoreCase(Constants.HTTP_HEADER_CONTENT_TYPE);
        messageEditorHttpRequestResponse.setRequest(request.toBurp());
    }
}
