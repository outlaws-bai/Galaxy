package org.m2sec.modules.fuzz.menu;

import org.m2sec.burp.menu.AbstractMenu;
import org.m2sec.burp.menu.AbstractMenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: outlaws-bai
 * @date: 2024/3/10 15:05
 * @description:
 */
public class FuzzMenu extends AbstractMenu {

    @Override
    public String displayName() {
        return "Fuzz";
    }

    @Override
    public List<AbstractMenu> getSubMenus() {
        return null;
    }

    @Override
    public List<AbstractMenuItem> getSubMenuItems() {
        return new ArrayList<>(
                List.of(
                        new FuzzSensitivePathMenuItem(),
                        new FuzzSensitivePathAndBypassMenuItem(),
                        new FuzzSwaggerDocsMenuItem(),
                        new ExtractInfoDictMenuItem()));
    }
}
