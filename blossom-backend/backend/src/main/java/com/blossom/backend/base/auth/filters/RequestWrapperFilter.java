package com.blossom.backend.base.auth.filters;

import com.blossom.backend.base.auth.AuthConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 包装request, 提供多次读取请求体的机会
 *
 * @author wangyf
 * @since 0.0.1
 */
@Slf4j
@Component
@Order(AuthConstant.AUTH_FILTER_WRAPPER_ORDER)
public class RequestWrapperFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // ContentType 为 "application/json" 格式才使用包装类
        // 这样是为了过滤上传文件等类型的请求
        if (request.getContentType() != null && request.getContentType().contains("application/json")) {
            ServletRequest requestWrapper = new RequestBodyReaderWrapper(request);
            filterChain.doFilter(requestWrapper, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
