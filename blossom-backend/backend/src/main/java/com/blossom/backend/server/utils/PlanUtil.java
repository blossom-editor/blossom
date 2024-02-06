package com.blossom.backend.server.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.blossom.backend.server.plan.pojo.PlanDayRes;
import com.blossom.common.base.util.SortUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
    private static TreeMap<String, List<PlanDayRes>> byDay(List<PlanDayRes> plans) {
        TreeMap<String, List<PlanDayRes>> map = new TreeMap<>();
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
        Map<String, List<PlanDayRes>> byDay = byDay(plans);
        Map<Long, List<PlanDayRes>> byGroupId = byGroupId(plans);

        // 先为所有计划设置默认排序
        plans.forEach(p -> p.setSort(-1));

        byDay.forEach((date, list) -> {
            list.sort((p1, p2) -> SortUtil.strSort.compare(p1.getPlanStartTime(), p2.getPlanStartTime()));
            for (PlanDayRes plan : list) {
                if (plan.getSort() == null) {
                    plan.setSort(-1);
                }
                // 排序如果不为 -1，相当于该计划已经被安排好排序了，则跳过
                if (plan.getSort() > -1) {
                    continue;
                }
                // 遍历当天中所有的计划
                for (int i = 0; i < list.size(); i++) {
                    final int targetSort = i;
                    // 如果该排序已经被占用，则跳过
                    boolean targetSortHasUsed = list.stream().anyMatch(p -> p.getSort() == targetSort && p.getId() > 0);
                    if (targetSortHasUsed) {
                        continue;
                    }
                    // 否则将该计划以及计划所在组的所有计划都设置为该排序
                    for (PlanDayRes groupPlan : byGroupId.get(plan.getGroupId())) {
                        groupPlan.setSort(targetSort);
                    }
                    break;
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
    public static TreeMap<String, List<PlanDayRes>> sortToTreeMap(List<PlanDayRes> plans, boolean debug) {
        if (CollUtil.isEmpty(plans)) {
            return new TreeMap<>();
        }
        Map<String, List<PlanDayRes>> map = sort(plans).stream()
                .collect(Collectors.groupingBy(PlanDayRes::getPlanDate));
        TreeMap<String, List<PlanDayRes>> treeMap = MapUtil.sort(map);
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
    private static void debugTreeMap(TreeMap<String, List<PlanDayRes>> treeMap) {
        for (String date : treeMap.keySet()) {
            System.out.print(date + "|");
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
    private static PlanDayRes getHolderPlan(String date, int sort) {
        PlanDayRes holder = new PlanDayRes();
        holder.setId(sort * -1L - 1);
        holder.setGroupId(0L);
        holder.setPlanDate(date);
        holder.setSort(sort);
        holder.setColor("hold");
        return holder;
    }

    public static void main(String[] args) {
        List<PlanDayRes> plans = new ArrayList<>();
        PlanDayRes p1_1 = new PlanDayRes();
        p1_1.setId(1L);
        p1_1.setGroupId(1L);
        p1_1.setPlanDate("2023-09-01");
        plans.add(p1_1);

        PlanDayRes p1_2 = new PlanDayRes();
        p1_2.setId(2L);
        p1_2.setGroupId(1L);
        p1_2.setPlanDate("2023-09-02");
        plans.add(p1_2);

        PlanDayRes p2 = new PlanDayRes();
        p2.setId(3L);
        p2.setGroupId(2L);
        p2.setPlanDate("2023-09-01");
        plans.add(p2);

        PlanDayRes p3_1 = new PlanDayRes();
        p3_1.setId(4L);
        p3_1.setGroupId(3L);
        p3_1.setPlanDate("2023-09-02");
        plans.add(p3_1);

        PlanDayRes p3_2 = new PlanDayRes();
        p3_2.setId(4L);
        p3_2.setGroupId(3L);
        p3_2.setPlanDate("2023-09-03");
        plans.add(p3_2);

        sortToTreeMap(plans, true);
    }

}
