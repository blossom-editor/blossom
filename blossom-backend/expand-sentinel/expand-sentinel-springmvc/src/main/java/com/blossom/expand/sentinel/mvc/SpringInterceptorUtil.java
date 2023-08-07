package com.blossom.expand.sentinel.mvc;

import javax.servlet.http.HttpServletRequest;

public class SpringInterceptorUtil {

    private static final String REQUEST_ATTRIBUTE_SENTINEL_EXCEPTION = "request_attribute_sentinel_exception";

    public static void fillException(HttpServletRequest request, Exception ex) {
        request.setAttribute(REQUEST_ATTRIBUTE_SENTINEL_EXCEPTION, ex);
    }

    public static Exception getException(HttpServletRequest request) {
        Object ex = request.getAttribute(REQUEST_ATTRIBUTE_SENTINEL_EXCEPTION);
        if (ex != null) {
            return (Exception) ex;
        }
        return null;
    }
}
