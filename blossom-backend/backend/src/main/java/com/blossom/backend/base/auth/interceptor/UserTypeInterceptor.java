package com.blossom.backend.base.auth.interceptor;

import cn.hutool.core.util.ObjUtil;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.auth.annotation.AuthIgnore;
import com.blossom.backend.base.user.UserService;
import com.blossom.backend.base.user.UserTypeEnum;
import com.blossom.backend.base.user.pojo.UserEntity;
import com.blossom.common.base.exception.XzException400;
import com.blossom.common.base.exception.XzException404;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class UserTypeInterceptor implements HandlerInterceptor {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * @param request  request
     * @param response response
     * @return 通过与否
     * @throws Exception 异常处理
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //必须强转为HandlerMethod
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

        UserEntity user = userService.selectById(AuthContext.getUserId());
        XzException404.throwBy(ObjUtil.isNull(user), "未查询到你的账户信息");
        XzException400.throwBy(UserTypeEnum.READONLY.getType().equals(user.getType()), "您的账号为只读账号, 无法使用该功能");
        return true;
    }
}
