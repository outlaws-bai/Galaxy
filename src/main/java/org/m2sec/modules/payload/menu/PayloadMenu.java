package org.m2sec.modules.payload.menu;

import org.m2sec.burp.menu.AbstractMenu;
import org.m2sec.burp.menu.AbstractMenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description: payload is Map<String, xxx> xxx is Map<String, xxx> | String | List<String>
 */
public class PayloadMenu extends AbstractMenu {

    private final Map<String, Object> payloads;

    private final boolean head;

    public PayloadMenu(Map<String, Object> payloads) {
        this(payloads, null);
    }

    public PayloadMenu(Map<String, Object> payloads, String displayName) {
        this.payloads = payloads;
        if (displayName == null) {
            head = true;
            this.setText(this.displayName());
        } else {
            head = false;
            this.setText(displayName);
        }
    }

    @Override
    public String displayName() {
        return "Payload";
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AbstractMenu> getSubMenus() {
        List<AbstractMenu> retVal = new ArrayList<>();
        if (head) {
            // multi
            retVal.add(new MultiPayloadMenu(payloads.containsKey("multi") ? (Map<String, String>) payloads.get("multi"
            ) : new HashMap<>()));
        }
        for (Map.Entry<String, Object> entry : payloads.entrySet()) {
            Map<String, Object> subPayload;
            if (entry.getValue() instanceof Map<?, ?> m) {
                subPayload = (Map<String, Object>) m;
                retVal.add(new PayloadMenu(subPayload, entry.getKey()));
            } else if (entry.getValue() instanceof List<?> l) {
                subPayload = l.stream().collect(Collectors.toMap(Object::toString, Object::toString, (oldValue,
                                                                                                      newValue) -> newValue));
                retVal.add(new PayloadMenu(subPayload, entry.getKey()));
            }
        }
        return retVal;
    }

    @Override
    public List<AbstractMenuItem> getSubMenuItems() {
        List<AbstractMenuItem> retVal = new ArrayList<>();
        for (Map.Entry<String, Object> entry : payloads.entrySet()) {
            if (entry.getValue() instanceof String str) {
                retVal.add(new PayloadMenuItem(entry.getKey(), str));
            }
        }
        return retVal;
    }
}
