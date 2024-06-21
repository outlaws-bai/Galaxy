package org.m2sec.common.crypto;

import org.m2sec.common.Constants;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */

public class MacUtil {

    public static byte[] calc(byte[] data, byte[] secret, String algorithm) {
        try {
            Mac mac = Mac.getInstance(algorithm, Constants.CRYPTO_PROVIDER_BC);
            SecretKeySpec keySpec = new SecretKeySpec(secret, algorithm);
            mac.init(keySpec);
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
}