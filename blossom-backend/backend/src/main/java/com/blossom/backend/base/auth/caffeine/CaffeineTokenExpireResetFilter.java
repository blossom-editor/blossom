package com.blossom.backend.base.auth.caffeine;

import cn.hutool.core.util.StrUtil;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.auth.pojo.AccessToken;
import com.blossom.backend.base.auth.repo.TokenRepository;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * 令牌刷新, 对令牌进行刷新.
 * 不同类型的令牌刷新不同
 *
 * @author xzzz
 * @since 1.3.0
 */
@Slf4j
public class CaffeineTokenExpireResetFilter {

    /**
     * 配置文件内容
     */
    private final TokenRepository tokenRepository;

    public CaffeineTokenExpireResetFilter(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * 执行过滤器
     * @param request request
     * @param response response
     * @throws IOException io
     * @throws ServletException servlet
     */
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        log.debug("[AUTHORIZ] filter(200) 令牌续期");

        // 1. 上下文获取授权主体
        AccessToken accessToken = AuthContext.getContext();

        // 2. 如果配置客户端有效 && 客户端允许刷新令牌
        if (accessToken != null && StrUtil.isNotBlank(accessToken.getToken()) && accessToken.getRequestRefresh()) {
            // 3.1 刷新存储中的授权信息
            accessToken.setExpire(System.currentTimeMillis() + (accessToken.getDuration() * 1000));
            tokenRepository.saveToken(accessToken);
            tokenRepository.saveUniqueToken(accessToken);
            // 3.2 刷新上下文授权信息
            AuthContext.setContext(accessToken);
        } else {

        }
    }
}
