/*
 * Copyright 1999-2019 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blossom.expand.sentinel.mvc;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.blossom.expand.sentinel.mvc.callback.DefaultBlockExceptionHandler;
import com.blossom.expand.sentinel.mvc.callback.UrlCleaner;
import com.blossom.expand.sentinel.mvc.config.SentinelWebMvcConfig;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Spring Web MVC interceptor that integrates with Sentinel.
 *
 * @author kaizi2009
 * @since 1.7.1
 */
public class SentinelWebInterceptor extends AbstractSentinelInterceptor {

    private final SentinelWebMvcConfig config;

    public SentinelWebInterceptor() {
        this(new SentinelWebMvcConfig());
    }

    public SentinelWebInterceptor(SentinelWebMvcConfig config) {
        super(config);
        if (config == null) {
            // Use the default config by default.
            this.config = new SentinelWebMvcConfig();
            // 设置阻塞异常处理器
        } else {
            this.config = config;
        }
        if (this.config.getBlockExceptionHandler() == null) {
            this.config.setBlockExceptionHandler(new DefaultBlockExceptionHandler());
        }
    }

    @Override
    protected String getResourceName(HttpServletRequest request) {
        // Resolve the Spring Web URL pattern from the request attribute.
        Object resourceNameObject = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (resourceNameObject == null || !(resourceNameObject instanceof String)) {
            return null;
        }
        String resourceName = (String) resourceNameObject;
        UrlCleaner urlCleaner = config.getUrlCleaner();
        if (urlCleaner != null) {
            resourceName = urlCleaner.clean(resourceName);
        }
        // Add method specification if necessary
        if (StringUtil.isNotEmpty(resourceName) && config.isHttpMethodSpecify()) {
            resourceName = request.getMethod().toUpperCase() + ":" + resourceName;
        }
        return resourceName;
    }

    @Override
    protected String getContextName(HttpServletRequest request) {
        if (config.isWebContextUnify()) {
            return super.getContextName(request);
        }

        return getResourceName(request);
    }
}
