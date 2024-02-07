package com.blossom.expand.sentinel.metric.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.command.CommandHandler;
import com.alibaba.csp.sentinel.command.CommandRequest;
import com.alibaba.csp.sentinel.command.CommandResponse;
import com.alibaba.csp.sentinel.command.handler.FetchClusterNodeByIdCommandHandler;
import com.alibaba.csp.sentinel.command.handler.FetchClusterNodeHumanCommandHandler;
import com.alibaba.csp.sentinel.command.handler.FetchSimpleClusterNodeCommandHandler;
import com.alibaba.csp.sentinel.command.handler.SendMetricCommandHandler;
import com.alibaba.csp.sentinel.transport.command.SimpleHttpCommandCenter;
import com.blossom.common.base.exception.XzException400;
import com.blossom.common.base.exception.XzException404;
import com.blossom.common.base.exception.XzException500;
import com.blossom.common.base.pojo.R;
import com.blossom.common.base.util.DateUtils;
import com.blossom.common.base.util.SortUtil;
import com.blossom.common.base.util.json.JsonUtil;
import com.blossom.expand.sentinel.metric.pojo.MetricLineRes;
import com.blossom.expand.sentinel.metric.pojo.MetricRes;
import com.blossom.expand.sentinel.metric.pojo.OneLineMetric;
import com.blossom.expand.sentinel.metric.pojo.ResourcesRes;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 流量监控【本地日志】
 *
 * @author xzzz
 * @apiNote Sentinel 功能逻辑与返回说明见文档: https://www.yuque.com/xiaozeizeizi/learning/rczi6d
 */
@Slf4j
@RestController
@RequestMapping("/sentinel")
public class SentinelMetricController extends AbstractSentinelController {

    /**
     * 获取资源列表, Sentinel处理类 {@link FetchSimpleClusterNodeCommandHandler}
     */
    private static final String CLUSTER_NODE = "clusterNode";

    /**
     * 获取某个资源信息, Sentinel处理类 {@link FetchClusterNodeByIdCommandHandler}
     */
    private static final String CLUSTER_NODE_BY_ID = "clusterNodeById";

    /**
     * 实时指标监控接口, Sentinel处理类 {@link SendMetricCommandHandler}
     */
    private static final String METRIC = "metric";

    /**
     * 资源的秒级, 分钟级指标信息, Sentinel处理类 {@link FetchClusterNodeHumanCommandHandler}
     */
    private static final String CNODE = "cnode";

    /**
     * 资源列表
     *
     * @param id 资源名称
     * @return 资源列表
     */
    @GetMapping("/clusterNode")
    public R<JsonNode> clusterNode(@RequestParam(value = "id", required = false) String id) {
        CommandHandler<?> commandHandler;
        if (StrUtil.isBlank(id)) {
            commandHandler = SimpleHttpCommandCenter.getHandler(CLUSTER_NODE);
        } else {
            commandHandler = SimpleHttpCommandCenter.getHandler(CLUSTER_NODE_BY_ID);
        }
        CommandRequest request = new CommandRequest();
        request.addParam("id", id);
        CommandResponse<?> response = commandHandler.handle(request);
        return R.ok(JsonUtil.toJsonNode((String) response.getResult()));
    }

    /**
     * 一天内被请求的资源列表
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param interval  快捷区间, 优先级比日期高
     * @return 资源列表
     */
    @GetMapping("/resources")
    public R<List<ResourcesRes>> resources(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String interval) {
        String[] times = checkTime(startTime, endTime, interval);
        startTime = times[0];
        endTime = times[1];
        String all = this.metric("", startTime, endTime, "");
        String[] lines = all.split("\n");
        XzException404.throwBy(lines.length == 0, "无指标数据");

        Set<ResourcesRes> resources = new HashSet<>();
        Map<String, List<OneLineMetric>> resourcesMetricMap = new HashMap<>(20);

        for (String line : lines) {
            OneLineMetric metric;
            if (StrUtil.isBlank(line) || (metric = lineToMetric(line)) == null) {
                continue;
            }
            List<OneLineMetric> metricList = resourcesMetricMap.getOrDefault(metric.getResource(), new ArrayList<>());
            metricList.add(metric);
            resourcesMetricMap.put(metric.getResource(), metricList);
        }

        resourcesMetricMap.forEach((resourceName, metrics) -> {
            ResourcesRes resourceMetric = new ResourcesRes();
            resourceMetric.setResource(resourceName);
            resourceMetric.setSuccess(metrics.stream().mapToInt(OneLineMetric::getS).sum());
            resourceMetric.setAvgRt((double) Math.round(metrics.stream().mapToDouble(OneLineMetric::getRt).average().orElse(0d) * 100) / 100);
            resourceMetric.setMaxRt(metrics.stream().mapToInt(OneLineMetric::getRt).max().orElse(0));
            resourceMetric.setMinRt(metrics.stream().mapToInt(OneLineMetric::getRt).min().orElse(0));
            resources.add(resourceMetric);
        });


        return R.ok(
                CollUtil.reverse(
                        resources.stream()
                                .sorted((r1, r2) -> SortUtil.intSort.compare(r1.getSuccess(), r2.getSuccess())).
                                collect(Collectors.toList())
                )
        );
    }


    /**
     * 资源的监控信息
     *
     * @param resource  资源名称, 如果不传入资源名称, 则搜索全部资源
     * @param startTime 开始时间
     * @param endTime   结束时间, 如果不传入结束时间, 则按日志最大行数搜索
     * @param maxLines  文件中查询最大行数, sentinel 本地查询最大 12000 行
     * @return 资源监控
     */
    @GetMapping("/metric")
    public String metric(@RequestParam(required = false) final String resource,
                         String startTime,
                         @RequestParam(required = false) String endTime,
                         @RequestParam(required = false) String maxLines) {
        CommandRequest request = new CommandRequest();
        if (StrUtil.isNotBlank(resource)) {
            // sentinel 原生API字段为 identity
            request.addParam("identity", resource);
        }
        if (StrUtil.isNotBlank(endTime)) {
            request.addParam("endTime", String.valueOf(DateUtils.toTimestamp(endTime)));
        }
        XzException400.throwBy(StrUtil.isBlank(startTime), "开始日期为必填项");
        XzException400.throwBy(StrUtil.isBlank(endTime) && StrUtil.isBlank(maxLines), "未填结束日期时, 日志搜索行数不得为空");

        request.addParam("startTime", String.valueOf(DateUtils.toTimestamp(startTime)));
        request.addParam("maxLines", maxLines);

        CommandHandler<?> commandHandler = SimpleHttpCommandCenter.getHandler(METRIC);
        XzException500.throwBy(commandHandler == null, "系统当前无访问流量，Sentinel 尚未初始化，请访问后再查询此接口");

        // 如果命令处理类存在, 则用该类处理该命令
        CommandResponse<?> response = commandHandler.handle(request);
        return (String) response.getResult();
    }

    /**
     * 资源折线图
     *
     * @param resource           资源名称
     * @param startTime          开始时间
     * @param endTime            结束时间
     * @param interval           快捷区间, 用于约定时间范围, 会自定生成开始和结束时间
     * @param customInterval     自定义单位聚合区间, 如 1m, 10m
     * @param customIntervalUnit 自定义单位聚合区间的时间单位, 如 SECONDS, MINUTES
     * @return EChart 使用的数据结构
     */
    @GetMapping("/metric/line")
    public R<MetricLineRes> metricLine(final String resource,
                                       String startTime,
                                       String endTime,
                                       String interval,
                                       Integer customInterval,
                                       TimeUnit customIntervalUnit) {
        log.debug("[SENTINEL] 查询 Sentinel 单机流量折线图");
        ResourceHistogram rh = explain(startTime, endTime, interval, customInterval, customIntervalUnit, "local");
        // 读取日志
        String all = this.metric(resource, rh.getStartTime(), rh.getEndTime(), "");
        String[] lines = all.split("\n");
        XzException500.throwBy(lines.length == 0, "无流量指标数据, 请等待服务接收请求并产生日期");

        // 初始化区间内所有的日期
        Map<String, MetricValue> metricMap = new LinkedHashMap<>();
        for (long thisTimestamp = rh.getStartTimestamp(); thisTimestamp <= rh.getEndTimestamp(); thisTimestamp += rh.getIntervalMs()) {
            long remainder = thisTimestamp % rh.getIntervalMs();
            // 一个区间的开始日期
            long thisTimeInterval = thisTimestamp - remainder;
            metricMap.put(DateUtils.format(DateUtils.date(thisTimeInterval), rh.getPattern()), MetricValue.init());
        }

        // 单时间节点的监控信息, 按时间插入, [日志时间：日志统计结果]
        for (String line : lines) {
            if (StrUtil.isBlank(line)) {
                continue;
            }
            OneLineMetric lineMetric;
            if (StrUtil.isBlank(line) || (lineMetric = lineToMetric(line)) == null) {
                continue;
            }

            // 计算日期区间
            long logHistogramDt = lineMetric.getTimestamp() - (lineMetric.getTimestamp() % rh.getIntervalMs());
            String metricMapKey = DateUtils.format(DateUtils.date(logHistogramDt), rh.getPattern());

            MetricValue value = metricMap.get(metricMapKey);
            if (value == null) {
                continue;
            }
            value = metricMap.get(metricMapKey);
            value.setSuccess(value.getSuccess() + lineMetric.getS());
            value.setException(value.getException() + lineMetric.getE());
            value.setPass(value.getPass() + lineMetric.getP());
            value.setBlock(value.getBlock() + lineMetric.getB());
            value.setSumRt(value.getSumRt() + lineMetric.getRt());
            value.setCount(value.getCount() + 1);
            Integer minRt = value.getMinRt();
            Integer maxRt = value.getMaxRt();
            if (lineMetric.getRt() < minRt) {
                value.setMinRt(lineMetric.getRt());
            }
            if (lineMetric.getRt() > maxRt) {
                value.setMaxRt(lineMetric.getRt());
            }
            metricMap.put(metricMapKey, value);
        }

        final MetricLineRes metrics = new MetricLineRes(metricMap.size());
        metrics.setResource(resource);
        metrics.setTitle(rh.getTitle());
        metrics.setSubTitle(rh.getSubTitle());
        int i = 0;
        for (Map.Entry<String, MetricValue> entry : metricMap.entrySet()) {
            metrics.addMetric(entry.getKey(), entry.getValue(), i++);
        }
        return R.ok(metrics);
    }

    /**
     * 集群过去24小时的总体信息
     *
     * @return 集群流量信息
     * @apiNote 资源名: __total_inbound_traffic__
     */
    @GetMapping("/metric/app")
    public R<MetricRes> metricTotalInboundTraffic(@RequestParam(required = false) String appName) {
        log.info("查询应用24小时流量统计");
        String[] times = checkTime(-24);
        String startTime = times[0];
        String endTime = times[1];

        String all = this.metric("__total_inbound_traffic__", startTime, endTime, "");
        String[] lines = all.split("\n");
        XzException500.throwBy(lines.length == 0, "无指标数据");
        MetricRes res = MetricRes.empty();
        for (String line : lines) {
            if (StrUtil.isBlank(line)) {
                continue;
            }
            OneLineMetric metric;
            if (StrUtil.isBlank(line) || (metric = lineToMetric(line)) == null) {
                continue;
            }

            res.setPass(res.getPass() + metric.getP());
            res.setBlock(res.getBlock() + metric.getB());
            res.setSuccess(res.getSuccess() + metric.getS());
            res.setException(res.getException() + metric.getE());
            res.setAvgRt(res.getAvgRt() + metric.getRt());

            Integer maxQps = res.getMaxQps();
            if (metric.getS() > maxQps) {
                res.setMaxQps(metric.getS());
            }

            Integer minRt = res.getMinRt();
            if (metric.getRt() < minRt) {
                res.setMinRt(metric.getRt());
            }

            Integer maxRt = res.getMaxRt();
            if (metric.getRt() > maxRt) {
                res.setMaxRt(metric.getRt());
            }
        }

        res.setAvgRt((double) Math.round(res.getAvgRt() / lines.length));
        res.setAvgQps(res.getSuccess() / lines.length);
        return R.ok(res);
    }


    /**
     * 资源的秒级, 分钟级指标信息
     *
     * @param id 资源名称
     * @return 秒级, 分钟级指标信息
     */
    @GetMapping("/cnode")
    public String cnode(String id) {
        CommandRequest request = new CommandRequest();
        request.addParam("id", id);
        CommandHandler<?> commandHandler = SimpleHttpCommandCenter.getHandler(CNODE);
        // 如果命令处理类存在, 则用该类处理该命令
        CommandResponse<?> response = commandHandler.handle(request);
        return (String) response.getResult();
    }

}

