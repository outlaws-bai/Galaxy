package org.m2sec.modules.payload.menu;

import burp.api.montoya.core.ToolType;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import com.google.gson.JsonElement;
import org.m2sec.burp.menu.AbstractMenuItem;
import org.m2sec.common.enums.ContentType;
import org.m2sec.common.models.Form;
import org.m2sec.common.models.Query;
import org.m2sec.common.models.Request;
import org.m2sec.common.parsers.JsonParser;
import org.m2sec.common.utils.HttpUtil;

import java.util.List;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class MultiPayloadMenuItem extends AbstractMenuItem {

    private final String name;

    private final String payload;

    public MultiPayloadMenuItem(String name, String payload) {
        this.name = name;
        this.payload = payload;
        this.setText(displayName());
    }

    @Override
    public String displayName() {
        return name;
    }

    @Override
    public boolean isDisplay(ContextMenuEvent event) {
        return event.isFromTool(ToolType.REPEATER) && event.messageEditorRequestResponse().isPresent() && event.messageEditorRequestResponse().get().selectionContext().equals(MessageEditorHttpRequestResponse.SelectionContext.REQUEST);
    }

    @Override
    @SuppressWarnings({"OptionalGetWithoutIsPresent", "unchecked"})
    public void action(ContextMenuEvent event) {
        MessageEditorHttpRequestResponse messageEditorHttpRequestResponse = event.messageEditorRequestResponse().get();
        Request request = Request.of(messageEditorHttpRequestResponse.requestResponse().request());
        Query newQuery = new Query((Map<String, List<String>>) HttpUtil.updateJsonValuesByMap(request.getQuery(),
            payload));
        request.setQuery(newQuery);
        ContentType contentType = request.getContentType();
        if (contentType == ContentType.FORM) {
            Form newForm =
                new Form((Map<String, List<String>>) HttpUtil.updateJsonValuesByMap(Form.of(new String(request.getContent())), payload));
            request.setContent(newForm.toRawString().getBytes());
        } else if (contentType == ContentType.JSON) {
            JsonElement jsonElement = JsonParser.fromJsonStr(new String(request.getContent()), JsonElement.class);
            HttpUtil.updateJsonValuesByJson(jsonElement, payload);
            request.setContent(jsonElement.toString().getBytes());
        }
        messageEditorHttpRequestResponse.setRequest(request.toBurp());
    }
}
