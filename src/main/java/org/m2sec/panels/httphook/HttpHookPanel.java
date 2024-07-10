package org.m2sec.panels.httphook;

import burp.api.montoya.MontoyaApi;
import org.m2sec.core.common.CacheInfo;
import org.m2sec.core.enums.HttpHookWay;
import org.m2sec.core.enums.RunStatus;
import org.m2sec.panels.Tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 21:04
 * @description:
 */

public class HttpHookPanel extends JPanel {
    private final CacheInfo cache;
    private final MontoyaApi api;

    public HttpHookPanel(CacheInfo cache, MontoyaApi api) {
        this.cache = cache;
        this.api = api;
        setName("HttpHook");

        initPanel();

    }

    private void initPanel() {
        setLayout(new BorderLayout());

        // 存放几种hook方式
        Map<String, JPanel> panelMap = new LinkedHashMap<>();
        GrpcJPanel rpcPanel = new GrpcJPanel(cache,api);
        JavaJPanel javaJPanel = new JavaJPanel(cache,api);
        panelMap.put("...", new JPanel());
        panelMap.put(HttpHookWay.GRPC.name(), rpcPanel);
        panelMap.put(HttpHookWay.JAVA.name(), javaJPanel);

        // 创建一个容器(卡片)用于放置不同方式的JPanel
        JPanel wayPanelContainer = new JPanel(new CardLayout());
        panelMap.forEach((k, v) -> wayPanelContainer.add(v, k));

        // 创建一个控制面板，放置选择哪种方式Hook的JComboBox
        JPanel wayControlPanel = new JPanel(new BorderLayout());
        JPanel descAndComboBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel selectDesc = new JLabel("Select Way: ");
        JComboBox<String> comboBox = new JComboBox<>(panelMap.keySet().toArray(String[]::new));
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        descAndComboBox.add(selectDesc);
        descAndComboBox.add(comboBox);
        wayControlPanel.add(descAndComboBox, BorderLayout.NORTH);
        wayControlPanel.add(separator, BorderLayout.CENTER);

        // 创建一个控制面板，放置启动开关、检查表达式，checkboxes
        JPanel nextControlPanel = new JPanel(new BorderLayout());
        nextControlPanel.setVisible(false);
        JPanel switchAndCheckBoxPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        switchAndCheckBoxPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        JButton switchButton = new JButton(RunStatus.START.name().toLowerCase());
        JCheckBox hookRequestCheckBox = new JCheckBox("HookRequest");
        JCheckBox hookResponseCheckBox = new JCheckBox("HookResponse");
        switchAndCheckBoxPanel.add(hookRequestCheckBox);
        switchAndCheckBoxPanel.add(hookResponseCheckBox);
        switchAndCheckBoxPanel.add(switchButton);
        JPanel wayDescAndSwitchAndCheckBox = new JPanel(new BorderLayout());
        wayDescAndSwitchAndCheckBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        wayDescAndSwitchAndCheckBox.add(switchAndCheckBoxPanel, BorderLayout.EAST);
        nextControlPanel.add(wayDescAndSwitchAndCheckBox, BorderLayout.NORTH);


        // 创建面板，用于输入检查当前请求是否需要被Hook的表达式
        JPanel requestCheckPanel = new JPanel(new BorderLayout());
        requestCheckPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        JLabel elDescLabel = new JLabel("Please enter an expression that will be used to " +
            "determine which requests need to be processed.");
        JPanel elDescPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        elDescPanel.add(elDescLabel);
        JTextField checkELTextField = new JTextField();
        JPanel checkELPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel elLabel = new JLabel("expression: ");
        checkELPanel.add(elLabel);
        checkELPanel.add(checkELTextField);
        requestCheckPanel.add(elDescPanel, BorderLayout.NORTH);
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
                String selectedItem = (String) e.getItem();
                boolean flag = !selectedItem.equals(panelMap.keySet().iterator().next());
                nextControlPanel.setVisible(flag);
                CardLayout cl = (CardLayout) (wayPanelContainer.getLayout());
                cl.show(wayPanelContainer, selectedItem);
            }
        });
        // 设置 switchButton 的事件监听器, 开关HttpHook功能
        switchButton.addActionListener(e -> {
            boolean target = !switchButton.getText().equals(RunStatus.START.name().toLowerCase());
            String text = target ? RunStatus.START.name().toLowerCase() : RunStatus.STOP.name().toLowerCase();
            switchButton.setText(text);
            //noinspection SuspiciousMethodCalls
            Tools.changePanelStatus(panelMap.get(comboBox.getSelectedItem()), target);
            Tools.changePanelStatus(requestCheckPanel, target);
            if (!target) {
                cache.setHookWay(HttpHookWay.valueOf((String) comboBox.getSelectedItem()))
                    .setRequestCheckExpression(checkELTextField.getText())
                    .setHookRequest(hookRequestCheckBox.isSelected())
                    .setHookResponse(hookResponseCheckBox.isSelected())
                    .setRpcConn(rpcPanel.getData())
                    .setJavaSelectItem(javaJPanel.getData());
            }
        });

        // set data
        checkELTextField.setText(cache.getRequestCheckExpression());
        hookRequestCheckBox.setSelected(cache.isHookRequest());
        hookResponseCheckBox.setSelected(cache.isHookResponse());
        if (cache.getHookWay() != null) {
            comboBox.setSelectedItem(cache.getHookWay().name());
        }

//        nextControlPanel.setBackground(Color.CYAN);
//        wayDescAndSwitchAndCheckBox.setBackground(Color.green);
//        requestCheckPanel.setBackground(Color.gray);
//        wayControlPanel.setBackground(Color.green);

    }

}
