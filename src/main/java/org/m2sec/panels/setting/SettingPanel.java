package org.m2sec.panels.setting;

import burp.api.montoya.MontoyaApi;
import org.m2sec.core.common.CompatTools;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.Setting;

import javax.swing.*;
import javax.swing.text.View;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 21:05
 * @description:
 */

public class SettingPanel extends JPanel {
    private final Setting setting;

    public SettingPanel(MontoyaApi api, Setting setting) {
        this.setting = setting;
        setName("Setting");
        initPanel();
    }

    private void initPanel() {
        JButton openDir = new JButton("Open work dir");
        openDir.addActionListener(e -> CompatTools.openFileManager(Constants.WORK_DIR));
        add(openDir);
    }
}
