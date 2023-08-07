package com.blossom.backend.base.auth.pojo;

import org.springframework.context.ApplicationEvent;

/**
 * 登录事件
 *
 * @author xzzz
 */
public class LoginEvent extends ApplicationEvent {

    public LoginEvent(LoginDTO source) {
        super(source);
    }
}
