package org.m2sec.panels.httphook;

import burp.api.montoya.MontoyaApi;
import org.m2sec.core.common.CacheInfo;
import org.m2sec.core.common.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 22:24
 * @description:
 */

public class GrpcJPanel extends JPanel {
    private final CacheInfo cache;

    private final MontoyaApi api;

    private final JTextField grpcConnTextField = new JTextField(20);


    public GrpcJPanel(CacheInfo cache, MontoyaApi api) {
        this.cache = cache;
        this.api = api;
        initPanel();
    }

    private void initPanel() {
//        setBackground(Color.red);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new BorderLayout());
        JPanel grpcConnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel descPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel descLabel = new JLabel(Constants.HTTP_HOOK_GRPC_DEF);
        descPanel.add(descLabel);
        JLabel label = new JLabel("GRPC Conn: ");
        grpcConnPanel.add(label);
        grpcConnPanel.add(grpcConnTextField);

        add(descPanel, BorderLayout.NORTH);
        add(grpcConnPanel, BorderLayout.CENTER);
        setData();
    }

    private void setData() {
        grpcConnTextField.setText(cache.getRpcConn());
    }

    public String getData() {
        return grpcConnTextField.getText();
    }
}
