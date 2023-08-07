package com.blossom.backend.base.auth.security;

import cn.hutool.crypto.SecureUtil;

/**
 * md5 加解密匹配
 *
 * @author xzzz
 */
public class PasswordEncoderMD5 implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return SecureUtil.md5(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return SecureUtil.md5(rawPassword.toString()).equals(encodedPassword);
    }
}
