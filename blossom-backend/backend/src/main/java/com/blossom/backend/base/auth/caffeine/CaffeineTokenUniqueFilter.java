package com.blossom.backend.base.auth.caffeine;

import com.blossom.backend.base.auth.AuthConstant;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.auth.exception.AuthException;
import com.blossom.backend.base.auth.exception.AuthRCode;
import com.blossom.backend.base.auth.pojo.AccessToken;
import com.blossom.backend.base.auth.repo.TokenRepository;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * redis 唯一token 拦截器, 用于处理用户多设备登录时的 唯一有效token 判断逻辑
 *
 * @author xzzz
 * @since 1.3.0
 */
@Slf4j
public class CaffeineTokenUniqueFilter {

    /**
     * 配置文件内容
     */
    private final TokenRepository tokenRepository;

    public CaffeineTokenUniqueFilter(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * 执行过滤器
     *
     * @param request  request
     * @param response response
     * @throws IOException      io
     * @throws ServletException servlet
     */
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        boolean isWhiteList = Boolean.TRUE.equals(request.getAttribute(AuthConstant.WHITE_LIST_ATTRIBUTE_KEY));

        if (isWhiteList) {
            return;
        }

        AccessToken accessToken = AuthContext.getContext();

        /*
         * 如果不允许多地登录, 则要判断当前用户是否是唯一的用户
         */
        if (!accessToken.getMultiPlaceLogin()) {
            String uniqueToken = tokenRepository.getUniqueToken(String.valueOf(accessToken.getUserId()));
            if (!accessToken.getToken().equals(uniqueToken)) {
                tokenRepository.remove(accessToken.getToken());
                throw new AuthException(AuthRCode.ANOTHER_DEVICE_LOGIN);
            }
        }
    }


}
