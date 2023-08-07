package com.blossom.backend.server.article.open.pojo;


import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 公开文章同步
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleOpenSyncReq extends AbstractPOJO {

    /**
     * 文章ID
     */
    @Min(value = 0, message = "[文章ID] 不能小于0")
    @NotNull(message = "[文章ID] 为必填项")
    private Long id;
}
