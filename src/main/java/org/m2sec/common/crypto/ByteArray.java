package org.m2sec.common.crypto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bouncycastle.util.encoders.Hex;

import java.util.Base64;

/**
 * @author: outlaws-bai
 * @date: 2024/6/12 19:31
 * @description:
 */

@Getter
@Setter
@AllArgsConstructor
public class ByteArray {
    /**
     * 原始数据类型
     */
    private byte[] data;

    public static ByteArray ofString(String str) {
        return new ByteArray(str.getBytes());
    }

    // 静态方法：通过十六进制字符串初始化Content对象
    public static ByteArray ofHex(String hexString) {
        return new ByteArray(Hex.decode(hexString));
    }

    // 静态方法：通过Base64字符串初始化Content对象
    public static ByteArray ofBase64(String base64String) {
        return new ByteArray(Base64.getDecoder().decode(base64String));
    }

    // 实例方法：将data转换为十六进制字符串
    public String toHex() {
        return Hex.toHexString(data);
    }

    // 实例方法：将data转换为Base64字符串
    public String toBase64() {
        return Base64.getEncoder().encodeToString(this.data);
    }

    @Override
    public String toString() {
        return new String(data);
    }
}
