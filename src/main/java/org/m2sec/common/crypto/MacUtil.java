package org.m2sec.common.crypto;

import org.m2sec.common.Constants;
import org.m2sec.common.utils.ByteUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Base64;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */

public class MacUtil {

    public static final String HMAC_SHA_256 = "HmacSHA256";

    public static byte[] calc(byte[] data, byte[] secret, String algorithm) {
        try {
            Mac mac = Mac.getInstance(algorithm);
            SecretKeySpec keySpec = new SecretKeySpec(secret, algorithm);
            mac.init(keySpec);
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static String calcToHex(byte[] data, byte[] secret, String algorithm) {
        return ByteUtil.toHexString(calc(data, secret, algorithm));
    }

    public static String calcToBase64(byte[] data, byte[] secret, String algorithm) {
        return Base64.getEncoder().encodeToString(calc(data, secret, algorithm));
    }
}