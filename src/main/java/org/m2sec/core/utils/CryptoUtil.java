package org.m2sec.core.utils;

import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.RC4Engine;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.math.ec.ECPoint;
import org.m2sec.core.common.Constants;
import org.m2sec.core.common.XXTEATools;
import org.m2sec.core.enums.SymmetricKeyMode;

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

    public static byte[] desEncrypt(
            String transformation, byte[] data, byte[] secret, Map<String, Object> params) {
        return symmetricKeyEncrypt(
                transformation,
                data,
                secret,
                params,
                ALGORITHM_DES,
                ALGORITHM_DES_DEFAULT_TRANSFORMATION);
    }

    public static byte[] desDecrypt(
            String transformation, byte[] data, byte[] secret, Map<String, Object> params) {
        return symmetricKeyDecrypt(
                transformation,
                data,
                secret,
                params,
                ALGORITHM_DES,
                ALGORITHM_DES_DEFAULT_TRANSFORMATION);
    }

    public static byte[] des3Encrypt(
            String transformation, byte[] data, byte[] secret, Map<String, Object> params) {
        return symmetricKeyEncrypt(
                transformation,
                data,
                secret,
                params,
                ALGORITHM_DES3,
                ALGORITHM_DES3_DEFAULT_TRANSFORMATION);
    }

    public static byte[] des3Decrypt(
            String transformation, byte[] data, byte[] secret, Map<String, Object> params) {
        return symmetricKeyDecrypt(
                transformation,
                data,
                secret,
                params,
                ALGORITHM_DES3,
                ALGORITHM_DES3_DEFAULT_TRANSFORMATION);
    }

    public static byte[] aesEncrypt(
            String transformation, byte[] data, byte[] secret, Map<String, Object> params) {
        return symmetricKeyEncrypt(
                transformation,
                data,
                secret,
                params,
                ALGORITHM_AES,
                ALGORITHM_AES_DEFAULT_TRANSFORMATION);
    }

    public static byte[] aesDecrypt(
            String transformation, byte[] data, byte[] secret, Map<String, Object> params) {
        return symmetricKeyDecrypt(
                transformation,
                data,
                secret,
                params,
                ALGORITHM_AES,
                ALGORITHM_AES_DEFAULT_TRANSFORMATION);
    }

    public static byte[] rc4Encrypt(byte[] data, byte[] key) {
        return rc4Crypt(data, key, true);
    }

    public static byte[] rc4Decrypt(byte[] data, byte[] key) {
        return rc4Crypt(data, key, false);
    }

    private static byte[] rc4Crypt(byte[] data, byte[] key, boolean encrypt) {
        try {
            RC4Engine rc4Engine = new RC4Engine();
            KeyParameter keyParam = new KeyParameter(key);
            rc4Engine.init(encrypt, keyParam);

            byte[] output = new byte[data.length];
            rc4Engine.processBytes(data, 0, data.length, output, 0);
            return output;
        } catch (Exception e) {
            throw new RuntimeException("RC4 processing error", e);
        }
    }

    public static byte[] rsaEncrypt(byte[] data, byte[] publicKey) {
        return rsaEncrypt(ALGORITHM_RSA, data, publicKey);
    }

    public static byte[] rsaDecrypt(byte[] data, byte[] privateKey) {
        return rsaDecrypt(ALGORITHM_RSA, data, privateKey);
    }

    public static byte[] rsaEncrypt(String transformation, byte[] data, byte[] publicKey) {
        try {
            PublicKey pubKey =
                    KeyFactory.getInstance(ALGORITHM_RSA)
                            .generatePublic(new X509EncodedKeySpec(publicKey));
            Cipher cipher = Cipher.getInstance(transformation, Constants.CRYPTO_PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            return cipher.doFinal(data);
        } catch (InvalidKeySpecException
                | NoSuchPaddingException
                | NoSuchAlgorithmException
                | IllegalBlockSizeException
                | BadPaddingException
                | InvalidKeyException
                | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] rsaDecrypt(String transformation, byte[] data, byte[] privateKey) {
        try {
            PrivateKey priKey =
                    KeyFactory.getInstance(ALGORITHM_RSA)
                            .generatePrivate(new PKCS8EncodedKeySpec(privateKey));
            Cipher cipher = Cipher.getInstance(transformation, Constants.CRYPTO_PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            return cipher.doFinal(data);
        } catch (InvalidKeySpecException
                | NoSuchPaddingException
                | NoSuchAlgorithmException
                | IllegalBlockSizeException
                | BadPaddingException
                | InvalidKeyException
                | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] sm2Encrypt(byte[] data, byte[] publicKey) {
        return sm2Crypt(data, publicKey, "c1c2c3", true);
    }

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
        if (!isEncrypt)
            data = data[0] == 0x04 ? data : ByteUtil.concatenateByteArrays(new byte[] {0x04}, data);
        try {
            SM2Engine.Mode mode;
            if (modeString.equalsIgnoreCase(SM2Engine.Mode.C1C3C2.name())) {
                mode = SM2Engine.Mode.C1C3C2;
            } else {
                mode = SM2Engine.Mode.C1C2C3;
            }
            CipherParameters param = getSm2CipherParameters(key, isEncrypt);
            SM2Engine sm2Engine = new SM2Engine(mode);
            sm2Engine.init(isEncrypt, param);
            return sm2Engine.processBlock(data, 0, data.length);
        } catch (InvalidCipherTextException e) {
            throw new RuntimeException(e);
        }
    }

    private static CipherParameters getSm2CipherParameters(byte[] key, boolean isEncrypt) {
        CipherParameters param;
        try {
            if (isEncrypt) {
                if (key.length == 64 || key.length == 65) {
                    if (key.length == 64)
                        key = ByteUtil.concatenateByteArrays(new byte[] {0x04}, key);
                    X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
                    ECDomainParameters domainParameters =
                            new ECDomainParameters(
                                    sm2ECParameters.getCurve(),
                                    sm2ECParameters.getG(),
                                    sm2ECParameters.getN());
                    ECPoint pukPoint = sm2ECParameters.getCurve().decodePoint(key);
                    param =
                            new ParametersWithRandom(
                                    new ECPublicKeyParameters(pukPoint, domainParameters),
                                    new SecureRandom());
                }
                //                else if (key.length ==) { // 待补充
                //
                //                }
                else if (key.length == 91) {
                    KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_EC);
                    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
                    BCECPublicKey publicKey = (BCECPublicKey) keyFactory.generatePublic(keySpec);
                    ECPoint q = publicKey.getQ();
                    ECDomainParameters domainParameters =
                            new ECDomainParameters(
                                    publicKey.getParameters().getCurve(),
                                    publicKey.getParameters().getG(),
                                    publicKey.getParameters().getN(),
                                    publicKey.getParameters().getH());
                    param =
                            new ParametersWithRandom(
                                    new ECPublicKeyParameters(q, domainParameters),
                                    new SecureRandom());
                } else {
                    throw new InvalidParameterException(
                            "Unknown public key, please try extracting the original "
                                    + "public key from it and then try again.");
                }
            } else {
                if (key.length == 32 || key.length == 33) {
                    if (key.length == 33) key = ByteUtil.removePrefixIfExists(key, (byte) 0x04);
                    X9ECParameters sm2ECParameters = GMNamedCurves.getByName("sm2p256v1");
                    ECDomainParameters domainParameters =
                            new ECDomainParameters(
                                    sm2ECParameters.getCurve(),
                                    sm2ECParameters.getG(),
                                    sm2ECParameters.getN());
                    param = new ECPrivateKeyParameters(new BigInteger(1, key), domainParameters);
                } else if (key.length == 121) {
                    key = ByteUtil.subBytes(key, 7, 7 + 32);
                    param = getSm2CipherParameters(key, false);
                } else if (key.length == 150) {
                    BCECPrivateKey priKey =
                            (BCECPrivateKey)
                                    KeyFactory.getInstance(ALGORITHM_EC)
                                            .generatePrivate(new PKCS8EncodedKeySpec(key));
                    BigInteger d = priKey.getD();
                    ECDomainParameters domainParameters =
                            new ECDomainParameters(
                                    priKey.getParameters().getCurve(),
                                    priKey.getParameters().getG(),
                                    priKey.getParameters().getN(),
                                    priKey.getParameters().getH());
                    param = new ECPrivateKeyParameters(d, domainParameters);
                } else {
                    throw new InvalidParameterException(
                            "Unknown private key, please try extracting the original "
                                    + "private key from it and then try again.");
                }
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
        return param;
    }

    public static byte[] teaEncrypt(String transformation, byte[] data, byte[] secret) {
        if (transformation != null
                && !transformation.isBlank()
                && transformation.equalsIgnoreCase(ALGORITHM_XXTEA))
            return XXTEATools.encrypt(data, secret);
        return symmetricKeyEncrypt(
                transformation, data, secret, null, transformation, transformation);
    }

    public static byte[] teaDecrypt(String transformation, byte[] data, byte[] secret) {
        if (transformation != null
                && !transformation.isBlank()
                && transformation.equalsIgnoreCase(ALGORITHM_XXTEA))
            return XXTEATools.decrypt(data, secret);
        return symmetricKeyDecrypt(
                transformation, data, secret, null, transformation, transformation);
    }

    public static byte[] sm4Encrypt(
            String transformation, byte[] data, byte[] secret, Map<String, Object> params) {
        return symmetricKeyEncrypt(
                transformation,
                data,
                secret,
                params,
                ALGORITHM_SM4,
                ALGORITHM_SM4_DEFAULT_TRANSFORMATION);
    }

    public static byte[] sm4Decrypt(
            String transformation, byte[] data, byte[] secret, Map<String, Object> params) {
        return symmetricKeyDecrypt(
                transformation,
                data,
                secret,
                params,
                ALGORITHM_SM4,
                ALGORITHM_SM4_DEFAULT_TRANSFORMATION);
    }

    private static byte[] symmetricKeyEncrypt(
            String transformation,
            byte[] data,
            byte[] secret,
            Map<String, Object> params,
            String algorithm,
            String algorithmDefaultTransformation) {
        try {
            String finalTransformation =
                    transformation != null && !algorithm.equals(transformation)
                            ? transformation
                            : algorithmDefaultTransformation;
            Cipher cipher = Cipher.getInstance(finalTransformation, Constants.CRYPTO_PROVIDER);
            SecretKeySpec keySpec = new SecretKeySpec(secret, algorithm);
            AlgorithmParameterSpec paramSpec =
                    getSymmetricKeyEncryptParameterSpec(finalTransformation, params);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, paramSpec);
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException
                | NoSuchPaddingException
                | InvalidAlgorithmParameterException
                | IllegalBlockSizeException
                | BadPaddingException
                | InvalidKeyException
                | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] symmetricKeyDecrypt(
            String transformation,
            byte[] data,
            byte[] secret,
            Map<String, Object> params,
            String algorithm,
            String algorithmDefaultTransformation) {
        try {
            String finalTransformation =
                    transformation != null && !algorithm.equals(transformation)
                            ? transformation
                            : algorithmDefaultTransformation;
            Cipher cipher = Cipher.getInstance(finalTransformation, Constants.CRYPTO_PROVIDER);
            SecretKeySpec keySpec = new SecretKeySpec(secret, algorithm);
            AlgorithmParameterSpec paramSpec =
                    getSymmetricKeyEncryptParameterSpec(finalTransformation, params);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException
                | NoSuchPaddingException
                | InvalidAlgorithmParameterException
                | IllegalBlockSizeException
                | BadPaddingException
                | InvalidKeyException
                | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    private static AlgorithmParameterSpec getSymmetricKeyEncryptParameterSpec(
            String transformation, Map<String, Object> params) {
        if (!transformation.contains("/")) return null;

        String[] parts = transformation.split("/");
        if (parts.length < 2) return null;

        String modeStr = parts[1];
        SymmetricKeyMode symmetricKeyMode = SymmetricKeyMode.valueOf(modeStr);

        // 获取IV参数
        byte[] ivBytes = getSymmetricKeyEncryptIv(params);

        switch (symmetricKeyMode) {
            case ECB:
                return null;
            case CBC:
            case CFB:
            case OFB:
            case CTR:
                // 这些模式都使用IV参数
                return new IvParameterSpec(ivBytes);
            case GCM:
                Integer tLen = (Integer) params.get("tLen");
                tLen = tLen == null ? 128 : tLen;
                return new GCMParameterSpec(tLen, ivBytes);
            default:
                throw new IllegalArgumentException("Unsupported mode: " + modeStr);
        }
    }

    private static byte[] getSymmetricKeyEncryptIv(Map<String, Object> params) {
        if (params == null) {
            throw new IllegalArgumentException("IV parameter is required for this mode");
        }
        byte[] ivBytes = (byte[]) params.get("iv");
        if (ivBytes == null) {
            throw new IllegalArgumentException("IV parameter is required for this mode");
        }
        return ivBytes;
    }
}
