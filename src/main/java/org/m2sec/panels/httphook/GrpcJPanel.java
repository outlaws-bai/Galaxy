package org.m2sec.panels.httphook;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 22:24
 * @description:
 */

public class GrpcJPanel extends JPanel {
    private final HashMap<String, Object> cache;

    public GrpcJPanel(HashMap<String, Object> cache) {
        this.cache = cache;
        initPanel();
    }

    private void initPanel() {
        setLayout(new BorderLayout());
        JPanel grpcConnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("GRPC Conn: ");
        JTextField grpcConnTextField = new JTextField(20);
        grpcConnPanel.add(label);
        grpcConnPanel.add(grpcConnTextField);

        add(new JLabel("This is GRPC"), BorderLayout.NORTH);
        add(grpcConnPanel, BorderLayout.CENTER);
//        setBackground(Color.blue);
    }
}
