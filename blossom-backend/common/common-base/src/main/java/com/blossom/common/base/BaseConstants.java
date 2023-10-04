package com.blossom.common.base;

import cn.hutool.core.util.StrUtil;
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
                        "\n=============================================================" +
                        "\n项目名称: [{}]" +
                        "\n当前环境: [{}], 版本: [{}]" +
                        "\n访问地址: http://127.0.0.1:{}{}" +
                        "\n" +
                        "\n启动成功: 可下载客户端登录, 默认用户名密码: blos/blos" +
                        "\n下载地址: https://github.com/blossom-editor/blossom/releases" +
                        "\n=============================================================",
                SpringUtil.getAppName(),
                SpringUtil.getProfileAction(), SpringUtil.get("project.base.version"),
                SpringUtil.getPort(), getContextPath()
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
