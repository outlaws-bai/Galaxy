package org.m2sec.burp.menu;

import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import org.m2sec.common.Log;

import javax.swing.*;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */

public abstract class AbstractMenuItem extends JMenuItem {

    private static final Log log = new Log(AbstractMenuItem.class);

    public AbstractMenuItem() {
        this.setText(displayName());
    }

    public abstract String displayName();

    public abstract boolean isDisplay(ContextMenuEvent event);

    public void safeAction(ContextMenuEvent event) {
        try {
            this.action(event);
        } catch (Exception exc) {
            log.exception(exc, "action execute error.");
            JOptionPane.showMessageDialog(
                    null, exc.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public abstract void action(ContextMenuEvent event);
}
