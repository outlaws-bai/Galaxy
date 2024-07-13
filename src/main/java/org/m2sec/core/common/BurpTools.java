package org.m2sec.core.common;

import burp.api.montoya.MontoyaApi;
import org.m2sec.Galaxy;
import org.m2sec.core.utils.ByteUtil;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class BurpTools {

    public static final char BURP_INTRUDER_VAR_FLAG = '§';

    private static String DNS_LOG_ROOT_DOMAIN;

    public static String getIntruderWrappedText(byte[] data) {
        return ByteUtil.getWrappedText(data, BURP_INTRUDER_VAR_FLAG);
    }

    public static String generateCollaboratorPayload(MontoyaApi api) {
        if (Galaxy.isInBurp()) {
            if (DNS_LOG_ROOT_DOMAIN == null) {
                DNS_LOG_ROOT_DOMAIN = api.collaborator().defaultPayloadGenerator().generatePayload().toString();
            }
            return Helper.generateRandomString(8) + "." + DNS_LOG_ROOT_DOMAIN;
        }
        return Helper.generateRandomString(32);
    }
}
