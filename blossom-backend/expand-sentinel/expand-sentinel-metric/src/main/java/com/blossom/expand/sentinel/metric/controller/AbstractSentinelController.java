package com.blossom.expand.sentinel.metric.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.StrUtil;
import com.blossom.common.base.exception.XzException400;
import com.blossom.common.base.util.DateUtils;
import com.blossom.common.base.util.SystemUtil;
import com.blossom.expand.sentinel.metric.pojo.OneLineMetric;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 抽象 sentinel 接口
 *
 * @author xzzz
 */
@Slf4j
public abstract class AbstractSentinelController {

    /**
     * 检查并获取时间, 间隔的优先级更高
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param interval  统计间隔
     * @return 返回String类型的日期数组，[开始时间，结束时间]
     */
    public String[] checkTime(String startTime, String endTime, String interval) {
        if (StrUtil.isBlank(interval)) {
            if (StrUtil.isBlank(startTime)) {
                throw new XzException400("未指定间隔(interval)时,开始日期为必填项");
            }
            if (StrUtil.isBlank(endTime)) {
                throw new XzException400("未指定间隔(interval)时,结束日期为必填项");
            }
        }
        return this.checkTime(startTime, endTime, interval, null);
    }

    /**
     * 检查并获取时间
     *
     * @param interval 统计间隔
     * @return 返回String类型的日期数组，[开始时间，结束时间]
     */
    public String[] checkTime(String interval) {
        if (StrUtil.isBlank(interval)) {
            throw new XzException400("统计间隔(interval)为必填项");
        }
        return this.checkTime(null, null, interval, null);
    }

    /**
     * 检查并获取时间
     *
     * @param offsetHour 日期偏移量，通常是一个负数，会获得一个[当前时间+偏移量，当前时间]格式的日期
     * @return 返回String类型的日期数组，[当前时间+offsetHour，当前时间]
     */
    public String[] checkTime(Integer offsetHour) {
        return this.checkTime(null, null, null, offsetHour);
    }

    /**
     * 检查并获取时间
     *
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param interval   统计间隔, 与 offsetHour 二选一，offsetHour 优先级更高
     * @param offsetHour 日期偏移量，通常是一个负数，会获得一个[当前时间+偏移量，当前时间]格式的日期, 与 interval 二选一，offsetHour 优先级更高
     * @return 返回String类型的日期数组，[开始时间，结束时间]
     */
    public String[] checkTime(String startTime, String endTime, String interval, Integer offsetHour) {
        if (offsetHour != null) {
            Date now = DateUtils.date();
            endTime = DateUtils.toYMDHMS(now);
            startTime = DateUtils.toYMDHMS(DateUtils.offsetHour(now, offsetHour));
            return new String[]{startTime, endTime};
        }
        if (StrUtil.isNotBlank(interval)) {
            Date now = DateUtils.date();
            endTime = DateUtils.toYMDHMS(now);
            switch (interval) {
                case "5m":
                    startTime = DateUtils.toYMDHMS(DateUtils.offsetMinute(now, -5));
                    break;
                case "10m":
                    startTime = DateUtils.toYMDHMS(DateUtils.offsetMinute(now, -10));
                    break;
                case "1h":
                    startTime = DateUtils.toYMDHMS(DateUtils.offsetHour(now, -1));
                    break;
                case "6h":
                    startTime = DateUtils.toYMDHMS(DateUtils.offsetHour(now, -6));
                    break;
                case "12h":
                    startTime = DateUtils.toYMDHMS(DateUtils.offsetHour(now, -12));
                    break;
                case "1d":
                    startTime = DateUtils.toYMDHMS(DateUtils.offsetHour(now, -24));
                    break;
                default:
                    throw new XzException400("无效的类型");
            }
        }
        return new String[]{startTime, endTime};
    }

    /**
     * 根据日期计算日期间隔
     *
     * @param startTime     开始日期
     * @param endTime       结束日期
     * @param quickInterval 快捷区间
     * @param repoType      类型, 用于生成指标解释
     * @return 返回
     */
    protected ResourceHistogram explain(String startTime,
                                        String endTime,
                                        String quickInterval,
                                        Integer customInterval,
                                        TimeUnit customIntervalUnit,
                                        String repoType) {
        String[] times = checkTime(startTime, endTime, quickInterval);

        ResourceHistogram rh = new ResourceHistogram();
        rh.setStartTime(times[0]);
        rh.setStartTimestamp(DateUtils.toTimestamp(times[0]));
        rh.setEndTime(times[1]);
        rh.setEndTimestamp(DateUtils.toTimestamp(times[1]));

        long diff = (rh.getEndTimestamp() - rh.getStartTimestamp()) / 1000;
        XzException400.throwBy(diff <= 0, "结束时间不能大于或等于开始时间");

        if (customInterval != null && customIntervalUnit != null) {
            XzException400.throwBy(customIntervalUnit == TimeUnit.MILLISECONDS, "时间单位最小为秒 [SECONDS]");
            if (customIntervalUnit == TimeUnit.SECONDS) {
                rh.setIntervalMs(customInterval * 1000);
            } else {
                rh.setIntervalMs(customInterval * 1000 * 60);
            }

            rh.setInterval(customInterval);
            rh.setTimeUnit(customIntervalUnit);
            rh.setTitle(String.format("本次统计按每【%s%s】聚合", customInterval, customIntervalUnit));
        } else {
            // 自动判断时间
            // 相差的秒数
            // 小于1小时, 按1秒展示, 最多3600条
            if (diff < 3601) {
                rh.setIntervalMs(1000);
                rh.setInterval(1);
                rh.setTimeUnit(TimeUnit.SECONDS);
                rh.setTitle("本次统计按每【1】秒种聚合");
            }
            // 小于2小时, 按5秒展示, 最多1440条
            else if (diff < 7201) {
                rh.setIntervalMs(5000);
                rh.setInterval(5);
                rh.setTimeUnit(TimeUnit.SECONDS);
                rh.setTitle("本次统计按每【1】秒种聚合");
            }
            // 小于12小时, 按30秒展示, 最多1440条
            else if (diff < 43201) {
                rh.setIntervalMs(30 * 1000);
                rh.setInterval(30);
                rh.setTimeUnit(TimeUnit.SECONDS);
                rh.setTitle("本次统计按每【30】秒种聚合");
            }
            // 小于二十四小时, 按1分钟展示, 最多1440条
            else if (diff < 86401) {
                rh.setIntervalMs(60 * 1000);
                rh.setInterval(1);
                rh.setTimeUnit(TimeUnit.MINUTES);
                rh.setTitle("本次统计按每【1】分种聚合");
            }
            // 大于二十四小时, 按5分钟展示
            else {
                rh.setIntervalMs(5 * 60 * 1000);
                rh.setInterval(5);
                rh.setTimeUnit(TimeUnit.MINUTES);
                rh.setTitle("本次统计按每【5】分种聚合");
            }
        }


        // 小于1分钟, 用秒, 否则用分钟来格式化日期
        if (rh.getIntervalMs() < 60 * 1000) {
            rh.setPattern(DatePattern.NORM_DATETIME_PATTERN);
        } else {
            rh.setPattern(DatePattern.NORM_DATETIME_MINUTE_PATTERN);
        }
        setTitle(rh, repoType);
        return rh;
    }

    private void setTitle(ResourceHistogram rh, String repoType) {
        if ("local".equals(repoType)) {
            rh.setSubTitle(String.format(
                    "1. 机器名【%s(%s)】\n" +
                    "2. %s\n",
//                            "3. 单机流量请在右上角选择集群\n" +
//                            "4. 被查询的机器受负载均衡控制",
                    SystemUtil.getHostName(),
                    SystemUtil.getIp(),
                    rh.getTitle()
            ));
            rh.setTitle("单机流量统计");
        } else if ("elasticsearch".equals(repoType)) {
            rh.setSubTitle(String.format(
                    "1. %s\n" +
                            "2. 当前统计为集群流量",
                    rh.getTitle()));
            rh.setTitle("集群流量统计");
        }
    }

    @Data
    protected static class ResourceHistogram {
        private String startTime;
        private String endTime;
        private Long startTimestamp;
        private Long endTimestamp;
        /**
         * 日期格式化
         * 按秒格式化:DatePattern.NORM_DATETIME_PATTERN;
         * 分钟格式化:DatePattern.NORM_DATETIME_MINUTE_PATTERN;
         */
        private String pattern;
        /**
         * 直方图的解释
         */
        private String title;
        private String subTitle;
        /**
         * 计算区间的范围, 单位毫秒
         */
        private Integer intervalMs;
        /**
         * 计算区间的范围, 单位是 TimeUnit
         */
        private Integer interval;
        /**
         * 只有秒和分两种
         */
        private TimeUnit timeUnit;
    }

    protected OneLineMetric lineToMetric(String line) {
        String[] s = line.split("\\|");
        if (s.length != 10) {
            log.warn("Sentinel 日志解析格式不正确:{}, 正常情况包含10个字段", line);
            return null;
        }
        OneLineMetric metric = new OneLineMetric();
        metric.setTimestamp(Long.valueOf(s[0]));
        metric.setResource(s[1]);
        metric.setP(Integer.valueOf(s[2]));
        metric.setB(Integer.valueOf(s[3]));
        metric.setS(Integer.valueOf(s[4]));
        metric.setE(Integer.valueOf(s[5]));
        metric.setRt(Integer.valueOf(s[6]));
        return metric;
    }

    @Data
    public static class MetricValue {
        private Integer count;
        private Integer sumRt;
        private Integer minRt;
        private Integer maxRt;

        private Integer success;
        private Integer exception;
        private Integer pass;
        private Integer block;

        public static MetricValue init() {
            MetricValue value = new MetricValue();
            value.setCount(0);
            value.setCount(0);
            value.setSumRt(0);
            value.setMinRt(0);
            value.setMaxRt(0);
            value.setSuccess(0);
            value.setException(0);
            value.setPass(0);
            value.setBlock(0);
            return value;
        }
    }

}
