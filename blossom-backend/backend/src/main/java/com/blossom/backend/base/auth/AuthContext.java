package com.blossom.backend.base.auth;

import cn.hutool.core.util.StrUtil;
import com.blossom.backend.base.auth.pojo.AccessToken;
import com.blossom.backend.base.auth.redis.RedisTokenValidateFilter;
import com.blossom.common.base.exception.XzException500;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 授权上下文
 * <p>
 * 设置: {@link RedisTokenValidateFilter}
 * 删除: {@link RedisTokenAuthFilterProxy#doFilter(ServletRequest, ServletResponse, FilterChain)}
 *
 * @author xzzz
 * @since 0.0.1
 */
public class AuthContext {

    private static final ThreadLocal<AccessToken> TOKEN_CONTEXT = new ThreadLocal<>();

    // region

    /**
     * 获取授权上下文
     *
     * @return 授权信息
     */
    public static AccessToken getContext() {
        return TOKEN_CONTEXT.get();
    }

    /**
     * 设置上下文信息
     *
     * @param accessToken 授权信息
     */
    public static void setContext(AccessToken accessToken) {
        TOKEN_CONTEXT.set(accessToken);
    }

    /**
     * 删除上下文
     */
    public static void removeContext() {
        TOKEN_CONTEXT.remove();
    }

    /**
     * 获取token 信息
     *
     * @return token
     */
    public static String getToken() {
        return TOKEN_CONTEXT.get().getToken();
    }

    // endregion

    /**
     * 获取UID
     *
     * @return 用户ID
     */
    public static Long getUserId() {
        AccessToken accessToken = getContext();
        if (accessToken == null) {
            return null;
        }
        return getContext().getUserId();
    }

    /**
     * 获取用户的类型
     *
     * @return 用户的类型
     */
    public static Integer getType() {
        Map<String, String> metadata = getUserMetadata();
        String type = metadata.get("type");
        if (StrUtil.isBlank(type)) {
            throw new XzException500("用户类型错误, 请重新登录");
        }
        return Integer.valueOf(type);
    }


    /**
     * 获取BID
     *
     * @return 商铺 ID
     */
    public static Map<String, String> getUserMetadata() {
        AccessToken accessToken = getContext();
        if (accessToken == null) {
            return new HashMap<>(0);
        }
        return accessToken.getMetadata();
    }

    /**
     * 用户用户ID及用户名
     */
    public static String getIdAndName() {
        String nameAndId = "0,Null";
        try {
            AccessToken accessToken = getContext();
            if (accessToken != null) {
                nameAndId = getUserId() + "," + getUserMetadata().get("username");
            }
        } catch (Exception ignored) {
            // 空的上下文时忽略
        }
        return nameAndId;
    }
}
