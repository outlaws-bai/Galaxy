package org.m2sec.panels;

import javax.swing.*;
import java.awt.*;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 22:29
 * @description:
 */

public class Tools {

    public static void enablePanel(JPanel panel) {
        changePanelStatus(panel, true);
    }

    public static void disablePanel(JPanel panel) {
        changePanelStatus(panel, false);
    }

    public static void changePanelStatus(JPanel panel, boolean target) {
        Component[] components = panel.getComponents();
        for (Component component : components) {
            if (!(component instanceof JPanel && ((JPanel) component).getComponent(0) instanceof JButton)) {
                component.setEnabled(target);
            }
        }
    }
}
