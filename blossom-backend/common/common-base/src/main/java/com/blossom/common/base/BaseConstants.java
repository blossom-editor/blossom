package com.blossom.common.base;

import com.blossom.common.base.util.DateUtils;
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
                "\n启动成功 [" + DateUtils.now() + "], 可使用客户端登录, 默认用户名/密码: blos/blos" +
                "\n下载地址: https://github.com/blossom-editor/blossom/releases" +
                "\n文档地址: https://www.wangyunf.com/blossom-doc/index" +
                "\n博客端访问地址: http://IP:端口(域名)/blog/#/home" +
                "\n客户端访问地址: http://IP:端口(域名)/editor/#/settingindex" +
                "\n=========================================================================");
    }
}
