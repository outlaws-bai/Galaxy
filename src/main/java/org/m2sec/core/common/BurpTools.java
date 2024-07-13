package org.m2sec.core.common;

import burp.api.montoya.MontoyaApi;
import org.m2sec.core.utils.ByteUtil;

import java.util.Arrays;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class BurpTools {

    public static final char BURP_INTRUDER_VAR_FLAG = '§';

    public static String getIntruderWrappedText(byte[] data) {
        return ByteUtil.getWrappedText(data, BURP_INTRUDER_VAR_FLAG);
    }

    public static String generateCollaboratorPayload(MontoyaApi api) {
        return api.collaborator().defaultPayloadGenerator().generatePayload().toString();
    }
}
