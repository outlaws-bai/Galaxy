package org.m2sec.common.crypto;

import org.m2sec.common.Constants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * @author: outlaws-bai
 * @date: 2024/6/12 19:33
 * @description:
 */

public class HashUtil {

    public static byte[] calc(byte[] data, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm, Constants.CRYPTO_PROVIDER_BC);
            return md.digest(data);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }


}
