package org.m2sec.core.httphook;

/**
 * @author: outlaws-bai
 * @date: 2024/7/12 22:52
 * @description:
 */

public interface ICodeHookerFactor {

    void init(String filepath);

    byte[] encrypt(byte[] data);

    byte[] decrypt(byte[] data);
}
