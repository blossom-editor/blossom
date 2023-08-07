package com.blossom.common.base.log;

import ch.qos.logback.core.PropertyDefinerBase;
import com.blossom.common.base.util.SystemUtil;

/**
 * 使用在 logback-spring.xml 中, 作为日志收集时的机器名
 *
 * @author xzzz
 */
public class HostNameConfig extends PropertyDefinerBase {

    private static final String HOST_NAME;

    static {
        HOST_NAME = SystemUtil.getHostName();
    }

    @Override
    public String getPropertyValue() {
        return HOST_NAME;
    }
}
