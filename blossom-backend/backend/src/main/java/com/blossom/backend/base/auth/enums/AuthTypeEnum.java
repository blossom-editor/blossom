package com.blossom.backend.base.auth.enums;

/**
 * 授权类型
 *
 * @author xzzz
 */
public enum AuthTypeEnum {
    /**
     * 有状态的授权方式, Token 按自定义逻辑生成, 并存储在 Redis 中, 该种授权可以退出和主动删除
     */
    redis,

    /**
     * 有状态的授权方式, Token 按自定义逻辑生成, 并存储在 Caffeine 中, 该种授权可以退出和主动删除, 但目前仅支持一种 clientId
     */
    caffeine,

    /**
     * 无状态的授权方式, Token 使用 JWT
     */
    jwt
}
