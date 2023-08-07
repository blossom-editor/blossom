package com.blossom.expand.tracker.core;

import cn.hutool.core.util.StrUtil;
import com.blossom.expand.tracker.core.common.TrackerConstants;

/**
 * Trace 对 HTTP 请求处理的相关工具类
 *
 * @author xzzz
 */
public class TrackerUtil {

    /**
     * 构造一个 Trace 请求头
     */
    public static String buildHeader(String type) {
        String traceId = Tracker.getTraceId();
        String spanParentId = Tracker.getSpanId();
        if (StrUtil.isNotBlank(traceId) && StrUtil.isNotBlank(spanParentId)) {
            return String.format("%s|%s|%s", traceId, spanParentId, type);
        }
        return "";
    }

    public static boolean checkIgnoreApi(String spanName) {
        for (String api : TrackerConstants.ignoreInnerApi) {
            if (spanName.contains(api)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkIgnoreSql(String spanName) {
        for (String api : TrackerConstants.ignoreInnerSql) {
            if (spanName.contains(api)) {
                return true;
            }
        }
        return false;
    }
}
