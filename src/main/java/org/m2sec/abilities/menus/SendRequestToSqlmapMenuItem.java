package org.m2sec.abilities.menus;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import org.m2sec.core.common.*;
import org.m2sec.core.models.Request;
import org.m2sec.core.utils.FactorUtil;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author: outlaws-bai
 * @date: 2024/8/30 0:07
 * @description:
 */

public class SendRequestToSqlmapMenuItem extends IItem {

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
        run(cmd);
    }

    public static void run(String cmd) {
        String osName = System.getProperty("os.name").toLowerCase();
        List<String> commandList = new ArrayList<>();
        if (osName.contains("windows")) {
            commandList.add("cmd.exe");
            commandList.add("/c");
            commandList.add("start");
            String filepath = Constants.TMP_FILE_DIR + File.separator + FactorUtil.uuid() + ".bat";
            FileTools.writeFile(filepath, cmd);
            commandList.add(filepath);
        } else if (osName.contains("mac")) {
            commandList.add("osascript");
            commandList.add("-e");
            String macCmd = """
                    tell application "Terminal"\s
                            activate
                            do script "%s"
                    end tell""";
            commandList.add(String.format(macCmd, cmd));
        } else if (osName.contains("linux")) {
            commandList.add("/bin/sh");
            commandList.add("-c");
            commandList.add("gnome-terminal");
            CompatTools.copyToClipboard(cmd);
            SwingTools.showInfoDialog("The command has been copied to the clipboard. Please open the command line to " +
                    "execute it");
        } else {
            commandList.add("/bin/bash");
            commandList.add("-c");
            commandList.add(cmd);
        }

        ProcessBuilder processBuilder = new ProcessBuilder(commandList);
        try {
            processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
