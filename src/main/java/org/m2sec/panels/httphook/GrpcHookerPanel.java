package org.m2sec.panels.httphook;

import burp.api.montoya.MontoyaApi;
import org.m2sec.core.common.Config;
import org.m2sec.core.common.Constants;
import org.m2sec.core.enums.HttpHookService;
import org.m2sec.core.httphook.GRpcHooker;

import javax.swing.*;
import java.awt.*;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 22:24
 * @description:
 */

public class GrpcHookerPanel extends IHookerPanel<GRpcHooker> {

    public final JTextField grpcConnTextField = new JTextField();

    public GrpcHookerPanel(Config config, MontoyaApi api, HttpHookService service) {
        super(config, api, service);
        initPanel();
    }


    private void initPanel() {
        setLayout(new BorderLayout());
        JPanel grpcConnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("GRPC Conn:");
        label.setPreferredSize(new Dimension(CodeFileHookerPanel.getDescWidth(), label.getPreferredSize().height));
        label.setToolTipText(Constants.HOOK_BY_GRPC_IMPL_DEF);
        grpcConnPanel.add(label);
        grpcConnPanel.add(grpcConnTextField);

        add(grpcConnPanel, BorderLayout.CENTER);
        resetInput();
    }


    @Override
    public GRpcHooker newHooker() {
        return new GRpcHooker();
    }

    @Override
    public String getInput() {
        return grpcConnTextField.getText();
    }

    @Override
    public void resetInput() {
        grpcConnTextField.setText(config.getOption().getGrpcConn());
    }
}
