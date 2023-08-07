package com.blossom.backend.base.auth.jwt;

import com.blossom.backend.base.auth.AuthConstant;
import com.blossom.backend.base.auth.AuthProperties;
import com.blossom.backend.base.auth.filters.AuthFilterProxy;
import com.blossom.backend.base.auth.filters.HttpFirewall;
import com.blossom.backend.base.auth.filters.RequestLogFilter;
import com.blossom.backend.base.auth.filters.WhiteListFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * JWT 过滤器实现
 *
 * @author xzzz
 */
@Slf4j
@Component
@Order(AuthConstant.AUTH_FILTER_PROXY)
@ConditionalOnProperty(value = "project.auth.type", havingValue = "jwt", matchIfMissing = true)
public class JWTAuthFilterProxy extends AuthFilterProxy {

    /**
     * 防火墙对象
     */
    private final HttpFirewall httpFirewall = new HttpFirewall();
    /**
     * 日志过滤器
     */
    private final RequestLogFilter logFilter;
    /**
     * 白名单过滤器
     */
    private final WhiteListFilter whiteListFilter;
    /**
     * JWT校验过滤器
     */
    private final JWTValidateFilter validateFilter;

    public JWTAuthFilterProxy(AuthProperties properties, JWTTokenEncoder tokenEncoder) {
        super(properties);
        this.logFilter = new RequestLogFilter(properties);
        this.whiteListFilter = new WhiteListFilter(properties);
        this.validateFilter = new JWTValidateFilter(tokenEncoder);
    }


    /**
     * <p>1. 防火墙校验
     * <p>2. 授权校验
     * <ol>
     *     <li>日志</li>
     *     <li>白名单</li>
     *     <li>授权校验</li>
     * </ol>
     *
     * @param request  request
     * @param response response
     * @param chain    chain
     */
    @Override
    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        /*
         1. 执行防火墙
         */
        httpFirewall.wall(request);

        /*
         2. 授权校验
         */
        // 2.1 日志
        logFilter.doFilter(request, response);
        // 2.2 白名单
        whiteListFilter.doFilter(request, response);
        // 2.3 token 校验
        validateFilter.doFilter(request, response);
        chain.doFilter(request, response);
    }
}
