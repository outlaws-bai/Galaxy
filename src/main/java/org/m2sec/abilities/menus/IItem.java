package org.m2sec.abilities.menus;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import lombok.extern.slf4j.Slf4j;
import org.m2sec.core.common.Config;
import org.m2sec.core.common.SwingTools;

import javax.swing.*;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Slf4j
public abstract class IItem extends JMenuItem {

    protected final MontoyaApi api;

    protected final Config config;

    public IItem(MontoyaApi api,Config config) {
        this.api = api;
        this.config = config;
        this.setText(displayName());
    }

    public abstract String displayName();

    public abstract boolean isDisplay(ContextMenuEvent event);

    public void safeAction(ContextMenuEvent event) {
        try {
            this.action(event);
        } catch (Exception exc) {
            api.logging().logToError("action execute error."+ exc.getMessage(), exc);
            log.error("action execute error. {} .", exc.getMessage(), exc);
            SwingTools.showErrorDetailDialog(exc);
        }
    }

    public abstract void action(ContextMenuEvent event);
}
