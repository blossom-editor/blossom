package com.blossom.backend.server.article.stat.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 文章编辑热力图统计
 *
 * @author xzzz
 */
@Data
public class ArticleHeatmapRes implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * [
     * [日期,值],
     * [日期,值]
     * ]
     */
    private List<Object[]> data;
    /**
     * 最大修改数据
     */
    private Integer maxStatValues;
    /**
     * 开始时间
     */
    private String dateBegin;
    /**
     * 结束时间
     */
    private String dateEnd;
}
