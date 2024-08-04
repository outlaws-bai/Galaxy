package org.m2sec.core.utils;


import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.math.ec.ECPoint;
import org.m2sec.core.common.XXTEATools;
import org.m2sec.core.enums.SymmetricKeyMode;

import javax.annotation.Nullable;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */

public class CryptoUtil {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static final String ALGORITHM_DES = "DES";
    public static final String ALGORITHM_DES_DEFAULT_TRANSFORMATION = "DES/ECB/PKCS5Padding";
    public static final String ALGORITHM_DES3 = "DESede";
    public static final String ALGORITHM_DES3_DEFAULT_TRANSFORMATION = "DESede/ECB/PKCS5Padding";
    public static final String ALGORITHM_AES = "AES";
    public static final String ALGORITHM_AES_DEFAULT_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    public static final String ALGORITHM_RSA = "RSA";
    public static final String ALGORITHM_EC = "EC";
    public static final String ALGORITHM_SM2 = "SM2";
    public static final String ALGORITHM_SM4 = "SM4";
    public static final String ALGORITHM_SM4_DEFAULT_TRANSFORMATION = "SM4/ECB/PKCS5Padding";

    public static final String ALGORITHM_XXTEA = "XXTEA";

    public static byte[] desEncrypt(String transformation, byte[] data, byte[] secret,
                                    Map<String, Object> params) {
        return symmetricKeyEncrypt(transformation, data, secret, params, ALGORITHM_DES,
            ALGORITHM_DES_DEFAULT_TRANSFORMATION);
    }

    public static byte[] desDecrypt(String transformation, byte[] data, byte[] secret,
                                    Map<String, Object> params) {
        return symmetricKeyDecrypt(transformation, data, secret, params, ALGORITHM_DES,
            ALGORITHM_DES_DEFAULT_TRANSFORMATION);
    }

    public static byte[] des3Encrypt(String transformation, byte[] data, byte[] secret,
                                     Map<String, Object> params) {
        return symmetricKeyEncrypt(transformation, data, secret, params, ALGORITHM_DES3,
            ALGORITHM_DES3_DEFAULT_TRANSFORMATION);
    }

    public static byte[] des3Decrypt(String transformation, byte[] data, byte[] secret,
                                     Map<String, Object> params) {
        return symmetricKeyDecrypt(transformation, data, secret, params, ALGORITHM_DES3,
            ALGORITHM_DES3_DEFAULT_TRANSFORMATION);
    }

    public static byte[] aesEncrypt(String transformation, byte[] data, byte[] secret,
                                    Map<String, Object> params) {
        return symmetricKeyEncrypt(transformation, data, secret, params, ALGORITHM_AES,
            ALGORITHM_AES_DEFAULT_TRANSFORMATION);
    }


    public static byte[] aesDecrypt(String transformation, byte[] data, byte[] secret,
                                    Map<String, Object> params) {
        return symmetricKeyDecrypt(transformation, data, secret, params, ALGORITHM_AES,
            ALGORITHM_AES_DEFAULT_TRANSFORMATION);
    }

    @Deprecated
    public static byte[] rsaEncrypt(byte[] data, byte[] publicKey) {
        return rsaEncrypt(ALGORITHM_RSA, data, publicKey);
    }

    @Deprecated
    public static byte[] rsaDecrypt(byte[] data, byte[] privateKey) {
        return rsaDecrypt(ALGORITHM_RSA, data, privateKey);
    }

    public static byte[] rsaEncrypt(String transformation, byte[] data, byte[] publicKey) {
        try {
            PublicKey pubKey = KeyFactory.getInstance(ALGORITHM_RSA).generatePublic(new X509EncodedKeySpec(publicKey));
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            return cipher.doFinal(data);
        } catch (InvalidKeySpecException | NoSuchPaddingException | NoSuchAlgorithmException |
                 IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] rsaDecrypt(String transformation, byte[] data, byte[] privateKey) {
        try {
            PrivateKey priKey =
                KeyFactory.getInstance(ALGORITHM_RSA).generatePrivate(new PKCS8EncodedKeySpec(privateKey));
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            return cipher.doFinal(data);
        } catch (InvalidKeySpecException | NoSuchPaddingException | NoSuchAlgorithmException |
                 IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public static byte[] sm2Encrypt(byte[] data, byte[] publicKey) {
        return sm2Crypt(data, publicKey, "c1c2c3", true);
    }

    @Deprecated
    public static byte[] sm2Decrypt(byte[] data, byte[] privateKey) {
        return sm2Crypt(data, privateKey, "c1c2c3", false);
    }

    public static byte[] sm2Encrypt(String mode, byte[] data, byte[] publicKey) {
        return sm2Crypt(data, publicKey, mode, true);
    }


    public static byte[] sm2Decrypt(String mode, byte[] data, byte[] privateKey) {
        return sm2Crypt(data, privateKey, mode, false);
    }

    private static byte[] sm2Crypt(byte[] data, byte[] key, String modeString, boolean isEncrypt) {
        try {
            SM2Engine.Mode mode;
            if (modeString.equalsIgnoreCase(SM2Engine.Mode.C1C3C2.name())) {
                mode = SM2Engine.Mode.C1C3C2;
            } else {
                mode = SM2Engine.Mode.C1C2C3;
            }
            CipherParameters param;
            if (isEncrypt) {
                KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_EC);
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
                BCECPublicKey publicKey = (BCECPublicKey) keyFactory.generatePublic(keySpec);
                ECPoint q = publicKey.getQ();
                ECDomainParameters domainParameters = new ECDomainParameters(
                    publicKey.getParameters().getCurve(),
                    publicKey.getParameters().getG(),
                    publicKey.getParameters().getN(),
                    publicKey.getParameters().getH());
                param = new ParametersWithRandom(new ECPublicKeyParameters(q, domainParameters), new SecureRandom());
            } else {
                BCECPrivateKey priKey =
                    (BCECPrivateKey) KeyFactory.getInstance(ALGORITHM_EC).generatePrivate(new PKCS8EncodedKeySpec(key));
                BigInteger d = priKey.getD();
                ECDomainParameters domainParameters = new ECDomainParameters(
                    priKey.getParameters().getCurve(),
                    priKey.getParameters().getG(),
                    priKey.getParameters().getN(),
                    priKey.getParameters().getH());
                param = new ECPrivateKeyParameters(d, domainParameters);
            }
            SM2Engine sm2Engine = new SM2Engine(mode);
            sm2Engine.init(isEncrypt, param);
            return sm2Engine.processBlock(data, 0, data.length);
        } catch (InvalidCipherTextException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] teaEncrypt(String transformation, byte[] data, byte[] secret) {
        if (transformation != null && !transformation.isBlank() && transformation.equalsIgnoreCase(ALGORITHM_XXTEA))
            return XXTEATools.encrypt(data, secret);
        return symmetricKeyEncrypt(transformation, data, secret, null, transformation,
            transformation);
    }


    public static byte[] teaDecrypt(String transformation, byte[] data, byte[] secret) {
        if (transformation != null && !transformation.isBlank() && transformation.equalsIgnoreCase(ALGORITHM_XXTEA))
            return XXTEATools.decrypt(data, secret);
        return symmetricKeyDecrypt(transformation, data, secret, null, transformation,
            transformation);
    }


    public static byte[] sm4Encrypt(@Nullable String transformation, byte[] data, byte[] secret,
                                    Map<String, Object> params) {
        return symmetricKeyEncrypt(transformation, data, secret, params, ALGORITHM_SM4,
            ALGORITHM_SM4_DEFAULT_TRANSFORMATION);
    }


    public static byte[] sm4Decrypt(@Nullable String transformation, byte[] data, byte[] secret,
                                    Map<String, Object> params) {
        return symmetricKeyDecrypt(transformation, data, secret, params, ALGORITHM_SM4,
            ALGORITHM_SM4_DEFAULT_TRANSFORMATION);
    }


    private static byte[] symmetricKeyEncrypt(@Nullable String transformation, byte[] data, byte[] secret, Map<String
        , Object> params, String algorithm, String algorithmDefaultTransformation) {
        try {
            String finalTransformation = transformation != null && !algorithm.equals(transformation) ?
                transformation : algorithmDefaultTransformation;
            Cipher cipher = Cipher.getInstance(finalTransformation);
            SecretKeySpec keySpec = new SecretKeySpec(secret, algorithm);
            AlgorithmParameterSpec paramSpec = getSymmetricKeyEncryptParameterSpec(finalTransformation, params);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, paramSpec);
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                 IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] symmetricKeyDecrypt(@Nullable String transformation, byte[] data, byte[] secret, Map<String
        , Object> params, String algorithm, String algorithmDefaultTransformation) {
        try {
            String finalTransformation = transformation != null && !algorithm.equals(transformation) ?
                transformation : algorithmDefaultTransformation;
            Cipher cipher = Cipher.getInstance(finalTransformation);
            SecretKeySpec keySpec = new SecretKeySpec(secret, algorithm);
            AlgorithmParameterSpec paramSpec = getSymmetricKeyEncryptParameterSpec(finalTransformation, params);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                 IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private static AlgorithmParameterSpec getSymmetricKeyEncryptParameterSpec(String transformation, Map<String,
        Object> params) {
        if (!transformation.contains("/")) return null;
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
