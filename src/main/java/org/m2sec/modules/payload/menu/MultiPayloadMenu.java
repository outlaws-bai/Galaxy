package org.m2sec.modules.payload.menu;

import org.m2sec.burp.menu.AbstractMenu;
import org.m2sec.burp.menu.AbstractMenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/5/15 11:30
 * @description:
 */
public class MultiPayloadMenu extends AbstractMenu {

    private final Map<String, String> payloads;


    public MultiPayloadMenu(Map<String, String> payloads){
        this.payloads = payloads;
        payloads.put("'", "'");
        payloads.put("!", "!");
        payloads.put(">", ">");
        payloads.put("9*1000", "9".repeat(1000));
        payloads.put("%00", "%00");
        payloads.put("%ff", "%ff");
        payloads.put("%99999999999s", "%99999999999s");
        payloads.put("../", "../");
        payloads.put("\\u003C", "\\u003C");
        payloads.put("!@#$%%^#$%#$@#$%$$@#$%^^**(()", "!@#$%%^#$%#$@#$%$$@#$%^^**(()");
        payloads.put("%", "%");
        payloads.put("#", "#");
        this.setText(displayName());
    }

    @Override
    public String displayName() {
        return "multi";
    }

    @Override
    public List<AbstractMenu> getSubMenus() {
        return null;
    }

    @Override
    public List<AbstractMenuItem> getSubMenuItems() {
        List<AbstractMenuItem> retVal = new ArrayList<>();
        for (Map.Entry<String, String> entry : payloads.entrySet()) {
            retVal.add(new MultiPayloadMenuItem(entry.getKey(), entry.getValue()));
        }
        return retVal;
    }
}
