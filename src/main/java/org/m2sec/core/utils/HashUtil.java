package org.m2sec.core.utils;

import org.m2sec.core.common.Constants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */

public class HashUtil {

    public static final String SHA_256 = "SHA256";
    public static final String SHA_1 = "SHA1";

    public static final String MD_5 = "MD5";

    public static byte[] calc(String algorithm, byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm, Constants.CRYPTO_PROVIDER);
            return md.digest(data);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    public static String calcToHex(String algorithm, byte[] data) {
        return CodeUtil.hexEncodeToString(calc(algorithm, data));
    }

    public static String calcToBase64(String algorithm, byte[] data) {
        return CodeUtil.b64encodeToString(calc(algorithm, data));
    }
}
