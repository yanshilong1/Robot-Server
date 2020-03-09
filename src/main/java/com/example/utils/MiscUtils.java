package com.example.utils;

import org.apache.commons.lang3.ArrayUtils;

public class MiscUtils {
    /**
     * 拼接byte数组
     * @param array byte数组
     * @return 合并结果
     */
    public static byte[] mergeByteArray(byte[]... array) {
        byte[] result = new byte[0];
        for (byte[] bytes : array) {
            result = ArrayUtils.addAll(result, bytes);
        }
        return result;
    }
}
