package com.blossom.common.base.util;


import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 主键工具类
 *      使用前提：IP 地址的，前两个网段重复，后两个网段不能重复。
 *      可用时间：139 年
 *      可用机器数：128 * 254 = 32512
 *      每秒产生的 Id 数量：64000
 *
 * @author xzzz
 * @since 0.0.1
 */
@Slf4j
public class PrimaryKeyUtil {

    /**
     * 起始的时间戳  2021-04-15 00:00:00
     */
    private final static long START_STAMP = 1618416000000L;

    /**
     * 起始的时间戳 2021-04-01 00:00:00
     */
    // private final static long START_STAMP = 1617206400000L;

    /**
     * 每一部分占用的位数
     * <p>
     * SEQUENCE_BIT：序列号占用的位数
     * DATACENTER_BIT：数据中心占用的位数
     * MACHINE_BIT：机器标识占用的位数
     */
    private final static long SEQUENCE_BIT = 6;
    private final static long DATACENTER_BIT = 8;
    private final static long MACHINE_BIT = 8;

    /**
     * 每一部分的最大值
     */
    private final static long MAX_DATACENTER_NUM = ~(-1L << DATACENTER_BIT);
    private final static long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTAMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    /**
     * 数据中心 ID
     */
    private static long datacenterId;
    /**
     * 机器标识 ID
     */
    private static long machineId;
    /**
     * 序列号
     */
    private static long sequence = 0L;
    /**
     * 上一次时间戳
     */
    private static long lastStamp = -1L;

    static {
        try {
            String ipAddr = InetAddress.getLocalHost().getHostAddress();
            String[] ipAddrArr = ipAddr.split("\\.");
            datacenterId = Long.parseLong(ipAddrArr[2]);
            machineId = Long.parseLong(ipAddrArr[3]);

            if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
                throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
            }
            if (machineId > MAX_MACHINE_NUM || machineId < 0) {
                throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
            }
        } catch (UnknownHostException e) {
            log.warn("生成唯一 Id 时，获取机器 Ip 异常");
            datacenterId = 254L;
            machineId = 254L;
        }
    }

    /**
     * 产生下一个ID
     *
     * @return long
     */
    public static synchronized long nextId() {
        long currStamp = getNewStamp();
        if (currStamp < lastStamp) {
            throw new RuntimeException("时钟事件出现倒退，拒绝生成 ID");
        }

        if (currStamp == lastStamp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currStamp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = 0L;
        }

        lastStamp = currStamp;

        // 时间戳部分
        return (currStamp - START_STAMP) << TIMESTAMP_LEFT
                // 数据中心部分
                | datacenterId << DATACENTER_LEFT
                // 机器标识部分
                | machineId << MACHINE_LEFT
                // 序列号部分
                | sequence;
    }

    private static long getNextMill() {
        long mill = getNewStamp();
        while (mill <= lastStamp) {
            mill = getNewStamp();
        }
        return mill;
    }

    private static long getNewStamp() {
        return System.currentTimeMillis();
    }

    private static int getHostId(String s, int max) {
        byte[] bytes = s.getBytes();
        int sums = 0;
        for (int b : bytes) {
            sums += b;
        }
        return sums % (max + 1);
    }

}
