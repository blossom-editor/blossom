package com.blossom.backend.base.auth.filters;

import com.blossom.backend.base.auth.AuthConstant;
import com.blossom.backend.base.auth.AuthProperties;
import com.blossom.common.base.util.spring.AntPathMatcherUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * [0]
 *
 * 白名单拦截器, 不一定作为拦截器链的入口
 * 通过获取配置文件中的白名单列表, 对请求(URI)进行判断
 * 支持 Ant 风格, 使用 {@link org.springframework.util.AntPathMatcher} 类进行匹配
 * 在白名单中的请求不会执行后续过滤器链, 即无法获取授权上下文
 *
 * @author xzzz
 * @since 0.0.1
 */
@Slf4j
public class WhiteListFilter {

    /**
     * 配置文件内容
     */
    private final AuthProperties properties;

    public WhiteListFilter(AuthProperties properties) {
        this.properties = properties;
    }

    /**
     * 如果不在白名单 isWhiteRequest = false, 则需要走授权校验逻辑, 即继续代理过滤器链流程
     * 如果在白名单中 isWhiteRequest = true,  则中断过滤器链, 代理过滤器会调用后续原生过滤器
     *
     * @param request request
     * @param response response
     */
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        log.debug("[AUTHORIZ] filter(000) 白名单校验");

        String uri = ((HttpServletRequest)request).getRequestURI();
        // 如果请求路径在白名单中, 则允许略过请求
        for (String pattern : properties.getWhiteList()) {
            if (AntPathMatcherUtil.match(pattern, uri)) {
                request.setAttribute(AuthConstant.WHITE_LIST_ATTRIBUTE_KEY,true);
                break;
            }
        }
    }

}
