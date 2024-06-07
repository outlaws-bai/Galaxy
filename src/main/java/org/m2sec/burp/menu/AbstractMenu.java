package org.m2sec.burp.menu;

import burp.api.montoya.ui.contextmenu.ContextMenuEvent;

import javax.swing.*;
import java.util.List;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public abstract class AbstractMenu extends JMenu {

    public AbstractMenu() {
        this.setText(displayName());
    }

    public abstract String displayName();

    public boolean isDisplay(ContextMenuEvent event) {
        boolean displayFlag = false;
        List<AbstractMenu> menuClazzs = this.getSubMenus();
        List<AbstractMenuItem> itemClazzs = this.getSubMenuItems();
        if (menuClazzs != null) {
            for (AbstractMenu menu : menuClazzs) {
                if (menu.isDisplay(event)) {
                    add(menu);
                    displayFlag = true;
                }
            }
        }
        if (itemClazzs != null) {
            for (AbstractMenuItem menuItem : itemClazzs) {
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
