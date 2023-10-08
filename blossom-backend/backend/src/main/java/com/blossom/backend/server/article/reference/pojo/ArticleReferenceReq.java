package com.blossom.backend.server.article.reference.pojo;

import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleReferenceReq extends AbstractPOJO {
    /**
     * 引用ID
     */
    private Long targetId;
    /**
     * 引用名称, 链接名称或图片名称
     */
    private String targetName;
    /**
     * 引用地址, 链接的地址或图片地址
     */
    private String targetUrl;
    /**
     * 类型 [暂无]
     */
    private Integer type;
}
