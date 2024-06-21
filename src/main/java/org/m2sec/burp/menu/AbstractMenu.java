package org.m2sec.burp.menu;

import burp.api.montoya.ui.contextmenu.ContextMenuEvent;

import javax.swing.*;
import java.util.List;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public abstract class AbstractMenu extends JMenu {

    public AbstractMenu() {
        this.setText(displayName());
    }

    public abstract String displayName();

    public boolean isDisplay(ContextMenuEvent event) {
        boolean displayFlag = false;
        List<AbstractMenu> menus = this.getSubMenus();
        List<AbstractMenuItem> menuItems = this.getSubMenuItems();
        if (menus != null) {
            for (AbstractMenu menu : menus) {
                if (menu.isDisplay(event)) {
                    add(menu);
                    displayFlag = true;
                }
            }
        }
        if (menuItems != null) {
            for (AbstractMenuItem menuItem : menuItems) {
                if (menuItem.isDisplay(event)) {
                    add(menuItem);
                    menuItem.addActionListener(e -> menuItem.safeAction(event));
                    displayFlag = true;
                }
            }
        }
        return displayFlag;
    }

    public abstract List<AbstractMenu> getSubMenus();

    public abstract List<AbstractMenuItem> getSubMenuItems();
}
