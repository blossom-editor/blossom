package com.blossom.common.base.util.security;

/**
 * BCrypt 加密, 每次加密密文不同, 需要原文和密文进行比较
 *
 * @author xzzz
 * @since 0.0.1
 */
@SuppressWarnings("all")
public class BCryptUtil {

    private static BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    /**
     * 加密
     *
     * @param plaintext 明文
     * @param salt      盐值
     * @return 密文
     */
    public static String encode(CharSequence plaintext, String salt) {
        return encode(plaintext + salt);
    }

    /**
     * 加密
     *
     * @param plaintext 明文
     * @return 密文
     */
    public static String encode(CharSequence plaintext) {
        return bCryptPasswordEncoder.encode(plaintext);
    }

    /**
     * 匹配密文与明文
     *
     * @param plaintext 密码明文
     * @param salt      盐值
     * @param ciphetext 密码密文
     * @return 密码是否匹配
     */
    public static boolean matches(CharSequence plaintext, String salt, String ciphetext) {
        return matches(plaintext + salt, ciphetext);
    }

    /**
     * 匹配密文与明文
     *
     * @param plaintext 密码明文
     * @param ciphetext 密码密文
     * @return 密码是否匹配
     */
    public static boolean matches(CharSequence plaintext, String ciphetext) {
        return bCryptPasswordEncoder.matches(plaintext, ciphetext);
    }

    public static void main(String[] args) {
        System.out.println("begin");
        String content = "abc";
        // admin 存储在数据库中都密码
        String adminPassword = "$2a$10$gnX5RJhJ/mKbR4slN/8aWOor0/wX1UafXtrbFJyliYa6B7eCyq1wa";
        Long startBCryptMatches = System.currentTimeMillis();
        // 方法1：判断密码是否正确，row不需要是加密后都密文
        boolean correct1 = BCryptUtil.matches(content, adminPassword);
        Long endBCryptMatches = System.currentTimeMillis();
        System.out.println("解密耗时:" + (endBCryptMatches - startBCryptMatches));
    }
}
