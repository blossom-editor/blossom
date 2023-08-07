package com.blossom.common.base.util.okhttp;

import okhttp3.Interceptor;

/**
 * OKHTTP 拦截器, 由于 OKHTTP 为工具类, 拦截器不好拓展, 所以使用 SPI 方式由引用方实现
 *
 * @author xzzz
 */
public interface OkHttpInterceptor extends Interceptor {

    /**
     * 说明该拦截器实现的作用
     */
    void instructions();
}
