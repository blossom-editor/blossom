package com.blossom.common.base.util.security;

import cn.hutool.core.util.StrUtil;

import java.util.Base64;

/**
 * 消息编码算法
 *
 * @author xzzz
 * @since 0.0.1
 */
public class Base64Util {

    public static String encrypt(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static String decrypt(String data) {
        return new String(Base64.getDecoder().decode(data));
    }

    /**
     * 是否 base64 图片, 只校验格式, 不判断内容是否为正确的图片
     * 例如: 传入 data:image/png;base64,a, 将会返回 true
     */
    public static boolean isBase64Img(String image) {
        if (StrUtil.isBlank(image)) {
            return false;
        }
        String prefix = image.substring(0, Math.max(image.indexOf(','), 0));
        return prefix.startsWith("data:image") && prefix.endsWith("base64");
    }
}
