package org.m2sec.common;

import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.DefaultStringLookup;
import org.mvel2.MVEL;

import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/6/5 17:42
 * @description:
 */
public class Render {

    private static final String EL_PREFIX = "mv:";

    public static String renderStr(String template, Map<String, Object> env) {
        return new StringSubstitutor(
                        key -> {
                            if (key.startsWith(EL_PREFIX)) {
                                return (String)
                                        MVEL.eval(key.substring(EL_PREFIX.length()).trim(), env);
                            }
                            if (key.contains(":")) {
                                for (DefaultStringLookup lookup : DefaultStringLookup.values()) {
                                    String lookupKey = lookup.getKey();
                                    if (key.startsWith(lookupKey + ":")) {
                                        return lookup.getStringLookup()
                                                .lookup(
                                                        key.substring(lookupKey.length() + 1)
                                                                .trim());
                                    }
                                }
                            }
                            return null;
                        })
                .replace(template);
    }
}
