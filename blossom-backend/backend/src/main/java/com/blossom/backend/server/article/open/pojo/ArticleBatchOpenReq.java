package com.blossom.backend.server.article.open.pojo;


import com.blossom.common.base.enums.YesNo;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 文件夹下的文章全部公开
 *
 * @author xzzz
 * @since 1.14.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleBatchOpenReq extends AbstractPOJO {

    /**
     * 文章ID
     */
    @Min(value = 0, message = "[文件夹ID] 不能小于0")
    @NotNull(message = "[文件夹ID] 为必填项")
    private Long pid;

    /**
     * 公开状态 {@link YesNo}
     *
     * @see YesNo
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
