package com.blossom.backend.server.article.draft;

import com.blossom.backend.server.doc.DocService;
import com.blossom.backend.server.folder.FolderTypeEnum;
import com.blossom.common.base.exception.XzException500;
import com.blossom.common.cache.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class ImportManager {

    @Autowired
    private DocService docService;

    private final ReentrantLock LOCK = new ReentrantLock();

    @Resource
    private CacheService<String, AtomicInteger> cacheService;

    /**
     * 并发导入时的排序获取
     */
    public Integer getSort(String batchId, Long pid, Long userId) {
        AtomicInteger sort = cacheService.get(batchId, AtomicInteger.class).orElse(null);
        if (null == sort) {
            try {
                LOCK.tryLock(1000, TimeUnit.MILLISECONDS);
                sort = cacheService.get(batchId, AtomicInteger.class).orElse(null);
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
        cacheService.put(batchId, i, 120 * 60L);
        return i;
    }
}
