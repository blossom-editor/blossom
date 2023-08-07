package com.blossom.backend.base.auth.security;

import com.blossom.common.base.util.security.SHA256Util;

/**
 * sha256 加解密匹配
 *
 * @author xzzz
 */
public class PasswordEncoderSHA256 implements PasswordEncoder{

    @Override
    public String encode(CharSequence rawPassword) {
        return SHA256Util.encode(String.valueOf(rawPassword));
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return SHA256Util.encode(String.valueOf(rawPassword)).equals(encodedPassword);
    }
}
