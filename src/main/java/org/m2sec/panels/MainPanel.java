package org.m2sec.panels;


import burp.api.montoya.MontoyaApi;
import org.m2sec.Galaxy;
import org.m2sec.core.common.Config;
import org.m2sec.core.common.SwingTools;
import org.m2sec.panels.httphook.HttpHookPanel;
import org.m2sec.panels.setting.SettingPanel;

import javax.swing.*;
import java.awt.*;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 0:42
 * @description:
 */

public class MainPanel extends JPanel {

    public MainPanel(MontoyaApi api, Config config) {
        SwingTools.patchSwingEnv();
        HttpHookPanel httpHookPanel = new HttpHookPanel(api, config);
        SettingPanel settingPanel = new SettingPanel(api, config.getSetting());
        if (Galaxy.isInBurp()) {
            api.userInterface().applyThemeToComponent(settingPanel);
        }
        JTabbedPane tabManager = new JTabbedPane();
        setLayout(new BorderLayout());
        tabManager.add(httpHookPanel);
        tabManager.add(settingPanel);
        add(tabManager);
    }
}
