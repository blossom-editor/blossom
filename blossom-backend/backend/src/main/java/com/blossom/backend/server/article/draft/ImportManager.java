package com.blossom.backend.server.article.draft;

import com.blossom.backend.server.doc.DocService;
import com.blossom.backend.server.folder.FolderTypeEnum;
import com.blossom.common.base.exception.XzException500;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@AllArgsConstructor
public class ImportManager {

    private final DocService docService;
    private final ReentrantLock LOCK = new ReentrantLock();

    /**
     * 批量导入时的批次缓存, 一个批次的导入只会从数据库获取一次排序, 后续排序从缓存中递增
     */
    private final Cache<String, AtomicInteger> batchCache = Caffeine.newBuilder()
            .initialCapacity(50)
            .expireAfterWrite(120, TimeUnit.MINUTES)
            .removalListener((String location, AtomicInteger i, RemovalCause cause) ->
                    log.info("batch import [" + location + "] has been deleted")
            )
            .build();

    /**
     * 并发导入时的排序获取
     */
    public Integer getSort(String batchId, Long pid, Long userId) {
        AtomicInteger sort = batchCache.getIfPresent(batchId);
        if (null == sort) {
            try {
                LOCK.tryLock(1000, TimeUnit.MILLISECONDS);
                sort = batchCache.getIfPresent(batchId);
                if (null == sort) {
                    sort = initBatchCount(batchId, pid, userId);
                }
            } catch (InterruptedException e) {
                LOCK.unlock();
            }
        }
        if (sort == null) {
            throw new XzException500("导入失败");
        }
        return sort.getAndIncrement();
    }

    private AtomicInteger initBatchCount(String batchId, Long pid, Long userId) {
        System.out.println("初始化导入排序");
        AtomicInteger i = new AtomicInteger(docService.selectMaxSortByPid(pid, userId, FolderTypeEnum.ARTICLE) + 1);
        batchCache.put(batchId, i);
        return i;
    }
}
