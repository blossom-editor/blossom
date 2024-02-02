package com.blossom.backend.base.auth.jwt;

import com.blossom.backend.base.auth.pojo.AccessToken;
import com.blossom.backend.base.auth.repo.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * JWT 存储, JWT 无状态无需存储
 *
 * @author xzzz
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "project.auth.type", havingValue = "jwt")
public class JWTTokenRepository implements TokenRepository {

    @Override
    public void saveToken(AccessToken accessToken) {

    }

    @Override
    public AccessToken getToken(String token) {
        return null;
    }

    @Override
    public void remove(String token) {

    }

    @Override
    public void removeAll(Long userId) {
    }

    @Override
    public void saveUniqueToken(AccessToken accessToken) {

    }

    @Override
    public String getUniqueToken(String userId) {
        return null;
    }
}
