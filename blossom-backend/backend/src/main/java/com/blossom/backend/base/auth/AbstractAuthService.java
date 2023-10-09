package com.blossom.backend.base.auth;

import com.blossom.backend.base.auth.exception.AuthException;
import com.blossom.backend.base.auth.exception.AuthRCode;
import com.blossom.backend.base.auth.pojo.AccessToken;
import com.blossom.backend.base.auth.pojo.LoginDTO;
import com.blossom.backend.base.auth.pojo.LoginEvent;
import com.blossom.backend.base.auth.pojo.LoginReq;
import com.blossom.backend.base.auth.repo.TokenRepository;
import com.blossom.backend.base.auth.security.PasswordEncoder;
import com.blossom.backend.base.auth.token.TokenEncoder;
import com.blossom.backend.base.user.pojo.UserEntity;
import com.blossom.common.base.util.DateUtils;
import com.blossom.common.base.util.ServletUtil;
import com.blossom.common.base.util.json.JsonUtil;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.util.TreeMap;

/**
 * 授权处理抽象类
 *
 * @author xzzz
 */
public abstract class AbstractAuthService {
    protected AuthProperties properties;
    protected TokenRepository tokenRepository;
    protected TokenEncoder tokenEncoder;
    protected PasswordEncoder passwordEncoder;
    private ApplicationContext applicationContext;

    public AbstractAuthService(AuthProperties properties,
                               TokenRepository tokenRepository,
                               TokenEncoder tokenEncoder,
                               PasswordEncoder passwordEncoder,
                               ApplicationContext applicationContext) {
        this.properties = properties;
        this.tokenRepository = tokenRepository;
        this.tokenEncoder = tokenEncoder;
        this.passwordEncoder = passwordEncoder;
        this.applicationContext = applicationContext;
    }

    /**
     * 登录
     *
     * @param request 请求
     * @param req     登录请求参数
     * @return 令牌对象
     */
    public AccessToken login(HttpServletRequest request, LoginReq req) {
        LoginDTO login = req.to(LoginDTO.class);
        AccessToken accessToken = req.to(AccessToken.class);
        accessToken.setMetadata(new TreeMap<>());
        loginByPassword(accessToken, login);
        fillConfig(accessToken);
        genToken(accessToken);
        saveToken(accessToken);
        publishEvent(request, accessToken, login);
        return accessToken;
    }

    /**
     * 为AccessToken填充配置信息, 如过期日期等
     */
    private void fillConfig(AccessToken accessToken) {
        AuthProperties.Client client = properties.getClientMap().get(accessToken.getClientId());
        if (client == null) {
            throw new AuthException(AuthRCode.INVALID_CLIENT_ID);
        }
        accessToken.setDuration(client.getDuration());
        accessToken.setExpire(System.currentTimeMillis() + (accessToken.getDuration() * 1000));
        accessToken.setRequestRefresh(client.getRequestRefresh());
        accessToken.setMultiPlaceLogin(client.getMultiPlaceLogin());
        accessToken.setLoginTime(DateUtils.now());
    }

    /**
     * 登录时生成TOKEN令牌
     */
    private void genToken(AccessToken accessToken) {
        accessToken.setToken(tokenEncoder.encode(accessToken));
    }

    /**
     * 保存 accessToken 到存储介质中
     */
    private void saveToken(AccessToken accessToken) {
        tokenRepository.saveToken(accessToken);
        tokenRepository.saveUniqueToken(accessToken);
    }

    /**
     * 发布登录事件
     *
     * @param request     request
     * @param accessToken token
     * @param login       登录信息
     */
    private void publishEvent(HttpServletRequest request, AccessToken accessToken, LoginDTO login) {
        String ip = ServletUtil.getIP(request);
        String userAgent = ServletUtil.getUserAgent(request);
        login.setUserId(accessToken.getUserId());
        login.setIp(ip);
        login.setUserAgent(userAgent);
        login.setLoginTime(accessToken.getLoginTime());
        login.setAccessTokenSnapshot(JsonUtil.toJson(accessToken));
        applicationContext.publishEvent(new LoginEvent(login));
    }

    /**
     * 根据用户名密码登录
     *
     * @param accessToken token
     * @param req         请求参数, 包含用户名和密码
     */
    protected abstract void loginByPassword(AccessToken accessToken, LoginDTO req);

    /**
     * 填充 metadata 内容
     *
     * @param accessToken token
     * @param user        用户信息
     */
    protected void fillUserDetail(AccessToken accessToken, UserEntity user) {
        if (user == null) {
            throw new AuthException(AuthRCode.USER_NOT_EXIST);
        }
        accessToken.setUserId(user.getId());
        accessToken.getMetadata().put("userId", String.valueOf(user.getId()));
        accessToken.getMetadata().put("username", user.getUsername());
        accessToken.getMetadata().put("type", user.getType().toString());
    }
}
