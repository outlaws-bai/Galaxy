package org.m2sec.panels.setting;

import burp.api.montoya.MontoyaApi;
import org.m2sec.core.common.Setting;

import javax.swing.*;
import java.awt.*;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 21:05
 * @description:
 */

public class SettingPanel extends JPanel {
    private final Setting setting;

    public SettingPanel(Setting setting, MontoyaApi api) {
        this.setting = setting;
        setName("Setting");
    }
}
