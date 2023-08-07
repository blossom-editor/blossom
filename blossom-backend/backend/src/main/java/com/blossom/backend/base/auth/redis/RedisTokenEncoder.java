//package com.blossom.backend.base.auth.redis;
//
//import cn.hutool.core.lang.UUID;
//import com.blossom.backend.base.auth.pojo.AccessToken;
//import com.blossom.backend.base.auth.token.TokenEncoder;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
///**
// * token 编解码器, 简单生成一个 UUID token, 与 token 信息无加解密, 编解码关系
// *
// * @author xzzz
// */
//@Component
//@ConditionalOnClass(RedisTemplate.class)
//@ConditionalOnProperty(value = "project.auth.type", havingValue = "redis")
//public class RedisTokenEncoder implements TokenEncoder {
//
//    /**
//     * 生成一个 uuid 作为 token
//     *
//     * @param accessToken token
//     * @return uuid
//     */
//    @Override
//    public String encode(AccessToken accessToken) {
//        return UUID.randomUUID().toString(true);
//    }
//}
