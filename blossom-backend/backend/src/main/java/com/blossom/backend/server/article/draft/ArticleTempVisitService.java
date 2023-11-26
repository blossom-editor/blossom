package com.blossom.backend.server.article.draft;

import cn.hutool.core.util.ObjUtil;
import com.blossom.common.base.exception.XzException400;
import com.blossom.common.base.util.security.SHA256Util;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
     */
    private final Cache<String, TempVisit> tempVisitCache = Caffeine.newBuilder()
            .initialCapacity(500)
            .expireAfterWrite(3, TimeUnit.HOURS)
            .removalListener((String key, TempVisit value, RemovalCause cause) ->
                    log.info("临时访问文章 [" + value.getArticleId() + "] 被删除")
            )
            .build();

    /**
     * 生成一个缓存 key, key 并非文章的摘要码
     *
     * @param articleId 文章ID
     * @return 缓存 key
     */
    public String create(Long articleId, Long userId) {
        XzException400.throwBy(ObjUtil.isNull(articleId), "文章ID为必填项");
        String key = SHA256Util.encode(UUID.randomUUID().toString());
        tempVisitCache.put(key, new TempVisit(articleId, userId));
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


//    public static void main(String[] args) throws InterruptedException {
//        ArticleTempService a = new ArticleTempService();
//        String key = a.create(20034L);
//        System.out.println(key);
//
//        Thread.sleep(2000);
//        Long v1 = a.get(key);
//        System.out.println(v1);
//
//        Thread.sleep(5000);
//        Long v2 = a.get(key);
//        System.out.println(v2);
//    }
}
