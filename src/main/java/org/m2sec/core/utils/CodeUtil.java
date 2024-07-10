package org.m2sec.core.utils;

import org.bouncycastle.util.encoders.Hex;

import java.util.Base64;

/**
 * @author: outlaws-bai
 * @date: 2024/7/10 20:26
 * @description:
 */

public class CodeUtil {
    public static byte[] b64decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    public static byte[] b64encode(byte[] data) {
        return Base64.getEncoder().encode(data);
    }

    public static String b64encodeToString(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] hexDecode(String data) {
        return Hex.decode(data);
    }

    public static byte[] hexEncode(byte[] data) {
        return Hex.encode(data);
    }

    public static String hexEncodeToString(byte[] data) {
        return Hex.toHexString(data);
    }
}
