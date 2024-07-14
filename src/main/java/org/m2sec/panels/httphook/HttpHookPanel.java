package org.m2sec.panels.httphook;

import burp.api.montoya.MontoyaApi;
import lombok.extern.slf4j.Slf4j;
import org.m2sec.Galaxy;
import org.m2sec.core.common.Helper;
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
    private final MontoyaApi api;

    public HttpHookPanel(MontoyaApi api, Option option) {
        this.option = option;
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

        GrpcHookerPanel rpcImpl = new GrpcHookerPanel(option, api, HttpHookService.GRPC);
        CodeFileHookerPanel javaFileHookerPanel = new CodeFileHookerPanel(option, api, HttpHookService.JAVA);
        javaFileHookerPanel.resetCodeTheme();
        CodeFileHookerPanel pythonFileHookerPanel = new CodeFileHookerPanel(option, api, HttpHookService.PYTHON);
        pythonFileHookerPanel.resetCodeTheme();
        CodeFileHookerPanel jsFileHookerPanel = new CodeFileHookerPanel(option, api, HttpHookService.JS);
        jsFileHookerPanel.resetCodeTheme();

        hookNames.add(HttpHookService.JS.name());
        hookNames.add(HttpHookService.PYTHON.name());
        hookNames.add(HttpHookService.GRPC.name());
        hookNames.add(HttpHookService.JAVA.name());
        serviceMap.put(Helper.capitalizeFirstLetter(HttpHookService.JS.name()), jsFileHookerPanel);
        serviceMap.put(Helper.capitalizeFirstLetter(HttpHookService.PYTHON.name()), pythonFileHookerPanel);
        serviceMap.put(Helper.capitalizeFirstLetter(HttpHookService.GRPC.name()), rpcImpl);
        serviceMap.put(Helper.capitalizeFirstLetter(HttpHookService.JAVA.name()), javaFileHookerPanel);

        // 创建一个容器(卡片)用于放置不同方式的JPanel
        JPanel wayPanelContainer = new JPanel(new CardLayout());
        serviceMap.forEach((k, v) -> wayPanelContainer.add(v, k));

        // 创建一个控制面板，放置选择哪种方式Hook的JComboBox
        JPanel wayControlPanel = new JPanel(new BorderLayout());
        JPanel descAndComboBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel selectDesc = new JLabel("Hooker: ");
        selectDesc.setToolTipText("Choose a hooker.");
        JComboBox<String> comboBox = new JComboBox<>(serviceMap.keySet().toArray(String[]::new));
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        descAndComboBox.add(selectDesc);
        descAndComboBox.add(comboBox);
        wayControlPanel.add(descAndComboBox, BorderLayout.NORTH);
        wayControlPanel.add(separator, BorderLayout.CENTER);

        // 创建一个控制面板，放置启动开关、检查表达式，checkboxes
        JPanel nextControlPanel = new JPanel(new BorderLayout());
        nextControlPanel.setVisible(false);
        JPanel switchAndCheckBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton switchButton = new JButton(Helper.capitalizeFirstLetter(RunStatus.START.name()));
        switchButton.setToolTipText("Start hook...");
        JCheckBox hookRequestCheckBox = new JCheckBox("Hook request");
        hookRequestCheckBox.setToolTipText("HTTP requests need to be hook?");
        JCheckBox hookResponseCheckBox = new JCheckBox("Hook response");
        hookResponseCheckBox.setToolTipText("HTTP responses need to be hook?");
        switchAndCheckBoxPanel.add(switchButton);
        switchAndCheckBoxPanel.add(hookRequestCheckBox);
        switchAndCheckBoxPanel.add(hookResponseCheckBox);
        JPanel wayDescAndSwitchAndCheckBox = new JPanel(new BorderLayout());
        wayDescAndSwitchAndCheckBox.add(switchAndCheckBoxPanel, BorderLayout.WEST);
        nextControlPanel.add(wayDescAndSwitchAndCheckBox, BorderLayout.NORTH);


        // 创建面板，用于输入检查当前请求是否需要被Hook的表达式
        JPanel requestCheckPanel = new JPanel(new BorderLayout());
        JTextField checkELTextField = new JTextField();
        JPanel checkELPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel elLabel = new JLabel("Expression: ");
        elLabel.setToolTipText("Enter an javascript expression that will be used to determine which requests need to " +
            "be processed.");

        checkELPanel.add(elLabel);
        checkELPanel.add(checkELTextField);
        requestCheckPanel.add(checkELPanel, BorderLayout.CENTER);
        nextControlPanel.add(requestCheckPanel, BorderLayout.CENTER);

        // 创建面板，组合wayPanelContainer和requestIsHookPanel
        JPanel blendPanel = new JPanel(new BorderLayout());
        blendPanel.add(wayPanelContainer, BorderLayout.CENTER);

        JPanel wayAndNextPanel = new JPanel(new BorderLayout());
        wayAndNextPanel.add(wayControlPanel, BorderLayout.NORTH);
        wayAndNextPanel.add(nextControlPanel, BorderLayout.CENTER);
        add(wayAndNextPanel, BorderLayout.NORTH);
        add(blendPanel, BorderLayout.CENTER);

        // 设置 JComboBox 的事件监听器, 选择不同的方式，展示不同方式自己的Panel
        comboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                wayPanelContainer.setVisible(true);
                requestCheckPanel.setVisible(true);
                nextControlPanel.setVisible(true);
                hookRequestCheckBox.setVisible(true);
                hookResponseCheckBox.setVisible(true);
                CardLayout cl = (CardLayout) (wayPanelContainer.getLayout());
                cl.show(wayPanelContainer, (String) e.getItem());
            } else {
                wayPanelContainer.setVisible(false);
                requestCheckPanel.setVisible(false);
                nextControlPanel.setVisible(false);
                hookRequestCheckBox.setVisible(false);
                hookResponseCheckBox.setVisible(false);
            }
        });
        // 设置 switchButton 的事件监听器, 开关HttpHook功能
        switchButton.addActionListener(e -> {
            String selectItem = (String) comboBox.getSelectedItem();
            IHookerPanel<?> hookerPanel = serviceMap.get(selectItem);
            boolean isStop = switchButton.getText().equalsIgnoreCase(RunStatus.STOP.name().toLowerCase());
            String text = isStop ? RunStatus.START.name() : RunStatus.STOP.name();

            try {
                if (!isStop) {
                    HttpHookService service = hookerPanel.getService();
                    // 设置本次所选择的配置
                    option.setHookStart(true)
                        .setHookService(service)
                        .setRequestCheckExpression(checkELTextField.getText())
                        .setHookRequest(hookRequestCheckBox.isSelected())
                        .setHookResponse(hookResponseCheckBox.isSelected())
                        .setGrpcConn(rpcImpl.getInput())
                        .setCodeSelectItem(hookerPanel.getInput());
                    hookerPanel.start(option);
                } else {
                    hookerPanel.stop(option);
                }
            } catch (Exception exc) {
                log.error("Start fail!", exc);
                SwingTools.showErrorDetailDialog(exc);
                return;
            }

            switchButton.setText(Helper.capitalizeFirstLetter(text));
            SwingTools.changePanelStatus(hookerPanel, isStop);
            SwingTools.changeComponentStatus(comboBox, isStop);
            SwingTools.changePanelStatus(requestCheckPanel, isStop);
            SwingTools.changeComponentStatus(hookRequestCheckBox, isStop);
            SwingTools.changeComponentStatus(hookResponseCheckBox, isStop);
        });

        // set data
        checkELTextField.setText(option.getRequestCheckExpression());
        hookRequestCheckBox.setSelected(option.isHookRequest());
        hookResponseCheckBox.setSelected(option.isHookResponse());
        if (option.getHookService() != null) {
            comboBox.setSelectedIndex(hookNames.indexOf(option.getHookService().name()));
        } else {
            comboBox.setSelectedIndex(-1);
        }

//        nextControlPanel.setBackground(Color.CYAN);
//        wayDescAndSwitchAndCheckBox.setBackground(Color.green);
//        requestCheckPanel.setBackground(Color.gray);
//        wayControlPanel.setBackground(Color.green);

    }

}
