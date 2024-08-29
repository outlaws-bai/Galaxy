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

public class OtherMenu extends IMenu {

    public OtherMenu(MontoyaApi api, Config config) {
        super(api, config);
    }

    @Override
    public String displayName() {
        return "Other";
    }

    @Override
    public List<IMenu> getSubMenus() {
        return null;
    }

    @Override
    public List<IItem> getSubItems() {
        List<IItem> items = new ArrayList<>();
        items.add(new ParseSwaggerApiDocItem(api, config));
        items.add(new CopyBodyMenuItem(api, config));
        items.add(new JsonToQueryMenuItem(api, config));
        items.add(new QueryToJsonMenuItem(api, config));
        items.add(new SendRequestToSqlmapMenuItem(api, config));
        items.add(new SendRequestToScannerMenuItem(api, config));
        return items;
    }
}
