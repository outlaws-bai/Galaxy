package org.m2sec.common.utils;

import org.m2sec.GalaxyMain;

import java.util.Arrays;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class BurpUtil {

    public static final char BURP_INTRUDER_VAR_FLAG = '§';

    public static String getIntruderWrappedText(byte[] data) {
        int startIndex = ByteUtil.findFirstCharIndex(data, BURP_INTRUDER_VAR_FLAG, 0);
        if (startIndex == -1) {
            throw new RuntimeException("cannot find first intruder flag in data. " + Arrays.toString(data));
        }
        int endIndex = ByteUtil.findFirstCharIndex(data, BURP_INTRUDER_VAR_FLAG, startIndex + 1);
        if (endIndex == -1) {
            throw new RuntimeException("cannot find second intruder flag in data. " + Arrays.toString(data));
        }
        return new String(ByteUtil.subBytes(data, startIndex + 1, endIndex));
    }

    public static String generateCollaboratorPayload() {
        return GalaxyMain.burpApi.collaborator().defaultPayloadGenerator().generatePayload().toString();
    }
}
