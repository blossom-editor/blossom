//package com.blossom.backend.base.auth.redis;
//
//import cn.hutool.core.util.StrUtil;
//import com.blossom.backend.base.auth.TokenUtil;
//import com.blossom.backend.base.auth.pojo.AccessToken;
//import com.blossom.backend.base.auth.repo.TokenRepository;
//import com.blossom.common.base.exception.XzException500;
//import com.blossom.common.base.util.json.JsonUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.TimeUnit;
//
///**
// * redis token 存储
// *
// * @author xzzz
// */
//@Slf4j
//@Component
//@ConditionalOnClass(RedisTemplate.class)
//@ConditionalOnProperty(value = "project.auth.type", havingValue = "redis")
//public class RedisTokenRepository implements TokenRepository {
//
//    @Autowired
//    private StringRedisTemplate redisTemplate;
//
//    @Override
//    public void saveToken(AccessToken accessToken) {
//        if (accessToken == null || StrUtil.isBlank(accessToken.getToken())) {
//            throw new XzException500("无法保存空的AccessToken");
//        }
//        redisTemplate.opsForValue().set(
//                TokenUtil.buildTokenKey(accessToken.getToken()),
//                JsonUtil.toJson(accessToken),
//                accessToken.getDuration(),
//                TimeUnit.SECONDS);
//    }
//
//    @Override
//    public AccessToken getToken(String token) {
//        if (StrUtil.isBlank(token)) {
//            return null;
//        }
//        return JsonUtil.toObj(redisTemplate.opsForValue().get(TokenUtil.buildTokenKey(token)), AccessToken.class);
//    }
//
//    @Async
//    @Override
//    public void remove(String token) {
//        log.info("异步删除 token");
//        redisTemplate.delete(TokenUtil.buildTokenKey(token));
//    }
//
//    @Override
//    public void saveUniqueToken(AccessToken accessToken) {
//        if (accessToken == null || StrUtil.isBlank(accessToken.getToken())) {
//            throw new XzException500("无法保存空的AccessToken");
//        }
//
//        if (accessToken.getMultiPlaceLogin()) {
//            return;
//        }
//
//        redisTemplate.opsForValue().set(
//                TokenUtil.buildUniqueTokenKey(String.valueOf(accessToken.getUserId())),
//                accessToken.getToken(),
//                accessToken.getDuration(),
//                TimeUnit.SECONDS);
//    }
//
//    @Override
//    public String getUniqueToken(String userId) {
//        if (StrUtil.isBlank(userId)) {
//            return null;
//        }
//        return redisTemplate.opsForValue().get(TokenUtil.buildUniqueTokenKey(userId));
//    }
//}
