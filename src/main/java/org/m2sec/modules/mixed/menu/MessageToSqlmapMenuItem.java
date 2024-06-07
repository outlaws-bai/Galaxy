package org.m2sec.modules.mixed.menu;

import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;

import org.m2sec.GalaxyMain;
import org.m2sec.burp.menu.AbstractMenuItem;
import org.m2sec.common.Constants;
import org.m2sec.common.config.MixedConfig;
import org.m2sec.common.models.Request;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class MessageToSqlmapMenuItem extends AbstractMenuItem {

    private static final String command = "%s -r %s %s";

    private static final String winCommand = "echo command: %s && %s && pause";

    private static final String unWinCommand =
            "echo command: %s && %s && read -p 'Press Enter to exit'";

    public String displayName() {
        return "MessageToSqlMap";
    }

    @Override
    public boolean isDisplay(ContextMenuEvent event) {
        return !GalaxyMain.config.getMixedConfig().getSqlMapConfig().getPath().isEmpty()
                && event.messageEditorRequestResponse().isPresent()
                && event.messageEditorRequestResponse()
                        .get()
                        .selectionContext()
                        .equals(MessageEditorHttpRequestResponse.SelectionContext.REQUEST);
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void action(ContextMenuEvent event) {
        // 写入请求到临时文件
        MessageEditorHttpRequestResponse messageEditorHttpRequestResponse =
                event.messageEditorRequestResponse().get();
        Request request =
                Request.of(messageEditorHttpRequestResponse.requestResponse().request());
        String tmpFilePath = Constants.TMP_FILE_DIR + File.separator + UUID.randomUUID() + ".txt";
        try (FileWriter fileWriter = new FileWriter(tmpFilePath)) {
            fileWriter.write(new String(request.toRaw()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MixedConfig.SqlMapConfig sqlMapConfig = GalaxyMain.config.getMixedConfig().getSqlMapConfig();
        String cmd = command.formatted(sqlMapConfig.getPath(), tmpFilePath, sqlMapConfig.getArg());
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
            processBuilder =
                    new ProcessBuilder(
                            "cmd", "/c", "start", "cmd", "/k", winCommand.formatted(cmd, cmd));
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            // Linux or macOS
            processBuilder =
                    new ProcessBuilder("xterm", "-e", "sh", "-c", unWinCommand.formatted(cmd, cmd));
        } else {
            throw new UnsupportedOperationException("Unsupported operating system");
        }
        return processBuilder;
    }
}
