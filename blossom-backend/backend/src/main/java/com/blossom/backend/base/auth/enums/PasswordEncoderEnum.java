package com.blossom.backend.base.auth.enums;

/**
 * 密码加密方式
 *
 * @author xzzz
 */
public enum PasswordEncoderEnum {
    /**
     * md5 与 sha256 为匹配 hash 值.
     */
    md5,
    /**
     * md5 与 sha256 为匹配 hash 值.
     */
    sha256,
    /**
     * bcrypt 安全性最高, 但加解密最慢.
     */
    bcrypt;
}
