package org.m2sec.core.utils;

import org.bouncycastle.util.encoders.Hex;

import java.util.Arrays;
import java.util.Base64;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
public class ByteUtil {

    /**
     * 从startIndex开始获取data中chr出现的第一个下标
     *
     * @param data       原始数据
     * @param chr        指定的字符
     * @param startIndex 开始的下标
     * @return 找到返回下标，反之返回-1
     */
    public static int findFirstCharIndex(byte[] data, char chr, int startIndex) {
        for (int i = startIndex; i < data.length; i++) {
            if ((data[i] & 0xff) == chr) {
                return i;
            }
        }
        return -1; // 如果没有找到，返回-1
    }

    /**
     * 从startIndex开始查询str出现的第一个下标
     *
     * @param data       原始数据
     * @param str        指定的字符串
     * @param startIndex 开始的下标
     * @return 找到返回下标，反之返回-1
     */
    public static int findFirstStringIndex(byte[] data, String str, int startIndex) {
        if (str == null || data == null || startIndex < 0 || startIndex >= data.length) {
            return -1; // 返回-1表示未找到
        }

        byte[] strBytes = str.getBytes();
        int strLength = strBytes.length;
        int byteArrayLength = data.length;

        for (int i = startIndex; i <= byteArrayLength - strLength; i++) {
            boolean found = true;
            for (int j = 0; j < strLength; j++) {
                if (data[i + j] != strBytes[j]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return i; // 返回字符串在字节数组中的起始下标
            }
        }
        return -1; // 返回-1表示未找到
    }

    /**
     * 替换original中startIndex->endIndex中的数据为replacement
     *
     * @param original    原始数据
     * @param startIndex  开始下标
     * @param endIndex    结束下标
     * @param replacement 要替换的byte[]
     * @return 新的已被替换的数组
     */
    public static byte[] replaceBytes(byte[] original, int startIndex, int endIndex, byte[] replacement) {
        if (startIndex < 0 || endIndex > original.length || startIndex > endIndex) {
            throw new IllegalArgumentException(String.format("Invalid startIndex or endIndex index. original: %s, " + "startIndex: %d, endIndex: %d, replacement: %s", Arrays.toString(original), startIndex, endIndex, Arrays.toString(replacement)));
        }

        // 计算替换后的数组长度
        int length = original.length - (endIndex - startIndex) + replacement.length;

        // 创建一个新的 byte 数组，用于存储替换后的内容
        byte[] result = new byte[length];

        // 复制替换前的内容到新数组
        System.arraycopy(original, 0, result, 0, startIndex);
        System.arraycopy(replacement, 0, result, startIndex, replacement.length);
        System.arraycopy(original, endIndex, result, startIndex + replacement.length, original.length - endIndex);

        return result;
    }

    /**
     * 截取byte数组
     *
     * @param originalArray 原始数据
     * @param startIndex    开始下标
     * @param endIndex      结束下表
     * @return startIndex -> endIndex处的byte数组
     */
    public static byte[] subBytes(byte[] originalArray, int startIndex, int endIndex) {
        // 检查startIndex和endIndex的有效性
        if (startIndex < 0 || endIndex - 1 >= originalArray.length || startIndex > endIndex - 1) {
            throw new IllegalArgumentException(String.format("Invalid startIndex or endIndex. originalArray: %s, " +
                "startIndex: %d, endIndex: %d", Arrays.toString(originalArray), startIndex, endIndex));
        }
        // 使用Arrays.copyOfRange方法截取指定范围的byte数组
        return Arrays.copyOfRange(originalArray, startIndex, endIndex);
    }


    public static String toHexString(byte[] data) {
        return Hex.toHexString(data);
    }

    public static String toBase64String(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
}
