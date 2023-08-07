package com.blossom.common.base;

import cn.hutool.core.util.StrUtil;
import com.blossom.common.base.enums.FrameworkEnum;
import com.blossom.common.base.util.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;


/**
 * 基础静态参数
 *
 * @author xzzz
 */
@Slf4j
public final class BaseConstants {

    /**
     * 项目名称
     */
    public static final String PROJECT_NAME = "blossom-backend";

    /**
     * 项目说明
     */
    public static void desc() {
        log.info("启动完成" +
                        "\n==========================================================" +
                        "\n项目名称: [{}]" +
                        "\n当前环境: [{}], 版本: [{}]" +
                        "\n访问地址: http://127.0.0.1:{}{}" +
                        "\n" +
                        "\n使用框架: {}" +
                        "\n==========================================================",
                SpringUtil.getAppName(),
                SpringUtil.getProfileAction(), SpringUtil.get("project.base.version"),
                SpringUtil.getPort(), getContextPath(),
                FrameworkEnum.getExistFrameworkStr()
        );
    }

    private static String getContextPath() {
        String contextPath = SpringUtil.getServletContextPath();
        if (StrUtil.isNotBlank(contextPath)) {
            return contextPath + "/";
        }
        return "/";
    }
}
