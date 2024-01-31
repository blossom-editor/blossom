package com.blossom.backend.server.article.draft;

import cn.hutool.core.util.ObjUtil;
import com.blossom.common.base.exception.XzException400;
import com.blossom.common.base.util.security.SHA256Util;
import com.blossom.common.cache.caffeine.DynamicExpiry;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 文章临时访问
 *
 * @since 1.9.0
 */
@Slf4j
@Service
public class ArticleTempVisitService {

    /**
     * 存放文章ID的缓存
     *
     * @since 1.13.0 支持动态过期时间
     */
    private final Cache<String, TempVisit> tempVisitCache = Caffeine.newBuilder()
            .expireAfter(new DynamicExpiry())
            .initialCapacity(500)
            .removalListener((String key, TempVisit value, RemovalCause cause) ->
                    log.info("remove temp visit articleId [" + Objects.requireNonNull(value).getArticleId() + "]")
            )
            .build();

    /**
     * 生成一个缓存 key, key 并非文章的摘要码,
     *
     * @param articleId 文章ID
     * @param userId    用户ID
     * @param duration  过期时间, 单位分钟
     * @return 缓存 key
     */
    public String create(Long articleId, Long userId, Long duration) {
        XzException400.throwBy(ObjUtil.isNull(articleId), "文章ID为必填项");
        String key = SHA256Util.encode(UUID.randomUUID().toString());
        tempVisitCache.policy().expireVariably().ifPresent(e -> {
            e.put(key, new TempVisit(articleId, userId), duration, TimeUnit.MINUTES);
        });
        return key;
    }


    /**
     * 通过缓存 Key 获取ID
     *
     * @param key 缓存 key
     * @return 文章ID, 文章ID不存在时返回 null
     */
    public TempVisit get(String key) {
        return tempVisitCache.getIfPresent(key);
    }

    /**
     * 临时访问对象
     */
    @Data
    public static class TempVisit {
        /**
         * 文章ID
         */
        private Long articleId;
        /**
         * 用户ID
         */
        private Long userId;

        public TempVisit(Long articleId, Long userId) {
            this.articleId = articleId;
            this.userId = userId;
        }
    }
}
