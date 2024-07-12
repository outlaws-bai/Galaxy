package org.m2sec.panels.httphook;

import burp.api.montoya.MontoyaApi;
import org.m2sec.core.common.Option;
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
    private final Option option;

    private final MontoyaApi api;

    private final JTextField grpcConnTextField = new JTextField(20);


    public GrpcHookerPanel(Option option, MontoyaApi api) {
        this.option = option;
        this.api = api;
        initPanel();
    }

    private void initPanel() {
//        setBackground(Color.red);
        setLayout(new BorderLayout());
        JPanel grpcConnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("GRPC Conn: ");
        label.setToolTipText(Constants.HOOK_GRPC_IMPL_DEF);
        grpcConnPanel.add(label);
        grpcConnPanel.add(grpcConnTextField);

        add(grpcConnPanel, BorderLayout.CENTER);
        setPanelData();
    }

    private void setPanelData() {
        grpcConnTextField.setText(option.getGrpcConn());
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
        return HttpHookService.GRPC.name();
    }
}
