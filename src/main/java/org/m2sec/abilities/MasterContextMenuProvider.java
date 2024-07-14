package org.m2sec.abilities;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import lombok.extern.slf4j.Slf4j;
import org.m2sec.abilities.menus.HttpHookMenu;
import org.m2sec.abilities.menus.IMenu;
import org.m2sec.abilities.menus.OtherMenu;
import org.m2sec.core.common.Config;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: outlaws-bai
 * @date: 2024/7/13 10:38
 * @description:
 */
@Slf4j
public class MasterContextMenuProvider implements ContextMenuItemsProvider {

    protected final MontoyaApi api;

    private final Config config;

    public MasterContextMenuProvider(MontoyaApi api, Config config) {
        this.api = api;
        this.config = config;
    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event) {
        List<Component> retVal = new ArrayList<>();
        List<IMenu> menus = new ArrayList<>();
        menus.add(new HttpHookMenu(api, config));
        menus.add(new OtherMenu(api, config));
        menus.stream().filter(x -> x.isDisplay(event)).forEach(retVal::add);
        return retVal;
    }

}
