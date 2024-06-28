package org.m2sec.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.m2sec.common.parsers.JsonParser;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Slf4j
public class CompatUtil {




    public static Map<String, List<String>> mapToMultiMap(Map<String, Object> map) {
        HashMap<String, List<String>> retVal = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map<?, ?> || entry.getValue() instanceof List<?>)
                retVal.put(entry.getKey(), new ArrayList<>(List.of(JsonParser.toJsonStr(entry.getValue()))));
            else retVal.put(entry.getKey(), new ArrayList<>(List.of(entry.getValue().toString())));
        }
        return retVal;
    }

    public static void copyToClipboard(byte[] data) {
        StringSelection stringSelection = new StringSelection(new String(data));
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public static void copyToClipboard(String str) {
        StringSelection stringSelection = new StringSelection(str);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public static void openWithBrowser(String url) {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void openFileManager(String filePath) {
        File folder = new File(filePath);
        try {
            // 检查桌面功能是否可用
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                // 打开文件夹
                Desktop.getDesktop().open(folder);
            } else {
                log.error("Failed to open file manager or does not support desktop related features.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Open file manager failed： " + e.getMessage());
        }
    }

    public static int getCPUCount() {
        return Runtime.getRuntime().availableProcessors();
    }
}
