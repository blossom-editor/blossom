package com.blossom.backend.base.auth.repo;

import com.blossom.backend.base.auth.pojo.AccessToken;

/**
 * token 存储
 *
 * @author xzzz
 */
public interface TokenRepository {

    /**
     * 保存 token
     *
     * @param accessToken token 对象信息
     */
    void saveToken(AccessToken accessToken);

    /**
     * 获取 token 对象信息
     *
     * @param token token 令牌
     * @return token
     */
    AccessToken getToken(String token);

    /**
     * 删除 Token 信息
     *
     * @param token token 令牌
     */
    void remove(String token);

    /**
     * 删除某个用户的所有 token
     *
     * @param userId 用户ID
     */
    void removeAll(Long userId);

    /**
     * 保存唯一生效的 token 对象
     *
     * @param accessToken 唯一生效的 token 信息
     */
    void saveUniqueToken(AccessToken accessToken);

    /**
     * 获取唯一生效的 token 令牌
     *
     * @param userId 用户ID, 注意, 为了保证兼容性, 用户ID为 String 类型, 如果是使用其他类型的用户的ID, 注意转换为 String 类型进行存储
     * @return 该用户有效的 token
     */
    String getUniqueToken(String userId);

}
