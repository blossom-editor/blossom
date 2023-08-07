package com.blossom.expand.tracker.core.collector;

import cn.hutool.core.collection.CollUtil;
import com.blossom.expand.tracker.core.SpanNode;
import com.blossom.expand.tracker.core.TrackerProperties;
import com.blossom.expand.tracker.core.common.TrackerConstants;
import com.blossom.expand.tracker.core.repository.TrackerRepository;
import com.blossom.common.base.util.spring.SpringUtil;
import com.blossom.common.base.util.thread.NamedThreadFactory;
import com.blossom.expand.tracker.core.Tracker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * tracker 开启 trace 信息收集, 该方法只用来收集本应用的 trace 信息, 具体信息保存在何处, 由
 * {@link TrackerRepository} 的实现类来实现
 *
 * @author xzzz
 */
@Slf4j
public class TrackerLocalCacheCollector implements TrackerCollector {

    /**
     * Tracker Span 收集器配置文件
     */
    private final TrackerProperties properties;

    /**
     * 本地缓存, 每次发送将一次性将缓存内全部发送到服务端, 并将缓存清空, 内存大小会受到
     * {@link TrackerProperties#getCollector()#localSpanCache} 配置项的控制
     */
    private final List<SpanNode> localSpanCache;

    /**
     * 发送 Span 集合追踪的线程池
     */
    private final ScheduledExecutorService spanSendExecutor = new ScheduledThreadPoolExecutor(
            1, new NamedThreadFactory("span-send-task", true));

    /**
     * 数据收集线程池
     */
    private final ExecutorService spanCollectWorker;

    /**
     * 在发送时将禁止数据写入缓存
     */
    private final ReentrantLock LOCK = new ReentrantLock();

    /**
     * 应用名称
     */
    private String APP_NAME;

    /**
     * 写入缓存的超时时间
     */
    private final long LOCAL_CACHE_WRITE_OVERTIME = 2000;

    /**
     * tracker span 持久化方法
     */
    @Autowired(required = false)
    private TrackerRepository trackerRepository;

    public TrackerLocalCacheCollector(TrackerProperties properties) {
        log.info("[TRACKERS] 开启 Span 收集器, 采样率[{}/1000], 本地缓存数[{}]",
                properties.getCollector().getRate(), properties.getCollector().getMaxCache());
        this.properties = properties;
        this.localSpanCache = new ArrayList<>(properties.getCollector().getMaxCache());

        int cores = Runtime.getRuntime().availableProcessors();
        // 数据收集线程池，线程池满时，会直接丢弃新任务
        spanCollectWorker = new ThreadPoolExecutor(
                cores, cores,
                10,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2048),
                new NamedThreadFactory("tracker-collect-worker"),
                new ThreadPoolExecutor.DiscardPolicy());
    }

    /**
     * 异步收集数据到本地缓存, 如果此时正在存储中, 则异步线程阻塞, 超时后 (默认5s) 放弃该条追踪数据。异步线程保存并不会阻塞业务线程,
     * 所以 span 信息的收集对性能影响很小。
     * <p>
     * 一次 span 结束时 {@link Tracker#end()} 会收集该 span 信息, 例如一次 trace 有 10 个 span 节点, 则会进入
     * 该方法 10 次, span 信息会交由 {@link TrackerLocalCacheCollector#spanCollectWorker} 线程池发送到本地缓存
     * {@link TrackerLocalCacheCollector#localSpanCache} 中, 并由 {@link TrackerLocalCacheCollector#spanSendExecutor}
     * 定时任务将缓存中的数据发送至 {@link TrackerRepository} trace 存储对象中。发送时本次缓存是被锁定的且无法写入的。
     * 发送完成后会清空本地缓存。此时收集器才能再次写入。
     *
     * @param spanNode span 信息
     */
    @Override
    public void collect(SpanNode spanNode) {
        if (Math.abs(spanNode.getTraceId().hashCode() % TrackerConstants.COLLECTOR_MAX_RATE) >= properties.getCollector().getRate()) {
            return;
        }

        if (ignore(spanNode)) {
            return;
        }

        spanNode.setAppName(APP_NAME);

        spanCollectWorker.submit(() -> {
            try {
                if (localSpanCache.size() > properties.getCollector().getMaxCache()) {
                    return;
                }
                if (LOCK.tryLock(LOCAL_CACHE_WRITE_OVERTIME, TimeUnit.MILLISECONDS)) {
                    try {
                        localSpanCache.add(spanNode);
                    } finally {
                        LOCK.unlock();
                    }
                } else {
                    log.warn("收集本地 span 失败, 本地缓存写入超时");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @PostConstruct
    public void init() {
        this.APP_NAME = SpringUtil.getAppName();
        // 每个任务会等上个任务完成后 delay 秒后执行
        spanSendExecutor.scheduleWithFixedDelay(() -> {
            if (localSpanCache.size() == 0) {
                return;
            }
            log.debug("即将发送本地SPAN日志({}条)", localSpanCache.size());
            try {
                if (LOCK.tryLock(5000, TimeUnit.MILLISECONDS)) {
                    try {
                        if (trackerRepository.save(localSpanCache)) {
                            localSpanCache.clear();
                        }
                    } catch (Throwable t) {
                        log.error("[TRACKERS] 发送本地SPAN日志({}条)失败, 异常信息: {}", localSpanCache.size(), t.getMessage());
                    } finally {
                        LOCK.unlock();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 10000, 5000, TimeUnit.MILLISECONDS);
    }

    @Override
    public List<SpanNode> get() {
        return localSpanCache;
    }

    /**
     * 不收集的 span 信息
     *
     * @param spanNode
     * @return
     */
    private boolean ignore(SpanNode spanNode) {
        if (CollUtil.isEmpty(properties.getCollector().getIgnoreTrackers())) {
            return false;
        }

        for (TrackerProperties.IgnoreTracker ignoreTracker : properties.getCollector().getIgnoreTrackers()) {

            if (spanNode.getSpanName().equals(ignoreTracker.getSpanName())) {
                // 忽略全部该名称的 span
                if (!ignoreTracker.getRootSpan()) {
                    return true;
                }
                // 忽略全部该名称的 root_span
                else if (ignoreTracker.getRootSpan() & spanNode.getSpanParentId().equals(TrackerConstants.ROOT_SPAN)) {
                    return true;
                }
            }

            if (spanNode.getSpanType().equals(ignoreTracker.getSpanType())) {
                if (!ignoreTracker.getRootSpan()) {
                    return true;
                } else if (ignoreTracker.getRootSpan() & spanNode.getSpanParentId().equals(TrackerConstants.ROOT_SPAN)) {
                    return true;
                }
            }
        }
        return false;
    }
}
