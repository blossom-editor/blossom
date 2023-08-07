package com.blossom.backend.base.auth;

import cn.hutool.core.util.StrUtil;
import com.blossom.common.base.util.spring.SpringUtil;


/**
 * @author xzzz
 * @since 0.0.1
 */
public class TokenUtil {

    private static final String TOKEN_KEY = "auth:token";
    private static final String TOKEN_UNIQUE_KEY = "auth:token_unique";

    public static String buildTokenKey(String token) {
        return SpringUtil.getAppName() + ":" + TOKEN_KEY + ":" + token;
    }

    public static String buildUniqueTokenKey(String token) {
        return SpringUtil.getAppName() + ":" + TOKEN_UNIQUE_KEY + ":" + token;
    }

    /**
     * 截取 token 前缀(Bearer), 返回 token
     *
     * @param tokenStr token 字符串
     * @return token
     */
    public static String cutPrefix(String tokenStr) {
        if (tokenStr == null || tokenStr.length() == 0) {
            return null;
        }
        if (StrUtil.startWith(tokenStr, AuthConstant.HEADER_TOKEN_PREFIX)) {
            return StrUtil.replace(tokenStr, AuthConstant.HEADER_TOKEN_PREFIX, "");
        }
        return tokenStr;
    }

}
