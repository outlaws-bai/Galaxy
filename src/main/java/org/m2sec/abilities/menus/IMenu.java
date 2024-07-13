package org.m2sec.abilities.menus;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import org.m2sec.core.common.Config;

import javax.swing.*;
import java.util.List;

/**
 * @author: outlaws-bai
 * @date: 2024/7/13 10:39
 * @description:
 */

public abstract class IMenu extends JMenu {

    protected final MontoyaApi api;

    protected final Config config;

    public IMenu(MontoyaApi api, Config config) {
        this.api = api;
        this.config = config;
        this.setText(displayName());
    }

    public abstract String displayName();

    public boolean isDisplay(ContextMenuEvent event) {
        boolean displayFlag = false;
        List<IMenu> menus = this.getSubMenus();
        List<IItem> menuItems = this.getSubItems();
        if (menus != null) {
            for (IMenu menu : menus) {
                if (menu.isDisplay(event)) {
                    add(menu);
                    displayFlag = true;
                }
            }
        }
        if (menuItems != null) {
            for (IItem menuItem : menuItems) {
                if (menuItem.isDisplay(event)) {
                    add(menuItem);
                    menuItem.addActionListener(e -> menuItem.safeAction(event));
                    displayFlag = true;
                }
            }
        }
        return displayFlag;
    }

    public abstract List<IMenu> getSubMenus();

    public abstract List<IItem> getSubItems();
}
