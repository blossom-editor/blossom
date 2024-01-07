package com.blossom.backend.base.search.queue;

import com.blossom.backend.base.search.message.IndexMsg;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 消息处理使用的阻塞队列
 *
 * @author Andecheal
 */
public class IndexMsgQueue {

    /**
     * 阻塞队列, 存放消息
     */
    private static final BlockingQueue<IndexMsg> indexMsgQueue = new ArrayBlockingQueue<>(2048);

    /**
     * 提交消息
     */
    public static void add(IndexMsg msg) throws InterruptedException {
        indexMsgQueue.add(msg);
    }

    /**
     * 提供一个阻塞式消息入口
     */
    public static void put(IndexMsg msg) throws InterruptedException {
        indexMsgQueue.put(msg);
    }

    /**
     * 获取消息
     */
    public static IndexMsg take() throws InterruptedException {
        return indexMsgQueue.take();
    }

}
