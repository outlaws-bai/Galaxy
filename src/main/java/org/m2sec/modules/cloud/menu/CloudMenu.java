package org.m2sec.modules.cloud.menu;

import org.m2sec.burp.menu.AbstractMenu;
import org.m2sec.burp.menu.AbstractMenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class CloudMenu extends AbstractMenu {
    @Override
    public String displayName() {
        return "Cloud";
    }

    @Override
    public List<AbstractMenu> getSubMenus() {
        return null;
    }

    @Override
    public List<AbstractMenuItem> getSubMenuItems() {
        return new ArrayList<>(List.of(new AwsSignMenuItem()));
    }
}
