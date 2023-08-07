package com.blossom.backend.server.article.draft.pojo;

import com.blossom.backend.server.article.draft.ArticleController;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 修改文章内容
 * <p>
 * {@link ArticleController#updateContent(ArticleUpdContentReq)}
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleUpdContentRes extends AbstractPOJO  implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    private Long id;
    /**
     * 文章版本
     */
    private Integer version;
    /**
     * 字数
     */
    private Integer words;
    /**
     * 修改时间
     */
    private Date updTime;
}
