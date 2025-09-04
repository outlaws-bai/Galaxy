package org.m2sec.abilities.menus;

import burp.api.montoya.MontoyaApi;
import org.m2sec.core.common.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: outlaws-bai
 * @date: 2024/7/13 10:54
 * @description:
 */

public class HttpHookMenu extends IMenu {
    public HttpHookMenu(MontoyaApi api, Config config) {
        super(api, config);
    }

    @Override
    public String displayName() {
        return "Http Hook";
    }

    @Override
    public List<IMenu> getSubMenus() {
        return null;
    }

    @Override
    public List<IItem> getSubItems() {
        List<IItem> items = new ArrayList<>();
        items.add(new DecryptRequestItem(api, config));
        items.add(new EncryptRequestItem(api, config));
        items.add(new DecryptResponseItem(api, config));
        items.add(new EncryptResponseItem(api, config));
        items.add(new ScanDecryptedRequestBySqlmapMenuItem(api, config));
        items.add(new ProxyDecryptedRequestToPassiveProxyScannerMenuItem(api, config));
        return items;
    }
}
