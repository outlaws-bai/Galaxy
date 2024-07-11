package org.m2sec.panels.httphook;

import burp.api.montoya.MontoyaApi;
import org.m2sec.core.common.CacheOption;
import org.m2sec.core.common.Constants;
import org.m2sec.core.enums.HttpHookWay;
import org.m2sec.core.httphook.GRpcHooker;
import org.m2sec.panels.SwingTools;

import javax.swing.*;
import java.awt.*;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 22:24
 * @description:
 */

public class GrpcImpl extends IHookService<GRpcHooker> {
    private final CacheOption cache;

    private final MontoyaApi api;

    private final JTextField grpcConnTextField = new JTextField(20);


    public GrpcImpl(CacheOption cache, MontoyaApi api) {
        this.cache = cache;
        this.api = api;
        initPanel();
    }

    private void initPanel() {
//        setBackground(Color.red);
        setLayout(new BorderLayout());
        JPanel grpcConnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("GRPC Conn: ");
        SwingTools.addTipToLabel(label, Constants.HTTP_HOOK_GRPC_DEF, api);
        grpcConnPanel.add(label);
        grpcConnPanel.add(grpcConnTextField);

        add(grpcConnPanel, BorderLayout.CENTER);
        setPanelData();
    }

    private void setPanelData() {
        grpcConnTextField.setText(cache.getGrpcConn());
    }

    public String getUserTypeData() {
        return grpcConnTextField.getText();
    }


    @Override
    public GRpcHooker newHooker() {
        return new GRpcHooker();
    }

    @Override
    public String displayName() {
        return HttpHookWay.GRPC.name();
    }
}
