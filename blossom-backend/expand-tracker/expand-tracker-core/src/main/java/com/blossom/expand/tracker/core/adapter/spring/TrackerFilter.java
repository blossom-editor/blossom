package com.blossom.expand.tracker.core.adapter.spring;

import cn.hutool.core.util.StrUtil;
import com.blossom.expand.tracker.core.common.TrackerConstants;
import com.blossom.expand.tracker.core.Tracker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.GenericFilterBean;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Rest 端点的过滤器配置
 *
 * @author xzzz
 * @since 1.2.0
 */
@Slf4j
@Order(Integer.MIN_VALUE)
public class TrackerFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            try {
                HttpServletRequest request = (HttpServletRequest) servletRequest;
                String header = request.getHeader(TrackerConstants.HTTP_HEADERS);

                boolean isFork = false;

                String[] params = null;
                if (StrUtil.isNotBlank(header)) {
                    params = header.split("\\|");
                    if (params.length == 3) {
                        isFork = true;
                    }
                }
                if (isFork) {
                    Tracker.fork(request.getMethod() + ":" + request.getServletPath(), params[2], params[0], params[1]);
                } else {
                    Tracker.start(request.getMethod() + ":" + request.getServletPath(), TrackerConstants.SPAN_TYPE_HTTP_MVC);
                }

            } catch (Exception ignored) {
            }
            filterChain.doFilter(servletRequest,response);
        } finally {
            Tracker.end();
        }
    }

    @PostConstruct
    public void init () {
        log.debug("[TRACKERS] 已经适配框架 : Spring Filter");
    }

}
