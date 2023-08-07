package com.blossom.backend.base.auth.security;

import com.blossom.backend.base.auth.AuthProperties;
import com.blossom.backend.base.auth.enums.PasswordEncoderEnum;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * 密码处理
 *
 * @author xzzz
 */
@Primary
@Component
public class PasswordManager implements PasswordEncoder {

    private final AuthProperties authProperties;
    private final PasswordEncoder passwordEncoder;

    public PasswordManager(AuthProperties authProperties) {
        this.authProperties = authProperties;
        if (authProperties.getPasswordEncoder() == PasswordEncoderEnum.md5) {
            passwordEncoder = new PasswordEncoderMD5();
        } else if (authProperties.getPasswordEncoder() == PasswordEncoderEnum.sha256) {
            passwordEncoder = new PasswordEncoderSHA256();
        } else {
            passwordEncoder = new PasswordEncoderBCrypt();
        }
    }

    public String getDefaultPassword() {
        return passwordEncoder.encode(authProperties.getDefaultPassword());
    }

    /**
     * 明文加密, 如果密码需要加盐, 需要加盐后传入
     *
     * @param rawPassword 密码明文
     * @return 密码密文
     */
    @Override
    public String encode(CharSequence rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 密码判断，用未加密的密码与已加密的密码进行判断
     *
     * @param rawPassword     原始密码，未加密的密码
     * @param encodedPassword 已经加密过的密码
     * @return true 为密码正确
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
