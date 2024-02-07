package com.blossom.backend.server.plan;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blossom.backend.server.plan.pojo.*;
import com.blossom.backend.server.utils.PlanUtil;
import com.blossom.common.base.util.BeanUtil;
import com.blossom.common.base.util.DateUtils;
import com.blossom.common.base.util.PrimaryKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 计划
 *
 * @author xzzz
 */
@Slf4j
@Service
public class PlanService extends ServiceImpl<PlanMapper, PlanEntity> {

    /**
     * 按月查询每日计划
     */
    public Map<String, List<PlanDayRes>> listDay(String month, Long userId) {
        PlanEntity where = new PlanEntity();
        where.setPlanMonth(month);
        where.setUserId(userId);
        where.setType(PlanTypeEnum.DAY.getType());
        List<PlanDayRes> list = new ArrayList<>();
        for (PlanEntity plan : baseMapper.listAll(where)) {
            PlanDayRes res =  BeanUtil.toObj(plan, PlanDayRes.class);
            res.setPlanDate(DateUtils.toYMD(plan.getPlanDate()) + " 00:00:00");
            list.add(res);
        }

        return PlanUtil.sortToTreeMap(list, false);
    }

    /**
     * 查询日常计划
     */
    public List<PlanDailyRes> listDaily(Long userId) {
        PlanEntity where = new PlanEntity();
        where.setType(PlanTypeEnum.DAILY.getType());
        where.setUserId(userId);

        List<PlanEntity> entities = baseMapper.listAll(where);
        if (CollUtil.isEmpty(entities)) {
            return new ArrayList<>();
        }

        List<PlanDailyRes> plans = BeanUtil.toList(entities, PlanDailyRes.class);
        String today = DateUtils.today() + " ";
        for (PlanDailyRes plan : plans) {
            Date start = DateUtils.parse(today + plan.getPlanStartTime() + ":00", DateUtils.PATTERN_YYYYMMDDHHMMSS);
            Date end = DateUtils.parse(today + plan.getPlanEndTime() + ":00", DateUtils.PATTERN_YYYYMMDDHHMMSS);
            plan.setCurrent(DateUtils.isIn(DateUtil.date(), start, end));
        }
        return plans;
    }

    /**
     * 新增每日计划
     */
    public void addDay(PlanDayAddReq req) {
        List<PlanEntity> plans = new ArrayList<>();

        if (req.getRepeatDay() == null || req.getRepeatDay() < 1) {
            req.setRepeatDay(1);
        }

        if (req.getRepeatDay() == 1) {
            PlanEntity plan = req.to(PlanEntity.class);
            plan.setGroupId(PrimaryKeyUtil.nextId());
            plan.setType(PlanTypeEnum.DAY.getType());
            plan.setPosition(PlanPositionEnum.ALL.getPosition());
            plan.setPlanMonth(DateUtils.format(plan.getPlanDate(), DateUtils.PATTERN_YYYYMM));
            plan.setImg("");
            plans.add(plan);
        } else {
            final Long groupId = PrimaryKeyUtil.nextId();
            Date planDate = req.getPlanDate();
            for (int i = 1; i <= req.getRepeatDay(); i++) {
                PlanEntity plan = req.to(PlanEntity.class);
                plan.setGroupId(groupId);
                plan.setType(PlanTypeEnum.DAY.getType());
                plan.setPlanMonth(DateUtils.format(planDate, DateUtils.PATTERN_YYYYMM));
                plan.setPlanDate(planDate);
                plan.setImg("");
                if (i == 1) {
                    plan.setPosition(PlanPositionEnum.HEAD.getPosition());
                } else if (i == req.getRepeatDay()) {
                    plan.setPosition(PlanPositionEnum.TAIL.getPosition());
                } else {
                    plan.setPosition(PlanPositionEnum.MID.getPosition());
                }
                plans.add(plan);
                planDate = DateUtils.offsetDay(planDate, 1);
            }
        }
        baseMapper.insertList(plans);
    }

    /**
     * 修改每日计划
     * <p>只能修改标题和内容, 如果是修改日期等信息需要重新新增
     *
     * @since 1.9.0
     */
    public void updDay(PlanDayUpdReq req) {
        PlanEntity upd = new PlanEntity();
        upd.setGroupId(req.getGroupId());
        upd.setTitle(req.getTitle());
        upd.setContent(req.getContent());
        upd.setUserId(req.getUserId());
        baseMapper.updByGroupId(upd);
    }

    /**
     * 新增日常计划
     */
    public void addDaily(PlanDailyAddReq req) {
        PlanEntity plan = req.to(PlanEntity.class);
        plan.setGroupId(0L);
        plan.setType(PlanTypeEnum.DAILY.getType());
        baseMapper.insert(plan);
    }

    /**
     * 删除计划
     */
    public void del(PlanDelReq req) {
        baseMapper.delById(req.getId(), req.getGroupId());
    }

}
