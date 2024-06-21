package org.m2sec.common.utils;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.utilities.DigestAlgorithm;
import org.m2sec.GalaxyMain;
import org.m2sec.common.enums.OperatingEnv;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class HashUtil {

    public static byte[] calcHash(byte[] data, DigestAlgorithm digestAlgorithm) {
        if (GalaxyMain.env.equals(OperatingEnv.LOCAL)) {
            try {
                MessageDigest md = MessageDigest.getInstance(digestAlgorithm.toString().replace("_", ""));
                return md.digest(data);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        return GalaxyMain.burpApi.utilities().cryptoUtils().generateDigest(ByteArray.byteArray(data),
            digestAlgorithm).getBytes();
    }

    public static String calcHashToHex(byte[] data, DigestAlgorithm digestAlgorithm) {
        return toHexString(calcHash(data, digestAlgorithm));
    }

    public static String calcHashToBase64(byte[] data, DigestAlgorithm digestAlgorithm) {
        return Base64.getEncoder().encodeToString(calcHash(data, digestAlgorithm));
    }

    public static byte[] calcHmac(byte[] secret, DigestAlgorithm digestAlgorithm, byte[] data) {
        try {
            String MacName = "Hmac" + digestAlgorithm.toString().replace("_", "");
            Mac mac = Mac.getInstance(MacName);
            SecretKeySpec secretKey = new SecretKeySpec(secret, MacName);
            mac.init(secretKey);
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static String calcHmacToHex(byte[] secret, DigestAlgorithm digestAlgorithm, byte[] data) {
        return toHexString(calcHmac(secret, digestAlgorithm, data));
    }

    public static String toHexString(byte[] data) {
        BigInteger bigInteger = new BigInteger(1, data);
        String hexString = bigInteger.toString(16);

        // 补足位数（如果需要的话）
        int paddingLength = (data.length * 2) - hexString.length();
        if (paddingLength > 0) {
            hexString = "0".repeat(paddingLength) + hexString;
        }

        return hexString;
    }
}
