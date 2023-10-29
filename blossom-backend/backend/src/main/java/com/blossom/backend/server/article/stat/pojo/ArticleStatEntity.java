package com.blossom.backend.server.article.stat.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.blossom.backend.server.article.stat.ArticleStatTypeEnum;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 文章统计表
 *
 * @author xzzz
 */
@Data
@TableName("blossom_stat")
@EqualsAndHashCode(callSuper = true)
public class ArticleStatEntity extends AbstractPOJO {

    @TableId
    private Long id;

    /**
     * 统计类型
     *
     * @see ArticleStatTypeEnum
     */
    private Integer type;

    /**
     * 统计日期 yyyy-MM-dd
     */
    private Date statDate;

    /**
     * 统计值
     */
    private Integer statValue;

    private Long userId;
}
