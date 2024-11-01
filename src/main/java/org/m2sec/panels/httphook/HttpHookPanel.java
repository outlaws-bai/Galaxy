package org.m2sec.panels.httphook;

import burp.api.montoya.MontoyaApi;
import lombok.extern.slf4j.Slf4j;
import org.m2sec.Galaxy;
import org.m2sec.core.common.Config;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.Option;
import org.m2sec.core.enums.HttpHookService;
import org.m2sec.core.enums.RunStatus;
import org.m2sec.core.common.SwingTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 21:04
 * @description:
 */
@Slf4j
public class HttpHookPanel extends JPanel {

    private final Option option;
    private final Config config;
    private final MontoyaApi api;

    public HttpHookPanel(MontoyaApi api, Config config) {
        this.config = config;
        this.option = config.getOption();
        this.api = api;
        setName("Http Hook");
        initPanel();
    }

    private void initPanel() {
        if (Galaxy.isInBurp()) api.userInterface().applyThemeToComponent(this);
        setLayout(new BorderLayout());
        // 存放几种hook方式
        Map<String, IHookerPanel<?>> serviceMap = new LinkedHashMap<>();
        List<String> hookNames = new ArrayList<>();

        HttpHookerPanel httpImpl = new HttpHookerPanel(config, api, HttpHookService.HTTP);
        hookNames.add(HttpHookService.HTTP.name().toLowerCase());
        serviceMap.put(HttpHookService.HTTP.name().toLowerCase(), httpImpl);

        if (Constants.hasJs) {
            CodeFileHookerPanel jsFileHookerPanel = new CodeFileHookerPanel(config, api, HttpHookService.JS);
            jsFileHookerPanel.resetCodeTheme();
            hookNames.add(HttpHookService.JS.name().toLowerCase());
            serviceMap.put(HttpHookService.JS.name().toLowerCase(), jsFileHookerPanel);
        }
        if (Constants.hasGraalpy) {
            CodeFileHookerPanel pythonFileHookerPanel = new CodeFileHookerPanel(config, api, HttpHookService.GRAALPY);
            pythonFileHookerPanel.resetCodeTheme();
            hookNames.add(HttpHookService.GRAALPY.name().toLowerCase());
            serviceMap.put(HttpHookService.GRAALPY.name().toLowerCase(), pythonFileHookerPanel);
        }
        if (Constants.hasJython) {
            CodeFileHookerPanel pythonFileHookerPanel = new CodeFileHookerPanel(config, api, HttpHookService.JYTHON);
            pythonFileHookerPanel.resetCodeTheme();
            hookNames.add(HttpHookService.JYTHON.name().toLowerCase());
            serviceMap.put(HttpHookService.JYTHON.name().toLowerCase(), pythonFileHookerPanel);
        }

//        if (Constants.isUseJdk) {
//            CodeFileHookerPanel javaFileHookerPanel = new CodeFileHookerPanel(config, api, HttpHookService.JAVA);
//            javaFileHookerPanel.resetCodeTheme();
//            hookNames.add(HttpHookService.JAVA.name().toLowerCase());
//            serviceMap.put(HttpHookService.JAVA.name().toLowerCase(), javaFileHookerPanel);
//        }

        // 创建一个容器(卡片)用于放置不同方式的JPanel
        JPanel hookerPanelContainer = new JPanel(new CardLayout());
        serviceMap.forEach((k, v) -> hookerPanelContainer.add(v, k));

        // 创建一个hook方式选择的面板，放置选择哪种方式Hook的JComboBox及描述
        JPanel hookerChoosePanel = new JPanel(new BorderLayout());
        JPanel descAndComboBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel selectDesc = new JLabel("Hooker: ");
        selectDesc.setToolTipText("Choose a hooker.");
        JComboBox<String> comboBox = new JComboBox<>(serviceMap.keySet().toArray(String[]::new));
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        descAndComboBox.add(selectDesc);
        descAndComboBox.add(comboBox);
        hookerChoosePanel.add(descAndComboBox, BorderLayout.NORTH);
        hookerChoosePanel.add(separator, BorderLayout.CENTER);

        // 创建一个控制面板，放置启动开关，两个hook的checkbox
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton switchButton = new JButton(RunStatus.START.getDisplay());
        switchButton.setFont(new Font(switchButton.getFont().getName(), Font.BOLD, switchButton.getFont().getSize()));
        switchButton.setForeground(new Color(255, 255, 255));
        switchButton.setBackground(new Color(255, 121, 76));
        switchButton.setToolTipText("Start hook...");
        JCheckBox hookResponseCheckBox = new JCheckBox("Hook Response");
        hookResponseCheckBox.setToolTipText("HTTP responses need to be hook?");
        JCheckBox autoForwardRequestCheckBox = new JCheckBox("Auto Scan Decrypted Request");
        autoForwardRequestCheckBox.setToolTipText("Auto Forward Request To Passive Proxy Scanner?");
        controlPanel.add(switchButton);
        controlPanel.add(hookResponseCheckBox);
        controlPanel.add(autoForwardRequestCheckBox);

        // 创建一个输入面板，放置表达式的输入框
        JPanel inputPanel = new JPanel(new BorderLayout());
        JPanel expressionInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel elLabel = new JLabel("Expression:");
        elLabel.setPreferredSize(new Dimension(CodeFileHookerPanel.getDescWidth(), elLabel.getPreferredSize().height));
        elLabel.setToolTipText("Enter an javascript expression that will be used to determine which requests need to " +
            "be processed.");
        JTextField checkELTextField = new JTextField();
        expressionInputPanel.add(elLabel);
        expressionInputPanel.add(checkELTextField);
        inputPanel.add(expressionInputPanel, BorderLayout.CENTER);

        // 创建一个汇总面板，组合上方的面板
        JPanel gatherPanel = new JPanel(new BorderLayout());
        JPanel hookerAndControlPanel = new JPanel(new BorderLayout());
        hookerAndControlPanel.add(hookerChoosePanel, BorderLayout.NORTH);
        hookerAndControlPanel.add(controlPanel, BorderLayout.CENTER);
        JPanel inputAndCodePanel = new JPanel(new BorderLayout());
        inputAndCodePanel.add(inputPanel, BorderLayout.NORTH);
        inputAndCodePanel.add(hookerPanelContainer, BorderLayout.CENTER);
        gatherPanel.add(hookerAndControlPanel, BorderLayout.NORTH);
        gatherPanel.add(inputAndCodePanel, BorderLayout.CENTER);
        add(gatherPanel);

        // 设置 JComboBox 的事件监听器, 选择不同的方式，展示不同方式自己的Panel
        comboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                controlPanel.setVisible(true);
                inputPanel.setVisible(true);
                hookerPanelContainer.setVisible(true);
                CardLayout cl = (CardLayout) (hookerPanelContainer.getLayout());
                cl.show(hookerPanelContainer, (String) e.getItem());
            }
        });
        // 设置 switchButton 的事件监听器, 开关HttpHook功能
        switchButton.addActionListener(e -> {
            switchButton.setEnabled(false);
            String selectItem = (String) comboBox.getSelectedItem();
            IHookerPanel<?> hookerPanel = serviceMap.get(selectItem);
            boolean toStop = option.isHookStart();
            RunStatus status = toStop ? RunStatus.START : RunStatus.STOP;

            try {
                if (!toStop) {
                    // check input
                    if (checkELTextField.getText().isBlank()) {
                        throw new Exception("Please input request check expression!");
                    }

                    HttpHookService service = hookerPanel.getService();
                    // 设置本次所选择的配置
                    option.setHookStart(true)
                        .setHookService(service)
                        .setRequestCheckExpression(checkELTextField.getText())
                        .setHookResponse(hookResponseCheckBox.isSelected())
                        .setHttpServer(httpImpl.getInput())
                        .setCodeSelectItem(hookerPanel.getInput())
                        .setAutoForwardRequest(autoForwardRequestCheckBox.isSelected());
                    hookerPanel.start(config);
                    log.info("Start http hook success. service: {}", service.name().toLowerCase());
                } else {
                    option.setHookStart(false);
                    hookerPanel.stop();
                    log.info("Stop http hook success.");
                }
            } catch (Exception exc) {
                switchButton.setEnabled(true);
                option.setHookStart(false);
                log.error("Start fail!", exc);
                SwingTools.showErrorStackTraceDialog(exc);
                return;
            }

            switchButton.setText(status.getDisplay());
            SwingTools.changePanelStatus(gatherPanel, toStop);
            switchButton.setEnabled(true);
        });

        // set data
        checkELTextField.setText(option.getRequestCheckExpression());
        hookResponseCheckBox.setSelected(option.isHookResponse());
        autoForwardRequestCheckBox.setSelected(option.isAutoForwardRequest());
        int width = ((checkELTextField.getPreferredSize().width + 99) / 100) * 100;
        checkELTextField.setPreferredSize(new Dimension(width, checkELTextField.getPreferredSize().height));
        if (option.getHookService() != null) {
            comboBox.setSelectedIndex(hookNames.indexOf(option.getHookService().name().toLowerCase()));
        } else {
            comboBox.setSelectedIndex(-1);
            controlPanel.setVisible(false);
            inputPanel.setVisible(false);
            hookerPanelContainer.setVisible(false);
        }

    }

}
