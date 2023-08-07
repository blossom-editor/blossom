package com.blossom.common.base.util;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;

/**
 * 代理辅助方法
 *
 * @author xzzz
 */
public final class ProxyUtils {

    /**
     * 创建指定对象的代理类
     *
     * @param obj         对象
     * @param interceptor 代理方法
     * @return 代理类
     */
    public static Object getProxy(Object obj, MethodInterceptor interceptor) {
        ProxyFactory proxy = new ProxyFactory(obj);
        proxy.setProxyTargetClass(true);
        proxy.addAdvice(interceptor);
        return proxy.getProxy();
    }
}