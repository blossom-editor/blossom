package com.blossom.backend.base.auth.token;

import com.blossom.backend.base.auth.pojo.AccessToken;

/**
 * token 编解码器
 *
 * @author xzzz
 */
public interface TokenEncoder {

    /**
     * token 编码成字符串
     *
     * @param accessToken token
     * @return 字符串
     */
    String encode(AccessToken accessToken);

    /**
     * token 解码
     *
     * @param string token 字符串
     * @return token
     */
    default AccessToken decode(String string) {
        return null;
    }
}
