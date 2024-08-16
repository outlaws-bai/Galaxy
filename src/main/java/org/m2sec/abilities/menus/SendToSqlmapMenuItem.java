package org.m2sec.abilities.menus;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import org.m2sec.abilities.HttpHookHandler;
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
 * @date: 2024/6/21 20:23
 * @description:
 */
public class SendToSqlmapMenuItem extends IItem {

    private static final String command = "%s -r %s %s";

    private static final String winCommand = "echo command: %s && %s && pause";

    private static final String unWinCommand = "echo command: %s && %s && read -p 'Press Enter to exit'";

    public SendToSqlmapMenuItem(MontoyaApi api, Config config) {
        super(api, config);
    }

    public String displayName() {
        return "Send Decrypted Request To sqlmap";
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
        // 写入请求到临时文件
        String tmpFilePath = Constants.TMP_FILE_DIR + File.separator + UUID.randomUUID() + ".txt";
        try (FileWriter fileWriter = new FileWriter(tmpFilePath)) {
            fileWriter.write(new String(request.toRaw()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String cmd = command.formatted(config.getSetting().getSqlmapExecutePath(), tmpFilePath,
            config.getSetting().getSqlmapExecuteArgs());
        try {
            ProcessBuilder processBuilder = getProcessBuilder(cmd);
            processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ProcessBuilder getProcessBuilder(String cmd) {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder processBuilder;
        if (os.contains("win")) {
            // Windows
            processBuilder = new ProcessBuilder("cmd", "/c", "start", "cmd", "/k", winCommand.formatted(cmd, cmd));
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            // Linux or macOS
            processBuilder = new ProcessBuilder("xterm", "-e", "sh", "-c", unWinCommand.formatted(cmd, cmd));
        } else {
            throw new UnsupportedOperationException("Unsupported operating system");
        }
        return processBuilder;
    }
}
