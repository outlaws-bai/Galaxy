package org.m2sec.panels.httphook;

import burp.api.montoya.MontoyaApi;
import org.m2sec.core.common.Config;
import org.m2sec.core.enums.HttpHookService;
import org.m2sec.core.httphook.HttpHooker;

import javax.swing.*;
import java.awt.*;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 22:24
 * @description:
 */

public class HttpHookerPanel extends IHookerPanel<HttpHooker> {

    public final JTextField httpServerTextField = new JTextField();

    public HttpHookerPanel(Config config, MontoyaApi api, HttpHookService service) {
        super(config, api, service);
        initPanel();
    }


    private void initPanel() {
        setLayout(new BorderLayout());
        JPanel httpConnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("Http Conn:");
        label.setPreferredSize(new Dimension(CodeFileHookerPanel.getDescWidth(), label.getPreferredSize().height));
        label.setToolTipText("Please start a http server and enter its address below.");
        httpConnPanel.add(label);
        httpConnPanel.add(httpServerTextField);
        add(httpConnPanel, BorderLayout.CENTER);
        resetInput();
    }


    @Override
    public HttpHooker newHooker() {
        return new HttpHooker();
    }

    @Override
    public String getInput() {
        return httpServerTextField.getText();
    }

    @Override
    public void resetInput() {
        httpServerTextField.setText(config.getOption().getHttpServer());
    }
}
