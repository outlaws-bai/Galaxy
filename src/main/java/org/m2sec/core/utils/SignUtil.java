package org.m2sec.core.utils;

import org.m2sec.core.common.Constants;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * @author: outlaws-bai
 * @date: 2024/11/1 20:28
 * @description: 数字签名工具类
 */

public class SignUtil {

    public static byte[] sign(String algorithm, byte[] data, byte[] privateKey) {
        try {
            Signature signature = Signature.getInstance(algorithm, Constants.CRYPTO_PROVIDER);
            PrivateKey priKey =
                KeyFactory.getInstance(algorithm.split("with")[1]).generatePrivate(new PKCS8EncodedKeySpec(privateKey));
            signature.initSign(priKey);
            signature.update(data);
            return signature.sign();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | SignatureException | NoSuchProviderException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static String signToHex(String algorithm, byte[] data, byte[] privateKey) {
        return CodeUtil.hexEncodeToString(sign(algorithm, data, privateKey));
    }

    public static String signToBase64(String algorithm, byte[] data, byte[] privateKey) {
        return CodeUtil.b64encodeToString(sign(algorithm, data, privateKey));
    }
}
