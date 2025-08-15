package org.m2sec.core.common;

import burp.api.montoya.core.Range;
import burp.api.montoya.intruder.AttackConfiguration;
import java.util.List;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class BurpTools {
    public static String getIntruderWrappedText(AttackConfiguration attackConfiguration) {
        List<Range> ranges = attackConfiguration.requestTemplate().insertionPointOffsets();
        if (ranges == null || ranges.isEmpty()) {
            throw new RuntimeException("Bypass Auth Of Path request does not contain any range");
        }
        Range range = ranges.get(0);
        return new String(attackConfiguration.requestTemplate().content().subArray(range).getBytes());
    }
}
