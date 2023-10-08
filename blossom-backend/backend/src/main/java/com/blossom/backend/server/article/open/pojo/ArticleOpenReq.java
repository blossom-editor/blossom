package com.blossom.backend.server.article.open.pojo;


import com.blossom.common.base.enums.YesNo;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 文章公开
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleOpenReq extends AbstractPOJO {

    /**
     * 文章ID
     */
    @Min(value = 0, message = "[文章ID] 不能小于0")
    @NotNull(message = "[文章ID] 为必填项")
    private Long id;

    /**
     * 公开状态 {@link YesNo}
     * @see com.blossom.common.base.enums.YesNo
     */
    @Min(value = 0, message = "[open 状态] 不能小于0")
    @Max(value = 1, message = "[open 状态] 不能大于1")
    @NotNull(message = "[open 状态] 为必填项")
    private Integer openStatus;

    /**
     * 用户ID
     */
    private Long userId;
}
