package com.blossom.backend.server.article.draft.pojo;

import com.blossom.backend.server.article.draft.ArticleController;
import com.blossom.backend.server.article.reference.pojo.ArticleReferenceReq;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 修改文章内容
 *
 * <p>
 * {@link ArticleController#updateContent(ArticleUpdContentReq)}
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleUpdContentReq extends AbstractPOJO {

    /**
     * ID
     */
    @Min(value = 0, message = "[文章ID] 不能小于0")
    @NotNull(message = "[文章ID] 为必填项")
    private Long id;
    /**
     * 名称, 用于引用关系表中的名称冗余
     */
    @NotBlank(message = "文章名称为必填项")
    private String name;
    /**
     * markdown 内容
     */
    private String markdown;
    /**
     * html 内容
     */
    private String html;
    /**
     * 目录
     */
    private String toc;
    /**
     * 引用链接集合
     */
    private List<ArticleReferenceReq> references;
}
