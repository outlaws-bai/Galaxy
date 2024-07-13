package org.m2sec.abilities.menus;

import burp.api.montoya.MontoyaApi;
import org.m2sec.core.common.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: outlaws-bai
 * @date: 2024/7/13 10:56
 * @description:
 */

public class TopMenu extends IMenu {

    public TopMenu(MontoyaApi api, Config config) {
        super(api, config);
    }

    @Override
    public String displayName() {
        return "Top";
    }

    @Override
    public List<IMenu> getSubMenus() {
        return null;
    }

    @Override
    public List<IItem> getSubItems() {
        List<IItem> items = new ArrayList<>();
        items.add(new ParseSwaggerApiDocItem(api, config));
        return items;
    }
}
