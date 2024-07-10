package org.m2sec.panels;


import burp.api.montoya.MontoyaApi;
import org.m2sec.Galaxy;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 22:29
 * @description:
 */

public class SwingTools {

    private static Icon icon;


    public static void changePanelStatus(Container panel, boolean target) {
        Component[] components = panel.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel chile) {
                changePanelStatus(chile, target);
            } else if (component instanceof Box chile) {
                changePanelStatus(chile, target);
            } else if (component instanceof JScrollPane chile) {
                changePanelStatus(chile, target);
            } else if (component instanceof JViewport chile) {
                changePanelStatus(chile, target);
            } else if (!(component instanceof JLabel)) {
                component.setEnabled(target);
            }
        }
    }

    public static void changeComponentStatus(Component component, boolean target) {
        component.setEnabled(target);
    }

    public static void addTipToLabel(JLabel label, String text, MontoyaApi api) {
        if (icon == null) {
            Color backgroud = Color.WHITE;
            if (Galaxy.isInBurp()) {
                backgroud = api.userInterface().swingUtils().suiteFrame().getBackground();
            }
            int width = 16;
            int height = 16;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();

            // 设置抗锯齿
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 绘制圆圈
            g2.setColor(backgroud);
            g2.fillOval(0, 0, width, height);
            g2.setColor(UIManager.getColor("Label.foreground"));
            g2.drawOval(0, 0, width - 1, height - 1);

            // 绘制感叹号
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.drawString("i", 6, 12);

            g2.dispose();
            icon = new ImageIcon(image);
        }
        label.setIcon(icon);
        label.setToolTipText(text);
    }

    public static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        // 将首字母大写，其他字母保持不变
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }


}
