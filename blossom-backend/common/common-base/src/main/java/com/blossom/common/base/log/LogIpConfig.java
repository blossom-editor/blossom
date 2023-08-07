package com.blossom.common.base.log;

import ch.qos.logback.core.PropertyDefinerBase;
import com.blossom.common.base.util.SystemUtil;

/**
 * 使用在 logback-spring.xml 中, 作为日志收集时的机器IP
 *
 * @author xzzz
 */
public class LogIpConfig extends PropertyDefinerBase {

    private static final String IP;

    static {
        IP = SystemUtil.getIp();
    }

    @Override
    public String getPropertyValue() {
        return IP;
    }
}
