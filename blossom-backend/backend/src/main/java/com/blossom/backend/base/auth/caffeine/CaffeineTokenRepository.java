package com.blossom.backend.base.auth.caffeine;

import cn.hutool.core.util.StrUtil;
import com.blossom.backend.base.auth.AuthProperties;
import com.blossom.backend.base.auth.pojo.AccessToken;
import com.blossom.backend.base.auth.repo.TokenRepository;
import com.blossom.common.base.exception.XzException500;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * redis token 存储
 *
 * @author xzzz
 * @since 1.3.0
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "project.auth.type", havingValue = "caffeine")
public class CaffeineTokenRepository implements TokenRepository {

    private final Cache<String, AccessToken> tokenCache;
    /**
     *
     */
    private final Cache<String, String> uniqueTokenCache;

    public CaffeineTokenRepository(AuthProperties props) {
        AuthProperties.Client client = props.getClientMap().get("blossom");
        if (client == null) {
            throw new XzException500("当授权方式为 [caffeine] 时, 仅支持一个客户端, 且 clientId 必须为 Blossom");
        }

        tokenCache = Caffeine.newBuilder()
                .initialCapacity(1000)
                .expireAfterWrite(client.getDuration(), TimeUnit.SECONDS)
                .removalListener((String key, AccessToken value, RemovalCause cause) ->
                        log.info("Token [" + key + "] has been deleted")
                )
                .build();

        uniqueTokenCache = Caffeine.newBuilder()
                .initialCapacity(1000)
                .expireAfterWrite(client.getDuration(), TimeUnit.SECONDS)
                .removalListener((String userId, String token, RemovalCause cause) ->
                        log.info("Unique Token(userId) [" + userId + "] has been deleted")
                )
                .build();

    }

    @Override
    public void saveToken(AccessToken accessToken) {
        if (accessToken == null || StrUtil.isBlank(accessToken.getToken())) {
            throw new XzException500("无法保存空的AccessToken");
        }
        tokenCache.put(accessToken.getToken(), accessToken);
    }

    @Override
    public AccessToken getToken(String token) {
        if (StrUtil.isBlank(token)) {
            return null;
        }
        return tokenCache.getIfPresent(token);
    }

    @Async
    @Override
    public void remove(String token) {
        log.info("异步删除 token");
        tokenCache.invalidate(token);
    }

    @Override
    public void removeAll(Long userId) {
        uniqueTokenCache.invalidate(userId);
        Map<String, AccessToken> maps = tokenCache.asMap();
        maps.forEach((k, t) -> {
            if (t.getUserId().equals(userId)) {
                tokenCache.invalidate(k);
            }
        });
    }

    @Override
    public void saveUniqueToken(AccessToken accessToken) {
        if (accessToken == null || StrUtil.isBlank(accessToken.getToken())) {
            throw new XzException500("无法保存空的AccessToken");
        }

        if (accessToken.getMultiPlaceLogin()) {
            return;
        }

        uniqueTokenCache.put(String.valueOf(accessToken.getUserId()), accessToken.getToken());
    }

    @Override
    public String getUniqueToken(String userId) {
        if (StrUtil.isBlank(userId)) {
            return null;
        }
        return uniqueTokenCache.getIfPresent(userId);
    }
}
