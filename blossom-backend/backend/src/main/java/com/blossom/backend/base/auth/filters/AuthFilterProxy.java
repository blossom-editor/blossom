package com.blossom.backend.base.auth.filters;

import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.auth.AuthProperties;
import com.blossom.backend.base.auth.exception.AuthException;
import com.blossom.backend.base.auth.exception.AuthRCode;
import com.blossom.common.base.BaseProperties;
import com.blossom.common.base.enums.ExFormat;
import com.blossom.common.base.enums.ExStackTrace;
import com.blossom.common.base.pojo.IRCode;
import com.blossom.common.base.pojo.R;
import com.blossom.common.base.pojo.RCode;
import com.blossom.common.base.util.ServletUtil;
import com.blossom.common.base.util.json.JsonUtil;
import com.blossom.common.base.util.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

/**
 * 授权过滤器抽象类, 授权有 JWT 和 有状态TOKEN 两种实现方式, 所以具体的过滤器逻辑需要
 * 子类重写 {@link AuthFilterProxy#doFilterInternal } 方法
 *
 * @author xzzz
 */
@Slf4j
public abstract class AuthFilterProxy extends GenericFilterBean {
    /**
     * 授权配置
     */
    protected final AuthProperties properties;

    public AuthFilterProxy(AuthProperties properties) {
        this.properties = properties;
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!properties.getEnabled()) {
            chain.doFilter(request, response);
        } else {
            log.debug("[AUTHORIZ] ================================ 代理过滤器 [开始] ================================");
            try {
                this.doFilterInternal(request, response, chain);
            } catch (Exception e) {
                log.debug("[AUTHORIZ] **Proxy** >> 代理过滤器执行异常: {}", e.getMessage());
                onAuthenticationFailure(request, response, e);
            } finally {
                // 无论执行成功与否都需要清空上下文
                AuthContext.removeContext();
                log.debug("[AUTHORIZ] **Proxy** << 代理过滤器: response 清空上下文: {}", JsonUtil.toJson(AuthContext.getContext()));
            }
            log.debug("[AUTHORIZ] ================================ 代理过滤器 [结束] ================================");
        }
    }

    /**
     * 执行过滤器, 由不同的子过滤器实现具体的过滤器内容
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    protected abstract void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException;

    /**
     * 执行时出现异常, 在此捕获并统一处理
     *
     * @param request   request
     * @param response  response
     * @param exception ex
     */
    protected void onAuthenticationFailure(ServletRequest request, ServletResponse response, Exception exception) throws IOException {
        HttpServletResponse resp = (HttpServletResponse) response;

        resp.setContentType("application/json;charset=utf-8");
        // 要求所有响应都为 200
        resp.setStatus(200);

        PrintWriter out = resp.getWriter();

        // 自定义响应码
        IRCode authCode = RCode.INTERNAL_SERVER_ERROR;
        if (exception instanceof AuthException) {
            authCode = AuthRCode.getByCode(((AuthException) exception).getCode());
        }

        printStackTrace(authCode, exception);

        R<Object> apiResult = R.fault(
                authCode.getCode(),
                authCode.getMsg(),
                exception.getMessage());
        String resultJson = JsonUtil.toJson(apiResult);
        out.write(resultJson);
        out.flush();
        out.close();
    }

    protected void printStackTrace(IRCode authCode, Exception exception) {
        boolean filterStackTrace = false;
        boolean onLine = false;

        try {
            BaseProperties properties = SpringUtil.getBean(BaseProperties.class);
            filterStackTrace = properties.getEx().getStackTrace().equals(ExStackTrace.project);
            onLine = properties.getEx().getFormat().equals(ExFormat.line);
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.error("{} | {}({})", ServletUtil.getUri(), exception.getClass().getSimpleName(),
                Optional.ofNullable(authCode.getMsg()).orElse(""));
//        log.error(ExceptionUtil.printStackTrace(exception, filterStackTrace, onLine));
    }
}
