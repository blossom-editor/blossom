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
    private Long targetId;
    private String targetName;
    private String targetUrl;
    private Integer type;
}
