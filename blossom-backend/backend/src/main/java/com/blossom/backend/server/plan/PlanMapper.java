package com.blossom.backend.server.plan;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blossom.backend.server.plan.pojo.PlanEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 计划
 *
 * @author xzzz
 */
@Mapper
public interface PlanMapper extends BaseMapper<PlanEntity> {

    /**
     * 查询全部
     */
    List<PlanEntity> listAll(PlanEntity entity);

    /**
     * 批量插入
     *
     * @param plans 计划列表
     */
    void insertList(@Param("plans") List<PlanEntity> plans);

    /**
     * 根据分组ID修改
     * @since 1.9.0
     */
    void updByGroupId(PlanEntity entity);

    /**
     * 删除计划
     *
     * @param id      计划ID
     * @param groupId 计划分组ID
     */
    void delById(@Param("id") Long id, @Param("groupId") Long groupId);

    /**
     * 删除计划
     */
    void delByUserId(@Param("userId") Long userId);
}
