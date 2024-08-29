package org.m2sec.abilities.menus;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import org.m2sec.core.common.CompatTools;
import org.m2sec.core.common.Config;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.SwingTools;
import org.m2sec.core.models.Request;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

/**
 * @author: outlaws-bai
 * @date: 2024/8/30 0:07
 * @description:
 */

public class SendRequestToSqlmapMenuItem extends IItem{

    private static final String command = "%s -r %s %s";

    public SendRequestToSqlmapMenuItem(MontoyaApi api, Config config) {
        super(api, config);
    }

    @Override
    public String displayName() {
        return "Send Request To Sqlmap";
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
            SwingTools.showInfoDialog("The request is decrypted.");
            return;
        }
        // 写入请求到临时文件
        String tmpFilePath = Constants.TMP_FILE_DIR + File.separator + UUID.randomUUID() + ".txt";
        try (FileWriter fileWriter = new FileWriter(tmpFilePath)) {
            fileWriter.write(new String(request.toRaw()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String cmd = command.formatted(config.getSetting().getSqlmapExecutePath(), tmpFilePath,
            config.getSetting().getSqlmapExecuteArgs());
        CompatTools.copyToClipboard(cmd.getBytes());
        SwingTools.showInfoDialog("The command has been copied to the clipboard. Please open the command line to " +
            "execute it");
    }
}
