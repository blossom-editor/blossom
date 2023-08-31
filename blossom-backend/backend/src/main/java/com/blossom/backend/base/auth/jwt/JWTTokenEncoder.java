package com.blossom.backend.base.auth.jwt;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.blossom.backend.base.auth.pojo.AccessToken;
import com.blossom.backend.base.auth.token.TokenEncoder;
import com.blossom.backend.base.param.ParamEnum;
import com.blossom.backend.base.param.ParamService;
import com.blossom.backend.base.param.pojo.ParamEntity;
import com.blossom.common.base.exception.XzException500;
import com.blossom.common.base.util.DateUtils;
import com.blossom.common.base.util.json.JsonUtil;
import com.blossom.common.base.util.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * jwt 在线解密解码：https://www.box3.cn/tools/jwt.html
 *
 * @author xzzz
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "project.auth.type", havingValue = "jwt")
public class JWTTokenEncoder implements TokenEncoder, EnvironmentAware {

    /**
     * 生成签名
     */
    private volatile Algorithm signer;
    private Environment environment;
    private String appName;
    private ParamService paramService;

    @Autowired
    public void setParamService(ParamService paramService) {
        this.paramService = paramService;
    }

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void init() {
        this.appName = environment.getProperty(SpringUtil.APP_NAME);
    }

    /**
     * 加密字符串配置在数据库 SERVER_JWT_SECRET 参数中.
     * <p>
     * 如果不配置加密字符串, 则默认生成一个, 若使用默认生成, 则每次重启后历史JWT会全部失效.
     */
    public Algorithm getSigner() {
        if (signer == null) {
            synchronized (JWTTokenEncoder.class) {
                if (signer == null) {
                    String secret;
                    ParamEntity param = paramService.getValue(ParamEnum.SERVER_JWT_SECRET);
                    if (param == null || StrUtil.isBlank(param.getParamValue())) {
                        secret = UUID.fastUUID().toString(true);
                    } else {
                        secret = param.getParamValue();
                    }
                    signer = Algorithm.HMAC256(secret);
                }
            }
        }
        return signer;
    }

    /**
     * token 对象转为 jwt
     *
     * @param accessToken token
     * @return jwt 字符串
     */
    @Override
    public String encode(AccessToken accessToken) {
        try {
            return JWT.create()
                    .withIssuer(appName)
                    // 主题，科目
                    .withSubject(accessToken.getClientId())
                    // 自定义字段
                    .withClaim("expire", accessToken.getExpire())
                    .withClaim("duration", accessToken.getDuration())
                    .withClaim("loginTime", accessToken.getLoginTime())
                    .withClaim("userId", accessToken.getUserId())
                    .withClaim("metadata", JsonUtil.toJson(accessToken.getMetadata()))
                    // 过期时间
                    .withExpiresAt(DateUtils.date(accessToken.getExpire()))
                    .sign(getSigner());
        } catch (Exception e) {
            log.error("授权发生异常：", e);
            throw new XzException500("JWT授权异常");
        }
    }

    /**
     * 解码 jwt
     *
     * @param token jwt
     * @return token 对象
     */
    @Override
    public AccessToken decode(String token) {
        try {
            JWTVerifier verifier = JWT.require(getSigner()).build();
            DecodedJWT jwt = verifier.verify(token);
            AccessToken accessToken = new AccessToken();
            accessToken.setToken(token);

            Long userId = jwt.getClaim("userId").asLong();
            if (userId == null || StrUtil.isBlank(jwt.getClaim("metadata").asString())) {
                return null;
            }

            accessToken.setExpire(jwt.getClaim("expire").asLong());
            accessToken.setDuration(jwt.getClaim("duration").asInt());
            accessToken.setLoginTime(jwt.getClaim("loginTime").asString());
            accessToken.setUserId(jwt.getClaim("userId").asLong());
            accessToken.setMetadata(JsonUtil.toMap(jwt.getClaim("metadata").asString()));
            return accessToken;
        } catch (SignatureVerificationException e) {
            log.error("token 解码时 Signature 错误");
            throw e;
        } catch (JWTDecodeException e) {
            log.error("token 解码错误");
            throw e;
        } catch (TokenExpiredException e) {
            log.error("token 解码超时");
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("token 解码参数错误");
            throw e;
        } catch (JWTVerificationException e) {
            log.error("token 解码失败");
            throw e;
        }
    }

}
