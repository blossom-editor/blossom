package com.blossom.backend.server.article.reference.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 文章图片引用关系
 *
 * @author xzzz
 */
@Data
@TableName("blossom_article_reference")
@EqualsAndHashCode(callSuper = true)
public class ArticleReferenceEntity extends AbstractPOJO implements Serializable {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 文章ID
     */
    private Long sourceId;
    /**
     * 文章名称
     */
    private String sourceName;
    /**
     * 引用文章ID
     */
    private Long targetId;
    /**
     * 引用名称
     */
    private String targetName;
    /**
     * 引用链接
     */
    private String targetUrl;
    /**
     * 引用类型: 10:图片; 11:文章; 21:外部文章
     */
    private Integer type;
    /**
     * 用户ID
     */
    private Long userId;

}
