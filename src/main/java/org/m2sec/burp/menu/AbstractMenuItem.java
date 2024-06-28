package org.m2sec.burp.menu;

import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Slf4j
public abstract class AbstractMenuItem extends JMenuItem {


    public AbstractMenuItem() {
        this.setText(displayName());
    }

    public abstract String displayName();

    public abstract boolean isDisplay(ContextMenuEvent event);

    public void safeAction(ContextMenuEvent event) {
        try {
            this.action(event);
        } catch (Exception exc) {
            log.error("action execute error. {} .", exc.getMessage(), exc);
            JOptionPane.showMessageDialog(null, exc.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public abstract void action(ContextMenuEvent event);
}
