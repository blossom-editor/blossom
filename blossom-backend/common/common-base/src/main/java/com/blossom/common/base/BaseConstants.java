package com.blossom.common.base;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.yaml.YamlUtil;
import com.blossom.common.base.util.spring.SpringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


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

    private static final List<CheckEntry> checkEntry = new ArrayList<CheckEntry>() {{
        this.add(new CheckEntry("spring.datasource.url", "数据库链接"));
        this.add(new CheckEntry("spring.datasource.username", "数据库用户"));
        this.add(new CheckEntry("spring.datasource.password", "数据库密码"));
        this.add(new CheckEntry("project.iaas.blos.domain", "图片前缀地址"));
        this.add(new CheckEntry("project.iaas.blos.default-path", "图片存储地址"));
    }};

    /**
     * 检查 yml 配置文件
     *
     * @since 1.8.0
     */
    public static void checkYml() {
        Dict prod = YamlUtil.loadByPath("config\\application-dev.yml");
        log.info("正在检查 Blossom 后台配置项" +
                "\n===================================================================================================");

        for (CheckEntry entry : checkEntry) {
            if (ObjUtil.isNotNull(prod.getByPath(entry.getPath())) && StrUtil.isNotBlank(prod.getByPath(entry.getPath()).toString())) {
                log.info("{}: {}", entry.getName(), prod.getByPath(entry.getPath()).toString());
            } else {
                log.error("[错误] 未配置{} > [{}]", entry.getName(), entry.getPath());
            }
        }

        log.info("\n===================================================================================================");
    }

    /**
     * 项目说明
     */
    public static void desc() {
        log.info("启动完成" +
                        "\n=============================================================" +
                        "\n项目名称: [{}]" +
                        "\n当前环境: [{}], 版本: [{}]" +
                        "\n访问地址: http://127.0.0.1:{}{}" +
                        "\n=============================================================" +
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

    @Data
    private static class CheckEntry {
        private String path;
        private String name;

        public CheckEntry(String path, String name) {
            this.path = path;
            this.name = name;
        }
    }
}
