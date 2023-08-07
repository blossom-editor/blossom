package com.blossom.backend.base.auth.security;

import com.blossom.common.base.util.security.BCryptUtil;
import com.blossom.common.base.util.security.SaltUtil;

/**
 * bcrypt 加解密
 *
 * @author xzzz
 */
public class PasswordEncoderBCrypt implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return BCryptUtil.encode(String.valueOf(rawPassword));
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return BCryptUtil.matches(rawPassword, encodedPassword);
    }

    public static void main(String[] args) {
        System.out.println(SaltUtil.randomSalt());

        String salt = "UVeESP5NgXwb8JmjCHUK";
        String password = "blos";
        PasswordEncoderBCrypt uuid = new PasswordEncoderBCrypt();
        System.out.println(uuid.encode(password + salt));
    }

}
