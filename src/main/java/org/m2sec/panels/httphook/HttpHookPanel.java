package org.m2sec.panels.httphook;

import org.m2sec.core.enums.RunStatus;
import org.m2sec.panels.Tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 21:04
 * @description:
 */

public class HttpHookPanel extends JPanel {
    private final HashMap<String, Object> cache;

    public HttpHookPanel(HashMap<String, Object> cache) {
        this.cache = cache;
        setName("HttpHook");

        initPanel();

    }

    private void initPanel() {
        setLayout(new BorderLayout());

        // 存放几种hook方式
        Map<String, JPanel> panelMap = new LinkedHashMap<>();
        panelMap.put("", new JPanel());
        panelMap.put("GRPC", new GrpcJPanel(cache));
        panelMap.put("JAVA", new JavaJPanel(cache));

        // 创建一个容器(卡片)用于放置不同方式的JPanel
        JPanel wayPanelContainer = new JPanel(new CardLayout());
        panelMap.forEach((k, v) -> wayPanelContainer.add(v, k));

        // 创建一个控制面板，放置 JComboBox 和按钮
        JPanel controlPanel = new JPanel(new BorderLayout());
        JComboBox<String> comboBox = new JComboBox<>(panelMap.keySet().toArray(String[]::new));
        JButton switchButton = new JButton(RunStatus.START.name().toLowerCase());
        switchButton.setVisible(false);
        controlPanel.add(comboBox, BorderLayout.WEST);
        controlPanel.add(switchButton, BorderLayout.EAST);

        // 创建面板，控制请求和相应是否需要处理
        JPanel checkBoxesPanel = new JPanel();
        checkBoxesPanel.setVisible(false);
        Box checkBoxes = Box.createVerticalBox();
        JCheckBox hookRequestCheckBox = new JCheckBox("HookRequest");
        JCheckBox hookResponseCheckBox = new JCheckBox("HookResponse");
        checkBoxes.add(hookRequestCheckBox);
        checkBoxes.add(hookResponseCheckBox);
        checkBoxesPanel.add(checkBoxes);

        // 创建面板，用于输入检查当前请求是否需要被Hook的表达式
        JPanel requestIsHookPanel = new JPanel(new BorderLayout());
        requestIsHookPanel.setVisible(false);
        Box box = Box.createHorizontalBox();
        JLabel requestCheckExpressionLanguageLabel = new JLabel("RequestCheckEL: ");
        JTextField checkELTextField = new JTextField();
        box.add(requestCheckExpressionLanguageLabel);
        box.add(checkELTextField);
        requestIsHookPanel.add(box);

        // 创建面板，组合wayPanelContainer和requestIsHookPanel
        JPanel blendPanel = new JPanel(new BorderLayout());
        blendPanel.add(requestIsHookPanel, BorderLayout.NORTH);
        blendPanel.add(wayPanelContainer, BorderLayout.CENTER);

//        controlPanel.setBackground(Color.green);
        add(controlPanel, BorderLayout.NORTH);
        add(checkBoxesPanel, BorderLayout.EAST);
        add(blendPanel, BorderLayout.CENTER);

        // 设置 JComboBox 的事件监听器, 选择不同的方式，展示不同方式自己的Panel
        comboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selectedItem = (String) e.getItem();
                boolean flag = !selectedItem.equals(panelMap.keySet().iterator().next());
                switchButton.setVisible(flag);
                requestIsHookPanel.setVisible(flag);
                checkBoxesPanel.setVisible(flag);
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
            Tools.changePanelStatus(requestIsHookPanel, target);
            Tools.changePanelStatus(checkBoxesPanel, target);
        });

        // 从缓存设置初始状态


    }

}
