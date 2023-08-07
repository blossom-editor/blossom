package com.blossom.backend.base.auth.security;

/**
 * 密码加密方式接口
 *
 * @author xzzz
 * @since 0.0.1
 */
public interface PasswordEncoder {

    /**
     * 明文加密
     * <p>
     * Encode the raw password. Generally, a good encoding algorithm applies a SHA-1 or
     * greater hash combined with an 8-byte or greater randomly generated salt.
     *
     * @param rawPassword 密码原文
     * @return 密文
     */
    String encode(CharSequence rawPassword);

    /**
     * 密码判断，用未加密的密码与已加密的密码进行判断
     *
     * @param rawPassword     原始密码，未加密的密码
     * @param encodedPassword 已经加密过的密码
     * @return true 为密码正确
     */
    boolean matches(CharSequence rawPassword, String encodedPassword);

    /**
     * Returns true if the encoded password should be encoded again for better security,
     * else false. The default implementation always returns false.
     *
     * @param encodedPassword the encoded password to check
     * @return true if the encoded password should be encoded again for better security,
     * else false.
     */
    default boolean upgradeEncoding(String encodedPassword) {
        return false;
    }
}
