package com.blossom.backend.server.article.draft.pojo;

import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 添加,取消标签
 *
 * @since 1.10.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleUpdTagReq extends AbstractPOJO {

    /**
     * ID
     */
    @Min(value = 0, message = "[文章ID] 不能小于0")
    @NotNull(message = "[文章ID] 为必填项")
    private Long id;
    /**
     * 标签, toc 标签因为具有特殊意义, 必须小写
     */
    @NotBlank(message = "标签为必填项")
    private String tag;
}
