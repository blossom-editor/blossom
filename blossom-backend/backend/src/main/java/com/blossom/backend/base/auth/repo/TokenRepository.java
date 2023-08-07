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
     * 保存唯一生效的 token 对象
     *
     * @param accessToken 唯一生效的 token 信息
     */
    void saveUniqueToken(AccessToken accessToken);

    /**
     * 获取唯一生效的 token 令牌
     *
     * @param userId 用户ID
     * @return 该用户有效的 token
     */
    String getUniqueToken(String userId);

}
