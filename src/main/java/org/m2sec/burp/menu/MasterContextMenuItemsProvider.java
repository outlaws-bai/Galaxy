package org.m2sec.burp.menu;

import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import org.m2sec.GalaxyMain;
import org.m2sec.modules.bypass.menu.BypassMenu;
import org.m2sec.modules.cloud.menu.CloudMenu;
import org.m2sec.modules.fuzz.menu.FuzzMenu;
import org.m2sec.modules.mixed.menu.MixedMenu;
import org.m2sec.modules.payload.menu.PayloadMenu;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class MasterContextMenuItemsProvider implements ContextMenuItemsProvider {

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event) {
        ArrayList<Component> menuList = new ArrayList<>();
        List<AbstractMenu> menus = new ArrayList<>(List.of(new CloudMenu(), new MixedMenu(), new BypassMenu(),
            new FuzzMenu(), new PayloadMenu(GalaxyMain.config.getPayloadConfig())));
        menus.stream().filter(menu -> menu.isDisplay(event)).forEach(menuList::add);
        return menuList;
    }
}
