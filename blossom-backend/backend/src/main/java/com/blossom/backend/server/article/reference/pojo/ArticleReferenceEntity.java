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

    @TableId
    private Long id;

    private Long sourceId;
    private String sourceName;
    private Long targetId;
    private String targetName;
    private String targetUrl;
    private Integer type;

    private Long userId;

}
