package org.m2sec.modules.mixed.menu;

import org.m2sec.burp.menu.AbstractMenu;
import org.m2sec.burp.menu.AbstractMenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class MixedMenu extends AbstractMenu {

    @Override
    public String displayName() {
        return "Mixed";
    }

    @Override
    public List<AbstractMenu> getSubMenus() {
        return null;
    }

    @Override
    public List<AbstractMenuItem> getSubMenuItems() {
        return new ArrayList<>(
                List.of(
                        new UrlToRepeaterMenuItem(),
                        new MessageToSqlmapMenuItem(),
                        new QueryStrToJsonMenuItem(),
                        new JsonToQueryStrMenuItem()));
    }
}
