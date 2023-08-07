package com.blossom.backend.base.auth.filters;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.blossom.backend.base.auth.AuthProperties;
import com.blossom.backend.base.auth.enums.LogTypeEnum;
import com.blossom.common.base.util.SystemUtil;
import com.blossom.common.base.util.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wangyf
 * @since 0.0.1
 */
@Slf4j
public class RequestLogFilter {
    /**
     * 配置文件内容
     */
    private AuthProperties properties;

    public RequestLogFilter(AuthProperties properties) {
        this.properties = properties;
    }

    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if (properties.getLogType().equals(LogTypeEnum.none)) {
            return;
        }

        // 显示请求路径
        try {
            if (properties.getLogType().equals(LogTypeEnum.simple)) {
                HttpServletRequest req = ((HttpServletRequest) request);
                String uri = req.getRequestURI();
                log.info("[AUTHORIZ] IP:{} > {}", StrUtil.fillAfter(SystemUtil.getRemoteIp(req), ' ', 15), uri);
            }

            // 显示请求详情,此配置会覆盖显示请求路径
            if (properties.getLogType().equals(LogTypeEnum.detail)) {
                HttpServletRequest req = ((HttpServletRequest) request);

                Enumeration<String> headerNames = req.getHeaderNames();
                Map<String, String> headers = new HashMap<>();
                while (headerNames.hasMoreElements()) {
                    String key = headerNames.nextElement();
                    String value = req.getHeader(key);
                    headers.put(key, value);
                }

                String requestBody = "";
                if (request.getContentType() != null && request.getContentType().contains("application/json")) {
                    try {
                        requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
                    } catch (Exception e) {
                        log.warn("请求体转换错误");
                    }
                }

                log.warn("[AUTHORIZ] 请求详情 [{}:{}] From [{}]" +
                                "\n请求参数: [{}]" +
                                "\n请求体　: [{}]" +
                                "\n请求头　: [{}]",
                        req.getMethod(), req.getRequestURL().toString(), SystemUtil.getRemoteIp(req),
                        StrUtil.isBlank(req.getQueryString()) ? "" : URLUtil.decode(req.getQueryString()),
                        requestBody,
                        JsonUtil.toPrettyJson(headers)
                );
            }
        } catch (Exception e) {
            log.warn("输出日志错误:" + e.getMessage());
        }
    }
}
