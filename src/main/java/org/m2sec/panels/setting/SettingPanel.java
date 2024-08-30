package org.m2sec.panels.setting;

import burp.api.montoya.MontoyaApi;
import org.m2sec.core.common.CompatTools;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.Setting;
import org.m2sec.panels.httphook.CodeFileHookerPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;

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
        setLayout(new BorderLayout());
        int width = 500;

        // open work dir panel
        JPanel openWorkDirPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel openWorkDirLabel = new JLabel("Open Work Dir: ");
        JButton openWorkDirButton = new JButton("Open");
        openWorkDirPanel.add(openWorkDirLabel);
        openWorkDirPanel.add(openWorkDirButton);
        // parsed swagger request auto send panel
        JPanel parsedSwaggerRequestPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel parsedSwaggerRequestLabel = new JLabel("Parsed Swagger Api Doc Request Auto Send: ");
        JCheckBox parsedSwaggerRequestCheckBox = new JCheckBox();
        parsedSwaggerRequestPanel.add(parsedSwaggerRequestLabel);
        parsedSwaggerRequestPanel.add(parsedSwaggerRequestCheckBox);
        // passive proxy scanner
        JPanel passiveProxyConnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel scannerConnLabel = new JLabel("Passive Proxy Scanner:");
        scannerConnLabel.setToolTipText("Enter the connection string of a passive proxy scanner.");
        JTextField scannerConnInput = new JTextField();
        scannerConnInput.setPreferredSize(new Dimension(width, scannerConnInput.getPreferredSize().height));
        passiveProxyConnPanel.add(scannerConnLabel);
        passiveProxyConnPanel.add(scannerConnInput);
        // sqlmap
        JPanel sqlmapPathPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel sqlmapPathLabel = new JLabel("Sqlmap Execute Path: ");
        JTextField sqlmapPathInput = new JTextField();
        sqlmapPathInput.setPreferredSize(new Dimension(width, sqlmapPathInput.getPreferredSize().height));
        sqlmapPathPanel.add(sqlmapPathLabel);
        sqlmapPathPanel.add(sqlmapPathInput);
        JPanel sqlmapArgsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel sqlmapArgsLabel = new JLabel("Sqlmap Execute Args: ");
        JTextField sqlmapArgsInput = new JTextField();
        sqlmapArgsInput.setPreferredSize(new Dimension(width, sqlmapArgsInput.getPreferredSize().height));
        sqlmapArgsPanel.add(sqlmapArgsLabel);
        sqlmapArgsPanel.add(sqlmapArgsInput);
        // static ext
        JPanel staticExtensionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel staticExtensionsLabel = new JLabel("Static Extensions: ");
        JTextField staticExtensionsInput = new JTextField();
        staticExtensionsInput.setPreferredSize(new Dimension(1500, sqlmapArgsInput.getPreferredSize().height));
        staticExtensionsPanel.add(staticExtensionsLabel);
        staticExtensionsPanel.add(staticExtensionsInput);


        openWorkDirButton.addActionListener(e -> CompatTools.openFileManager(Constants.WORK_DIR));
        parsedSwaggerRequestCheckBox.addItemListener(e -> setting.setParsedSwaggerApiDocRequestAutoSend(e.getStateChange() == ItemEvent.SELECTED));
        scannerConnInput.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                setting.setScannerConn(scannerConnInput.getText());
            }
        });
        sqlmapPathInput.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                setting.setSqlmapExecutePath(sqlmapPathInput.getText());
            }
        });
        sqlmapArgsInput.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                setting.setSqlmapExecuteArgs(sqlmapArgsInput.getText());
            }
        });
        staticExtensionsInput.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                setting.setStaticExtensions(staticExtensionsInput.getText());
                Constants.HTTP_STATIC_EXTENSIONS = staticExtensionsInput.getText().split("\\|");
            }
        });

        // set data
        parsedSwaggerRequestCheckBox.setSelected(setting.isParsedSwaggerApiDocRequestAutoSend());
        sqlmapPathInput.setText(setting.getSqlmapExecutePath());
        sqlmapArgsInput.setText(setting.getSqlmapExecuteArgs());
        scannerConnInput.setText(setting.getScannerConn());
        staticExtensionsInput.setText(setting.getStaticExtensions());


        Box box = Box.createVerticalBox();
        box.add(openWorkDirPanel);
        box.add(parsedSwaggerRequestPanel);
        box.add(sqlmapPathPanel);
        box.add(sqlmapArgsPanel);
        box.add(passiveProxyConnPanel);
        box.add(staticExtensionsPanel);

        add(box, BorderLayout.NORTH);
    }
}
