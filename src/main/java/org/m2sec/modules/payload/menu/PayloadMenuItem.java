package org.m2sec.modules.payload.menu;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.core.Range;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import org.m2sec.burp.menu.AbstractMenuItem;
import org.m2sec.common.Render;
import org.m2sec.common.utils.BurpUtil;
import org.m2sec.common.utils.ByteUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class PayloadMenuItem extends AbstractMenuItem {
    private final String name;
    private final String value;

    private static final Map<String, Object> env = new HashMap<>(Map.of(BurpUtil.class.getSimpleName(),
        BurpUtil.class));

    public PayloadMenuItem(String name, String value) {
        this.name = name;
        this.value = value;
        this.setText(this.name);
    }

    @Override
    public String displayName() {
        return this.name;
    }

    @Override
    public boolean isDisplay(ContextMenuEvent event) {
        return event.isFromTool(ToolType.REPEATER) && event.messageEditorRequestResponse().isPresent() && event.messageEditorRequestResponse().get().selectionContext().equals(MessageEditorHttpRequestResponse.SelectionContext.REQUEST);
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void action(ContextMenuEvent event) {
        // 已check，因此不用关心是否以判断preset
        MessageEditorHttpRequestResponse messageEditorHttpRequestResponse = event.messageEditorRequestResponse().get();
        // 处理payload中的某些变量
        String finalValue = Render.renderTemplate(value, env);
        HttpRequest httpRequest;
        if (messageEditorHttpRequestResponse.selectionOffsets().isPresent()) {
            Range selectRange = messageEditorHttpRequestResponse.selectionOffsets().get();
            httpRequest =
                HttpRequest.httpRequest(ByteArray.byteArray(ByteUtil.replaceBytes(messageEditorHttpRequestResponse.requestResponse().request().toByteArray().getBytes(), selectRange.startIndexInclusive(), selectRange.endIndexExclusive(), finalValue.getBytes())));
        } else {
            int position = messageEditorHttpRequestResponse.caretPosition();
            httpRequest =
                HttpRequest.httpRequest(ByteArray.byteArray(ByteUtil.replaceBytes(messageEditorHttpRequestResponse.requestResponse().request().toByteArray().getBytes(), position, position, finalValue.getBytes())));
        }
        messageEditorHttpRequestResponse.setRequest(httpRequest);
    }
}
