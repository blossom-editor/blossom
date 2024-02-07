package com.blossom.backend.base.auth.interceptor;

import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.auth.annotation.AuthIgnore;
import com.blossom.backend.base.auth.annotation.AuthUserType;
import com.blossom.backend.base.auth.exception.AuthException;
import com.blossom.backend.base.auth.exception.AuthRCode;
import com.blossom.backend.base.user.UserTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 接口对于用户的校验
 */
@Slf4j
public class UserTypeInterceptor implements HandlerInterceptor {

    /**
     * @param request  request
     * @param response response
     * @return 通过与否
     * @throws Exception 异常处理
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 忽略静态资源处理器
        if (handler instanceof ResourceHttpRequestHandler) {
            return true;
        }

        // 必须强转为HandlerMethod
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 白名单不校验
        boolean isIgnore = handlerMethod.hasMethodAnnotation(AuthIgnore.class);
        if (isIgnore) {
            return true;
        }

        // 校验接口允许的用户类型
        boolean isCheckUserType = handlerMethod.hasMethodAnnotation(AuthUserType.class);
        if (isCheckUserType) {
            AuthUserType userType = handlerMethod.getMethodAnnotation(AuthUserType.class);
            if (userType != null) {
                UserTypeEnum type = userType.value();
                if (!type.getType().equals(AuthContext.getType())) {
                    throw new AuthException(AuthRCode.PERMISSION_DENIED);
                }
                return true;
            }
        }

        // 只读账号不非 GET 请求
        if (UserTypeEnum.READONLY.getType().equals(AuthContext.getType()) && !HttpMethod.GET.name().equals(request.getMethod())) {
            throw new AuthException(AuthRCode.PERMISSION_DENIED.getCode(), "您的账号为只读账号, 无法使用该功能");
        }

        return true;
    }
}
