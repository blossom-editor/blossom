package com.blossom.backend.base.auth;

import java.util.ArrayList;
import java.util.List;

/**
 * 授权模块静态参数
 *
 * @author xzzz
 * @since 0.0.1
 */
public class AuthConstant {

    /**
     * token 所在的请求头名称
     */
    public static final String HEADER_AUTHORIZATION = "Authorization";

    /**
     * token 前缀, 遵循 Oauth2.0 规范 (RFC6750: https://tools.ietf.org/html/rfc6750)
     * <p>
     * 参考文档:
     * https://tools.ietf.org/html/RFC6750
     * https://learning.postman.com/docs/sending-requests/authorization/
     */
    public static final String HEADER_TOKEN_PREFIX = "Bearer ";

    /**
     * Client 配置不踢出其他前一个 Token 时, token_unique 中记录的值
     */
    public static final String UNIQUE_TOKEN_EVERY_WHERE = "Client允许多处登录";

    /**
     * 标识请求时白名单, 为后续过滤器判断使用
     */
    public static final String WHITE_LIST_ATTRIBUTE_KEY = "IS_WHITE_LIST";

    /**
     * 默认忽略的请求
     */
    public static final List<String> DEFAULT_WHITE_LIST = new ArrayList<String>() {
        private static final long serialVersionUID = -1;
        {
            // 登录接口
            this.add("/login");
            // 获取图片验证码接口
            this.add("/captcha/image");
            // 获取手机验证码接口
            this.add("/captcha/phone");
            // 一些默认的本地静态资源, 一些非前后端分离的静态文件, 如某些框架自带的操作界面(如 swagger)等
            this.add("/favicon.ico");
            this.add("/**/**.js");
            this.add("/**/**.css");
            // swagger 默认请求路径
            this.add("/doc.html");
            this.add("/webjars/**");
            this.add("/swagger-resources");
            this.add("/v2/**");
        }
    };

    /**
     * 请求体包装过滤器的顺序
     */
    public static final int AUTH_FILTER_WRAPPER_ORDER = -101;
    /**
     * 代理过滤器的顺序
     */
    public static final int AUTH_FILTER_PROXY = -100;
}
