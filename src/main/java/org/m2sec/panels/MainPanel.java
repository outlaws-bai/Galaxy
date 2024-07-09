package org.m2sec.panels;


import javax.swing.*;
import java.awt.*;
import java.util.stream.Stream;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 0:42
 * @description:
 */

public class MainPanel extends JPanel {

    public MainPanel(JPanel... panels) {
        JTabbedPane tabManager = new JTabbedPane();
        Stream.of(panels).forEach(tabManager::add);
        setLayout(new BorderLayout());
        add(tabManager);
    }
}
