package com.blossom.common.base;

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
                "\n=========================================================================" +
                "\n启动成功: 可下载客户端登录, 默认用户名密码: blos/blos，推荐阅读使用文档" +
                "\n下载地址: https://github.com/blossom-editor/blossom/releases" +
                "\n文档地址: https://www.wangyunf.com/blossom-doc/index" +
                "\n=========================================================================");
    }
}
