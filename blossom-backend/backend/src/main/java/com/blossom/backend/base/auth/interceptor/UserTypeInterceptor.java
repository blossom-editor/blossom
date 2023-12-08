package com.blossom.backend.base.auth.interceptor;

import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.auth.annotation.AuthIgnore;
import com.blossom.backend.base.user.UserTypeEnum;
import com.blossom.common.base.exception.XzException400;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 判断用户的类型, 只读用户只允许发送 get 请求
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
        if(handler instanceof ResourceHttpRequestHandler) {
            return true;
        }

        // 必须强转为HandlerMethod
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 白名单不校验
        boolean isIgnore = handlerMethod.hasMethodAnnotation(AuthIgnore.class);
        if (isIgnore) {
            return true;
        }
        // GET请求校验
        if (HttpMethod.GET.name().equals(request.getMethod())) {
            return true;
        }

        XzException400.throwBy(UserTypeEnum.READONLY.getType().equals(AuthContext.getType()), "您的账号为只读账号, 无法使用该功能");
        return true;
    }
}
