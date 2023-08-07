package com.blossom.backend.thirdparty.gitee.pojo;

import lombok.Data;

import java.util.List;

/**
 * 热力图响应
 *
 * @author xzzz
 */
@Data
public class HeatmapRes {
    /**
     * 最大修改数据
     */
    private Integer maxUpdateNum;
    /**
     * 开始时间
     */
    private String dateBegin;
    /**
     * 结束时间
     */
    private String dateEnd;
    /**
     * 从开始时间到结束的数据
     */
    private List<Object[]> data;
}
