package com.blossom.backend.base.auth;


import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.StrUtil;
import com.blossom.backend.base.auth.annotation.AuthIgnore;
import com.blossom.backend.base.auth.enums.AuthTypeEnum;
import com.blossom.backend.base.auth.enums.GrantTypeEnum;
import com.blossom.backend.base.auth.enums.LogTypeEnum;
import com.blossom.backend.base.auth.enums.PasswordEncoderEnum;
import com.blossom.common.base.exception.XzException500;
import com.blossom.common.base.util.spring.SpringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 授权配置项
 *
 * @author xzzz
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "project.auth")
public class AuthProperties implements EnvironmentAware, ApplicationContextAware {

    /**
     * 是否启用授权功能
     */
    private Boolean enabled = Boolean.TRUE;
    /**
     * 使用JWT
     */
    private AuthTypeEnum type = AuthTypeEnum.jwt;
    /**
     * 默认密码
     */
    private String defaultPassword = "123456";
    /**
     * 重置密码
     */
    private Boolean passwordReset = Boolean.FALSE;
    /**
     * 日志类型
     */
    private LogTypeEnum logType = LogTypeEnum.none;
    /**
     * 默认加密方式
     */
    private PasswordEncoderEnum passwordEncoder = PasswordEncoderEnum.bcrypt;
    /**
     * 授权平台配置,不同的平台类型生成的 token 时效是不同的
     * <p>
     * 该类创建完成后会检查是否配置平台数据, 未自定义平台配置会创建默认配置
     *
     * @see AuthProperties#initAfterProcessorClient()
     */
    private List<Client> clients;
    /**
     * platforms 转为 map, 方便查询
     */
    private Map<String, Client> clientMap;
    /**
     * 白名单列表
     * 配置时不需要增加context-path,会自动拼接
     */
    private List<String> whiteList;

    @PostConstruct
    public void init() {
        // 检查配置的授权类型是否正确
        initAfterProcessorTypeCheck();
        // 白名单后置处理
        initAfterProcessorWhiteList();
        // 平台配置后置处理
        initAfterProcessorClient();
    }

    /**
     * 白名单后置处理
     */
    private void initAfterProcessorWhiteList() {
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> methodMap = mapping.getHandlerMethods();
        methodMap.forEach((key, value) -> {
            if (null != AnnotationUtil.getAnnotation(value.getMethod(), AuthIgnore.class)) {
                if (key.getPatternsCondition() != null) {
                    Set<String> urls = key.getPatternsCondition().getPatterns();
                    whiteList.addAll(urls);
                }
            }
        });

        String contentPath = env.getProperty(SpringUtil.SERVLET_CONTEXT_PATH);
        // 设置默认登陆页白名单
        if (CollUtil.isEmpty(whiteList)) {
            this.whiteList = new ArrayList<>();
        }
        whiteList.addAll(AuthConstant.DEFAULT_WHITE_LIST);
        this.whiteList = this.whiteList.stream()
                .map(whiteUrl -> Optional.ofNullable(contentPath).orElse("") + whiteUrl)
                .collect(Collectors.toList());
    }

    /**
     * 平台配置后置处理,设置默认平台类型
     */
    private void initAfterProcessorClient() {
        // 初始化客户端列表
        if (CollUtil.isEmpty(this.clients)) {
            this.clientMap = new HashMap<>(1);
            this.clients = new ArrayList<>(1);
        }

        final Client defaultClient = Client.getDefault();

        log.info("[AUTHORIZ] 授权类型:{}, Client:客户端, Duration(h):授权时长(小时), Refresh:请求刷新授权, MultiPlace:允许多处登录", type);
        log.info("[AUTHORIZ] ┌──────────┬──────────┬─────────┬────────────┐");
        log.info("[AUTHORIZ] | ClientId | Duration | Refresh | MultiPlace |");
        log.info("[AUTHORIZ] ├──────────┼──────────┼─────────┼────────────┤");
        for (Client client : this.clients) {
            // 配置文件有配置,但配置不全,则未配置的参数使用默认配置
            if (client.getDuration() == null || client.getDuration() == 0) {
                client.setDuration(defaultClient.getDuration());
            }
            if (client.getRequestRefresh() == null) {
                client.setRequestRefresh(defaultClient.getRequestRefresh());
            }
            if (client.getMultiPlaceLogin() == null) {
                client.setMultiPlaceLogin(defaultClient.getMultiPlaceLogin());
            }
            if (CollUtil.isEmpty(client.getGrantType())) {
                client.setGrantType(defaultClient.getGrantType());
            }
            if (AuthTypeEnum.jwt.equals(type)) {
                client.setRequestRefresh(false);
                client.setMultiPlaceLogin(true);
            }
            log.info("[AUTHORIZ] | {}| {}| {}| {}| {}",
                    StrUtil.fillAfter(client.getClientId(), StrUtil.C_SPACE, 9),
                    StrUtil.fillAfter(client.getDuration() / 3600L + " Hour", StrUtil.C_SPACE, 9),
                    StrUtil.fillAfter(String.valueOf(client.getRequestRefresh()), StrUtil.C_SPACE, 8),
                    StrUtil.fillAfter(String.valueOf(client.getMultiPlaceLogin()), StrUtil.C_SPACE, 11),
                    client.getGrantType()
            );
        }
        // 转换为 map, 有相同的 type, 后者会覆盖前者
        this.clientMap = this.clients.stream().collect(Collectors.toMap(Client::getClientId, Function.identity(), (key1, key2) -> key2));
        log.info("[AUTHORIZ] └──────────┴──────────┴─────────┴────────────┘");
    }

    private void initAfterProcessorTypeCheck() {
        if (type.equals(AuthTypeEnum.redis) && !ClassLoaderUtil.isPresent("org.springframework.data.redis.core.RedisTemplate")) {
            String msg = "授权方式 [project.auth.type = redis] 配置错误, 当前项目未使用 Redis, 无法使用 Redis 授权方式.";
            log.error(msg);
            throw new XzException500(msg);
        }
    }

    /**
     * 授权客户端的配置
     */
    public static class Client {
        /**
         * 编码
         */
        private String clientId;
        /**
         * 默认过期时间, 单位为秒
         */
        private Integer duration;
        /**
         * 每次请求刷新
         */
        private Boolean requestRefresh;
        /**
         * 登录时是否踢出上一个登录的 token，注意，该配置修改后，并不会改变在之前下发的 Token 的校验逻辑
         * <p>为 true  则每次登录 token 会替换前一个 token。
         * <p>为 false 则每次登录返回的 token 是一样的。
         */
        private Boolean multiPlaceLogin;
        /**
         * 该平台允许的登录方式
         */
        private List<String> grantType;

        /**
         * 获取默认平台配置
         *
         * @return 平台配置
         * @see AuthProperties#initAfterProcessorClient
         */
        public static Client getDefault() {
            Client client = new Client();
            client.setDuration(60 * 60 * 4);
            client.setRequestRefresh(Boolean.TRUE);
            client.setMultiPlaceLogin(Boolean.TRUE);
            client.setGrantType(CollUtil.newArrayList(GrantTypeEnum.PASSWORD.getType()));
            return client;
        }

        public Client() {
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public Integer getDuration() {
            return duration;
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
        }

        public Boolean getRequestRefresh() {
            return requestRefresh;
        }

        public void setRequestRefresh(Boolean requestRefresh) {
            this.requestRefresh = requestRefresh;
        }

        public Boolean getMultiPlaceLogin() {
            return multiPlaceLogin;
        }

        public void setMultiPlaceLogin(Boolean multiPlaceLogin) {
            this.multiPlaceLogin = multiPlaceLogin;
        }

        public List<String> getGrantType() {
            return grantType;
        }

        public void setGrantType(List<String> grantType) {
            this.grantType = grantType;
        }
    }

    /**
     * 环境配置
     */
    private Environment env;

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
