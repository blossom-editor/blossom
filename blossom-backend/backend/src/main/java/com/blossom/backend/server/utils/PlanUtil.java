package com.blossom.backend.server.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.blossom.backend.server.plan.pojo.PlanDayRes;
import com.blossom.common.base.util.DateUtils;
import com.blossom.common.base.util.SortUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 计划工具类
 *
 * @author xzzz
 */
public class PlanUtil {

    /**
     * 将计划按天分组, 天数逐天递增
     *
     * @param plans 计划列表
     */
    private static TreeMap<Date, List<PlanDayRes>> byDay(List<PlanDayRes> plans) {
        TreeMap<Date, List<PlanDayRes>> map = new TreeMap<>();
        for (PlanDayRes plan : plans) {
            List<PlanDayRes> list = map.getOrDefault(plan.getPlanDate(), new ArrayList<>());
            list.add(plan);
            map.put(plan.getPlanDate(), list);
        }
        return map;
    }

    /**
     * 计划按 groupId 分组
     *
     * @param plans 计划列表
     */
    private static TreeMap<Long, List<PlanDayRes>> byGroupId(List<PlanDayRes> plans) {
        TreeMap<Long, List<PlanDayRes>> map = new TreeMap<>();
        for (PlanDayRes plan : plans) {
            List<PlanDayRes> list = map.getOrDefault(plan.getGroupId(), new ArrayList<>());
            list.add(plan);
            map.put(plan.getGroupId(), list);
        }
        return map;
    }

    /**
     * 对计划进行排序和填充占位的空计划
     *
     * @param plans 计划列表, 传入的计划列表需要提前按 {@link PlanDayRes#getPlanDate()} 升序
     * @return 排序后的计划列表, 注意并不是在 List 中进行排序, 而是补充了 sort 字段
     */
    public static List<PlanDayRes> sort(List<PlanDayRes> plans) {
        if (CollUtil.isEmpty(plans)) {
            return plans;
        }
        Map<Date, List<PlanDayRes>> byDay = byDay(plans);
        Map<Long, List<PlanDayRes>> byGroupId = byGroupId(plans);

        plans.forEach(p -> p.setSort(-1));

        byDay.forEach((date, list) -> {
            for (PlanDayRes plan : list) {
                if (plan.getSort() == null) {
                    plan.setSort(-1);
                }
                if (plan.getSort() > -1) {
                    continue;
                }
                for (int i = 0; i < list.size(); i++) {
                    final int targetSort = i;
                    boolean targetSortHasUsed = list.stream().anyMatch(p -> p.getSort() == targetSort && p.getId() > 0);
                    if (targetSortHasUsed) {
                        continue;
                    }
                    for (PlanDayRes groupPlan : byGroupId.get(plan.getGroupId())) {
                        groupPlan.setSort(targetSort);
                    }
                }
            }

            for (int i = 0; i < list.stream().mapToInt(PlanDayRes::getSort).max().getAsInt(); i++) {
                final int targetSort = i;
                if (list.stream().noneMatch(p -> p.getSort() == targetSort)) {
                    plans.add(getHolderPlan(date, targetSort));
                }
            }
        });
        return plans;
    }

    /**
     * 对计划进行排序和填充占位的空计划, 并转换成按天分组后的 treeMap
     *
     * @param plans 计划列表, 传入的计划列表需要提前按 {@link PlanDayRes#getPlanDate()} 升序
     * @return treeMap key 为日期(天), value 为该日期计划数组
     */
    public static TreeMap<Date, List<PlanDayRes>> sortToTreeMap(List<PlanDayRes> plans, boolean debug) {
        if (CollUtil.isEmpty(plans)) {
            return new TreeMap<>();
        }
        Map<Date, List<PlanDayRes>> map = sort(plans).stream()
                .collect(Collectors.groupingBy(PlanDayRes::getPlanDate));
        TreeMap<Date, List<PlanDayRes>> treeMap = MapUtil.sort(map);
        treeMap.forEach((k, v) -> {
            v = v.stream().sorted((p1, p2) -> SortUtil.intSort.compare(p1.getSort(), p2.getSort())).collect(Collectors.toList());
            treeMap.put(k, v);
        });

        if (debug) {
            debugTreeMap(treeMap);
        }

        return treeMap;
    }

    /**
     * 控制台打印计划的排序
     */
    private static void debugTreeMap(TreeMap<Date, List<PlanDayRes>> treeMap) {
        for (Date date : treeMap.keySet()) {
            System.out.print(DateUtils.format(date, DateUtils.PATTERN_YYYYMMDD) + "|");
        }
        System.out.println();
        for (int i = 0; i < treeMap.values().size(); i++) {
            for (List<PlanDayRes> value : treeMap.values()) {
                if (i > value.size() - 1) {
                    System.out.print("          |");
                } else {
                    System.out.print(StrUtil.fillBefore(String.valueOf(value.get(i).getGroupId()), ' ', 10));
                    System.out.print("|");
                }
            }
            System.out.println();
        }
    }

    /**
     * 获取一个占位计划, 用于在每日的计划列表数组中站位
     *
     * @param date 日期
     * @param sort 顺序
     */
    private static PlanDayRes getHolderPlan(Date date, int sort) {
        PlanDayRes holder = new PlanDayRes();
        holder.setId(sort * -1L - 1);
        holder.setGroupId(0L);
        holder.setPlanDate(date);
        holder.setSort(sort);
        holder.setColor("hold");
        return holder;
    }

}
