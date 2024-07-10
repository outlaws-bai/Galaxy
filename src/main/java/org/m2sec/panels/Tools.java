package org.m2sec.panels;


import javax.swing.*;
import java.awt.*;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 22:29
 * @description:
 */

public class Tools {


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




}
