package com.blossom.backend.base.auth.redis;


import cn.hutool.core.util.StrUtil;
import com.blossom.backend.base.auth.AuthConstant;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.auth.TokenUtil;
import com.blossom.backend.base.auth.exception.AuthException;
import com.blossom.backend.base.auth.exception.AuthRCode;
import com.blossom.backend.base.auth.pojo.AccessToken;
import com.blossom.backend.base.auth.repo.TokenRepository;
import com.blossom.common.base.util.ServletUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 请求验证, 校验请求是否携带 token, 未携带的进行拦截跑出错误
 *
 * @author xzzz
 * @since 0.0.1
 */
@Slf4j
public class RedisTokenValidateFilter {

    private final TokenRepository tokenRepository;

    public RedisTokenValidateFilter(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * 执行过滤器
     *
     * @param request         request
     * @param response        response
     * @throws IOException      io
     * @throws ServletException servlet
     */
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        log.debug("[AUTHORIZ] filter(100) 授权校验过滤器");

        boolean isWhiteList = Boolean.TRUE.equals(request.getAttribute(AuthConstant.WHITE_LIST_ATTRIBUTE_KEY));

        // 解析请求头中的 token
        String token = getHeaderToken(request);

        // 如果白名单且无token, 则直接放行
        if (isWhiteList && StrUtil.isBlank(token)) {
            return;
        }

        // 根据 token 取出 Redis 中的授权数据
        AccessToken accessToken = tokenRepository.getToken(token);

        // 如果是白名单请求没有授权信息,则构造空的授权信息到上下文,异常由调用方捕获
        if (isWhiteList && null == accessToken) {
            AccessToken accessTokenNull = new AccessToken();
            accessTokenNull.setToken("");
            accessTokenNull.setUserId(0L);
            setAuthContext(accessTokenNull);
            return;
        }

        // 如果白名单请求,且获得了token,则说明是登录状态请求白名单的接口,正常执行下一过滤器
        if (isWhiteList && null != accessToken) {
            // 将授权数据存放到上下文中,不执行后续过滤器
            setAuthContext(accessToken);
            return;
        }
        // 如果不是白名单请求,则抛出错误
        if (!isWhiteList && null == accessToken) {
            throw new AuthException(AuthRCode.INVALID_TOKEN);
        }
        // 将授权数据存放到上下文中,并执行下一过滤器
        setAuthContext(accessToken);
    }

    /**
     * 1. 从请求头中获取 Token
     *
     * @param servletRequest request
     * @return Token
     */
    private String getHeaderToken(ServletRequest servletRequest) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        // 从请求头获取
        String token = httpServletRequest.getHeader(AuthConstant.HEADER_AUTHORIZATION);

        // 从请求头获取全小写
        if (StrUtil.isBlank(token)) {
            token = httpServletRequest.getHeader(AuthConstant.HEADER_AUTHORIZATION.toLowerCase());
        }

        // 从请求头获取全大写
        if (StrUtil.isBlank(token)) {
            token = httpServletRequest.getHeader(AuthConstant.HEADER_AUTHORIZATION.toUpperCase());
        }

        // 从 cookie 获取
        if (StrUtil.isBlank(token)) {
            Cookie cookie = ServletUtil.getCookie(httpServletRequest, AuthConstant.HEADER_AUTHORIZATION);
            if (cookie != null) {
                token = cookie.getValue();
            }
        }

        // 以 Bearer 开头 && 除此外长度 > 0
        token = TokenUtil.cutPrefix(token);
        if (null != token && token.length() > 0) {
            return token;
        }

        return null;
    }

    /**
     * 3. 保存到上下文
     *
     * @param accessToken 授权主体
     */
    private void setAuthContext(AccessToken accessToken) {
        AuthContext.setContext(accessToken);
    }
}
