//package com.blossom.backend.base.auth.redis;
//
//import com.blossom.backend.base.auth.AuthConstant;
//import com.blossom.backend.base.auth.AuthProperties;
//import com.blossom.backend.base.auth.repo.TokenRepository;
//import com.blossom.backend.base.auth.filters.AuthFilterProxy;
//import com.blossom.backend.base.auth.filters.HttpFirewall;
//import com.blossom.backend.base.auth.filters.RequestLogFilter;
//import com.blossom.backend.base.auth.filters.WhiteListFilter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.core.annotation.Order;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import java.io.IOException;
//
///**
// * redis token 拦截器实现
// *
// * @author xzzz
// */
//@Slf4j
//@Component
//@Order(AuthConstant.AUTH_FILTER_PROXY)
//@ConditionalOnClass(RedisTemplate.class)
//@ConditionalOnProperty(value = "project.auth.type", havingValue = "redis")
//public class RedisTokenAuthFilterProxy extends AuthFilterProxy {
//    /**
//     * 防火墙对象
//     */
//    protected final HttpFirewall httpFirewall = new HttpFirewall();
//    /**
//     * 日期过滤器
//     */
//    protected final RequestLogFilter logFilter;
//    /**
//     * 白名单过滤器
//     */
//    protected final WhiteListFilter whiteListFilter;
//    /**
//     * 授权检查过滤器
//     */
//    private final RedisTokenValidateFilter redisTokenValidateFilter;
//    /**
//     * 唯一授权检查过滤器
//     */
//    private final RedisTokenUniqueFilter redisTokenUniqueFilter;
//    /**
//     * 授权重置过滤器
//     */
//    private final RedisTokenExpireResetFilter redisTokenExpireResetFilter;
//
//    public RedisTokenAuthFilterProxy(AuthProperties properties, TokenRepository tokenRepository) {
//        super(properties);
//        this.logFilter = new RequestLogFilter(properties);
//        this.whiteListFilter = new WhiteListFilter(properties);
//        this.redisTokenValidateFilter = new RedisTokenValidateFilter(tokenRepository);
//        this.redisTokenUniqueFilter = new RedisTokenUniqueFilter(tokenRepository);
//        this.redisTokenExpireResetFilter = new RedisTokenExpireResetFilter(tokenRepository);
//    }
//
//    /**
//     * <p>1. 防火墙校验
//     * <p>2. 授权校验
//     * <ol>
//     *     <li>日志</li>
//     *     <li>白名单</li>
//     *     <li>授权校验</li>
//     *     <li>授权续期</li>
//     * </ol>
//     *
//     * @param request  request
//     * @param response response
//     * @param chain    chain
//     */
//    @Override
//    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        /*
//         1. 执行防火墙
//         */
//        httpFirewall.wall(request);
//
//        /*
//         2. 授权校验
//         */
//        // 2.1 日志
//        logFilter.doFilter(request, response);
//        // 2.2 白名单
//        whiteListFilter.doFilter(request, response);
//        // 2.3 token 校验
//        redisTokenValidateFilter.doFilter(request, response);
//        // 2.4 token 唯一校验
//        redisTokenUniqueFilter.doFilter(request, response);
//        // 2.5 过期时间刷新
//        redisTokenExpireResetFilter.doFilter(request, response);
//
//        /*
//         虚拟过滤器链执行完毕后交由原生过滤器继续执行
//         */
//        chain.doFilter(request, response);
//
//        log.debug("[AUTHORIZ] **Proxy** >> 原生过滤器: request  请求结束");
//    }
//
//}
