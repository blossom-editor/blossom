package com.blossom.backend.config;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.ds.hikari.HikariDSFactory;
import cn.hutool.setting.Setting;
import com.blossom.common.base.util.spring.SpringUtil;
import com.zaxxer.hikari.pool.HikariPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

import javax.sql.DataSource;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.TimeZone;

/**
 * 启动检查配置文件内容, 用于检查配置项是否正确
 *
 * @since 1.9.0
 */
@Slf4j
public class PropertiesCheckListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final String SEPARATOR =
            "==========================================================================================================================";
    private static final String SEPARATOR2 =
            "----------------------------------------------------------------------------------------------------------------------------------";
    private static final String ERROR_SEPARATOR =
            "\n\n[ERROR] " + SEPARATOR + "\n\n";

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment env = event.getEnvironment();
        setTimeZone(env);
        log.warn("\n\n正在检查 Blossom 后台配置项\n\n" +
                        "\n[CHECK] ==========================================================================================================================" +
                        "\n[CHECK] 使用环境: [{}], 版本: [{}]" +
                        "\n[CHECK] ==========================================================================================================================" +
                        "\n[CHECK] 数据库配置: {}" +
                        "\n[CHECK] 数据库用户: {}" +
                        "\n[CHECK] 数据库密码: {}" +
                        "\n[CHECK] 文件前缀: {}, 非 [http://www.google.com/] 内容会强制覆盖后台配置" +
                        "\n[CHECK] 文件存储: {}" +
                        "\n[CHECK] 文件大小: {}" +
                        "\n[CHECK] 授权时长: {}" +
                        "\n[CHECK] 重置密码: {}" +
                        "\n[CHECK] 指定时区: {}" +
                        "\n[CHECK] ==========================================================================================================================\n\n",
                get(env, SpringUtil.PROFILE_ACTION), get(env, "project.base.version"),
                get(env, "spring.datasource.url"),
                get(env, "spring.datasource.username"),
                get(env, "spring.datasource.password"),
                get(env, "project.iaas.blos.domain"),
                get(env, "project.iaas.blos.default-path"),
                get(env, "spring.servlet.multipart.max-file-size"),
                get(env, "project.auth.clients[0].duration"),
                get(env, "project.auth.password-reset"),
                get(env, "project.base.time-zone")
        );

        String defaultPath = get(env, "project.iaas.blos.default-path");

        if (StrUtil.isBlank(defaultPath)) {
            log.error("【配置错误】你需要填写图片的默认存储地址 project.iaas.blos.default-path");
            throw new IllegalArgumentException("你需要填写图片的默认存储地址 project.iaas.blos.default-path");
        }

        if ("/".equals(defaultPath)) {
            log.error("【配置错误】图片的默认存储地址(project.iaas.blos.default-path)不能是根目录 \"/\"");
            throw new IllegalArgumentException("图片的默认存储地址(project.iaas.blos.default-path)不能是根目录 \"/\"");
        }
        Connection connection = null;
        try {
            log.warn("\n\n正在检查数据库连接\n\n" +
                    "\n[CHECK] =========================================================================================================================="
            );
            Setting setting = new Setting();
            setting.set("url", get(env, "spring.datasource.url"));
            setting.set("user", get(env, "spring.datasource.username"));
            setting.set("pass", get(env, "spring.datasource.password"));
            setting.set("driver", "com.mysql.cj.jdbc.Driver");
            DataSource ds = HikariDSFactory.create(setting).getDataSource();
            connection = ds.getConnection();
            log.warn("数据库链接检查完毕" +
                    "\n[CHECK] =========================================================================================================================="
            );
        } catch (Exception t) {
            if (t.getMessage().contains("Access denied for user") && t.getMessage().contains("(using password: YES)")) {
                log.error("{}数据库链接失败, 可能是您的数据库用户名或密码错误, 请检查配置项 [spring.datasource.username] 与 [spring.datasource.password]{}",
                        ERROR_SEPARATOR,
                        ERROR_SEPARATOR);
            } else if (t instanceof HikariPool.PoolInitializationException) {
                /*
                 数据库不允许远程范文
                 */
                if (isNotAllowedConnect(t.getLocalizedMessage())) {
                    log.error("{}{}\n{}\n已正确连接数据库, 但数据库不允许访问, 请检查数据库远程连接配置{}",
                            ERROR_SEPARATOR,
                            t.getLocalizedMessage(),
                            SEPARATOR2,
                            ERROR_SEPARATOR);
                } else if (isNotReceived(t.getLocalizedMessage())) {
                    String ip = getSqlIp(get(env, "spring.datasource.url"));
                    int port = getSqlPort(get(env, "spring.datasource.url"));
                    boolean b = NetUtil.ping(ip);
                    if (b) {
                        InetSocketAddress address = InetSocketAddress.createUnresolved(ip, port);
                        boolean telnet = NetUtil.isOpen(address, 3000);
                        if (!telnet) {
                            log.error("{}{}\n{}\n无法连接到数据库, 即将进一步确认...\n无法连接到数据库IP端口 [{}:{}]。{}",
                                    ERROR_SEPARATOR,
                                    t.getLocalizedMessage(),
                                    SEPARATOR2,
                                    ip, port,
                                    ERROR_SEPARATOR);
                        }
                    } else {
                        log.error("{}{}\n{}\n无法连接到数据库, 即将进一步确认...\n无法连接到目标IP [{}], 发送 ping 命令失败。{}",
                                ERROR_SEPARATOR,
                                t.getLocalizedMessage(),
                                SEPARATOR2,
                                ip,
                                ERROR_SEPARATOR);
                    }
                }
            } else {

            }
            System.exit(1);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

    private String get(ConfigurableEnvironment env, String prop) {
        return env.getProperty(prop);
    }

    private String getSqlIp(String url) {
        url = url.substring(13);
        return url.substring(0, url.indexOf(':'));
    }


    private int getSqlPort(String url) {
        url = url.substring(13);
        String port = url.substring(url.indexOf(':') + 1, url.indexOf('/'));
        try {
            return Integer.parseInt(port);
        } catch (NumberFormatException e) {
            log.error("数据库端口错误:{}", port);
            throw e;
        }
    }

    public boolean isNotAllowedConnect(String message) {
        return message.contains("message from server:") && message.contains("is not allowed to connect to this MySQL server");
    }

    public boolean isNotReceived(String message) {
        return message.contains("The driver has not received any packets from the server");
    }

    private void setTimeZone(ConfigurableEnvironment env) {
        String timeZone = get(env, "project.base.time-zone");
        if (StrUtil.isBlank(timeZone) || TimeZone.getTimeZone(timeZone) == null) {
            TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        } else {
            TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
        }


    }
}
