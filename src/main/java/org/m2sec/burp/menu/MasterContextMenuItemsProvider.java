package org.m2sec.burp.menu;

import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import org.m2sec.modules.bypass.menu.BypassMenu;
import org.m2sec.modules.fuzz.menu.FuzzMenu;
import org.m2sec.modules.mixed.menu.MixedMenu;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class MasterContextMenuItemsProvider implements ContextMenuItemsProvider {

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event) {
        ArrayList<Component> menuList = new ArrayList<>();
        List<AbstractMenu> menus =
                new ArrayList<>(List.of(new MixedMenu(), new BypassMenu(), new FuzzMenu()));
        menus.stream().filter(menu -> menu.isDisplay(event)).forEach(menuList::add);
        return menuList;
    }
}
