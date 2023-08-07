package com.blossom.expand.tracker.core.adapter.okhttp;

import com.blossom.expand.tracker.core.TrackerUtil;
import com.blossom.expand.tracker.core.common.TrackerConstants;
import com.blossom.common.base.util.okhttp.OkHttpInterceptor;
import com.blossom.expand.tracker.core.SpanContext;
import com.blossom.expand.tracker.core.Tracker;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * 链路追踪对 okhttp 的拦截
 *
 * @author xzzz
 */
@Slf4j
public class TrackerInterceptor implements OkHttpInterceptor {

    /**
     * 在请求头中增加 TRACE 请求头
     * @param chain 拦截器链
     * @return response
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        SpanContext spanContext = null;
        try {
            spanContext = Tracker.start(chain.request().method() + ":" + chain.request().url().toString(),
                    TrackerConstants.SPAN_TYPE_HTTP_OKHTTP);

            // 创建请求头
            Request request = chain.request().newBuilder().addHeader(
                    TrackerConstants.HTTP_HEADERS,
                    TrackerUtil.buildHeader(TrackerConstants.SPAN_TYPE_HTTP_OKHTTP_INVOKE)).build();
            return chain.proceed(request);
        } finally {
            if (spanContext != null) {
                Tracker.end();
            }
        }
    }

    @Override
    public void instructions() {
        log.info("[TRACKERS] 已经适配框架 : OKHTTP BY OKHTTP_INTERCEPTOR");
    }
}
