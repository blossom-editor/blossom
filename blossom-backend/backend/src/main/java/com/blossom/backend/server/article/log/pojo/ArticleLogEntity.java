package com.blossom.backend.server.article.log.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;
import java.io.Serializable;

/**
 * 文章记录
 */
@Data
@TableName("blossom_article_log")
@EqualsAndHashCode(callSuper = true)
public class ArticleLogEntity extends AbstractPOJO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @TableId
    private Long id;
    /** 文章ID */
    private Long articleId;
    /** 版本 */
    private Integer version;
    /** 文章内容 */
    private String markdown;
    /** 修改日期 */
    private Date creTime;
}
