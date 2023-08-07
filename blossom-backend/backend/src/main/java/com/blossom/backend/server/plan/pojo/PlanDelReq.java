package com.blossom.backend.server.plan.pojo;

import lombok.Data;

/**
 * 删除计划
 *
 * @author xzzz
 */
@Data
public class PlanDelReq {

    /**
     * 计划ID
     */
    private Long id;

    /**
     * 计划组ID
     */
    private Long groupId;
}
