package org.m2sec.common.crypto;

import org.m2sec.common.enums.SymmetricKeyMode;

import javax.annotation.Nullable;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/6/12 19:34
 * @description:
 */

public class CryptoUtil {

    public static final String ALGORITHM_AES = "AES";
    public static final String ALGORITHM_AES_DEFAULT_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    public static final String ALGORITHM_RSA = "RSA";
    public static final String ALGORITHM_EC = "EC";
    public static final String ALGORITHM_SM2 = "SM2";
    public static final String ALGORITHM_SM4 = "SM4";
    public static final String ALGORITHM_SM4_DEFAULT_TRANSFORMATION = "SM4/ECB/PKCS5Padding";

    public static byte[] aesEncrypt(
            @Nullable String transformation,
            byte[] data,
            byte[] secret,
            Map<String, Object> params) {
        return symmetricKeyEncrypt(
                transformation,
                data,
                secret,
                params,
                ALGORITHM_AES,
                ALGORITHM_AES_DEFAULT_TRANSFORMATION);
    }


    public static byte[] aesDecrypt(
            @Nullable String transformation,
            byte[] data,
            byte[] secret,
            Map<String, Object> params) {
        return symmetricKeyDecrypt(
                transformation,
                data,
                secret,
                params,
                ALGORITHM_AES,
                ALGORITHM_AES_DEFAULT_TRANSFORMATION);
    }


    public static byte[] rsaEncrypt(
            @Nullable String transformation, byte[] data, byte[] publicKey) {
        try {
            String finalTransformation = transformation != null ? transformation : ALGORITHM_RSA;
            PublicKey pubKey =
                    KeyFactory.getInstance(ALGORITHM_RSA)
                            .generatePublic(new X509EncodedKeySpec(publicKey));
            Cipher cipher = Cipher.getInstance(finalTransformation);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            return cipher.doFinal(data);
        } catch (InvalidKeySpecException | NoSuchPaddingException | NoSuchAlgorithmException |
                 IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }


    public static byte[] rsaDecrypt(
            @Nullable String transformation, byte[] data, byte[] privateKey) {
        try {
            String finalTransformation = transformation != null ? transformation : ALGORITHM_RSA;
            PrivateKey priKey =
                    KeyFactory.getInstance(ALGORITHM_RSA)
                            .generatePrivate(new PKCS8EncodedKeySpec(privateKey));
            Cipher cipher = Cipher.getInstance(finalTransformation);
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            return cipher.doFinal(data);
        } catch (InvalidKeySpecException | NoSuchPaddingException | NoSuchAlgorithmException |
                 IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }


    public static byte[] sm2Encrypt(byte[] data, byte[] publicKey) {
        try {
            PublicKey pubKey =
                    KeyFactory.getInstance(ALGORITHM_EC)
                            .generatePublic(new X509EncodedKeySpec(publicKey));
            Cipher cipher = Cipher.getInstance(ALGORITHM_SM2);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            return cipher.doFinal(data);
        } catch (InvalidKeySpecException | NoSuchPaddingException | NoSuchAlgorithmException |
                 IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }


    public static byte[] sm2Decrypt(byte[] cipherText, byte[] privateKey) {
        try {
            PrivateKey priKey =
                    KeyFactory.getInstance(ALGORITHM_EC)
                            .generatePrivate(new PKCS8EncodedKeySpec(privateKey));
            Cipher cipher = Cipher.getInstance(ALGORITHM_SM2);
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            return cipher.doFinal(cipherText);
        } catch (InvalidKeySpecException | NoSuchPaddingException | NoSuchAlgorithmException |
                 IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }


    public static byte[] sm4Encrypt(
            @Nullable String transformation,
            byte[] data,
            byte[] secret,
            Map<String, Object> params) {
        return symmetricKeyEncrypt(
                transformation,
                data,
                secret,
                params,
                ALGORITHM_SM4,
                ALGORITHM_SM4_DEFAULT_TRANSFORMATION);
    }


    public static byte[] sm4Decrypt(
            @Nullable String transformation,
            byte[] data,
            byte[] secret,
            Map<String, Object> params) {
        return symmetricKeyDecrypt(
                transformation,
                data,
                secret,
                params,
                ALGORITHM_SM4,
                ALGORITHM_SM4_DEFAULT_TRANSFORMATION);
    }


    private static byte[] symmetricKeyEncrypt(
            @Nullable String transformation,
            byte[] data,
            byte[] secret,
            Map<String, Object> params,
            String algorithm,
            String algorithmDefaultTransformation) {
        try {
            String finalTransformation =
                    transformation != null ? transformation : algorithmDefaultTransformation;
            Cipher cipher = Cipher.getInstance(finalTransformation);
            SecretKeySpec keySpec = new SecretKeySpec(secret, algorithm);
            AlgorithmParameterSpec paramSpec =
                    getSymmetricKeyEncryptParameterSpec(finalTransformation, params);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, paramSpec);
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                 IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] symmetricKeyDecrypt(
            @Nullable String transformation,
            byte[] data,
            byte[] secret,
            Map<String, Object> params,
            String algorithm,
            String algorithmDefaultTransformation) {
        try {
            String finalTransformation =
                    transformation != null ? transformation : algorithmDefaultTransformation;
            Cipher cipher = Cipher.getInstance(finalTransformation);
            SecretKeySpec keySpec = new SecretKeySpec(secret, algorithm);
            AlgorithmParameterSpec paramSpec =
                    getSymmetricKeyEncryptParameterSpec(finalTransformation, params);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                 IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private static AlgorithmParameterSpec getSymmetricKeyEncryptParameterSpec(
            String transformation, Map<String, Object> params) {
        String modeStr = transformation.split("/")[1];
        SymmetricKeyMode symmetricKeyMode = SymmetricKeyMode.valueOf(modeStr);
        if (symmetricKeyMode == SymmetricKeyMode.ECB) {
            return null;
        } else if (symmetricKeyMode == SymmetricKeyMode.CBC) {
            return new IvParameterSpec(getSymmetricKeyEncryptIv(params));
        } else if (symmetricKeyMode == SymmetricKeyMode.GCM) {
            byte[] ivBytes = getSymmetricKeyEncryptIv(params);
            Integer tLen = (Integer) params.get("tLen");
            tLen = tLen == null ? 128 : tLen;
            return new GCMParameterSpec(tLen, ivBytes);
        }
        throw new IllegalArgumentException("Unsupported mode: " + modeStr);
    }

    private static byte[] getSymmetricKeyEncryptIv(Map<String, Object> params) {
        Object iv = params.get("iv");
        if (iv instanceof byte[] ivBytes) {
            return ivBytes;
        } else if (iv instanceof String ivString) {
            return ivString.getBytes();
        }
        throw new IllegalArgumentException("iv type error! " + iv.getClass());
    }
}
