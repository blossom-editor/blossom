package com.blossom.backend.base.auth.jwt;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.blossom.backend.base.auth.token.TokenEncoder;
import com.blossom.common.base.exception.XzException500;
import com.blossom.common.base.util.DateUtils;
import com.blossom.common.base.util.json.JsonUtil;
import com.blossom.common.base.util.spring.SpringUtil;
import com.blossom.backend.base.auth.pojo.AccessToken;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
     * 加密字符串
     */
    private final String secret = "T1h22WstOUaStOiUnAuCYif3Kw7DeLUciE8iWVAReqdnJTnJ7n4WYtE6x0UW";
    /**
     * 生成签名
     */
    private final Algorithm signer = Algorithm.HMAC256(secret);
    private Environment environment;
    private String appName;

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void init() {
        this.appName = environment.getProperty(SpringUtil.APP_NAME);
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
                    .sign(signer);
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
            JWTVerifier verifier = JWT.require(signer).build();
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
