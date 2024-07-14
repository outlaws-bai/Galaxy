package org.m2sec.core.common;


import javax.swing.*;
import java.awt.*;

/**
 * @author: outlaws-bai
 * @date: 2024/7/9 22:29
 * @description:
 */

public class SwingTools {


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


    public static void showErrorDetailDialog(Exception e) {
        JOptionPane.showMessageDialog(null, e.getStackTrace(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfoDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }


    public static boolean showConfirmDialog(String message) {
        int result = JOptionPane.showConfirmDialog(null, message, "Please confirm again", JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    public static String showInputDialog(String message) {
        return JOptionPane.showInputDialog(null, message);
    }


}
