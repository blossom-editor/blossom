package com.blossom.backend.server.article.recycle.pojo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 回收站列表
 *
 * @since 1.10.0
 */
@Data
public class ArticleRecycleRestoreReq {
    /**
     * 文章ID
     */
    @Min(value = 0, message = "[文章ID] 不能小于0")
    @NotNull(message = "[文章ID] 为必填项")
    private Long id;
}
