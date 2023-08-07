package com.blossom.backend.server.article.draft.pojo;

import com.blossom.common.db.pojo.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 文章查询请求
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleQueryReq extends PageReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 上级ID集合
     */
    private List<Long> pids;
    /**
     * star状态, YesNoEnum
     */
    private Integer starStatus;
    /**
     * 公开状态
     */
    private Integer openStatus;
    /**
     * 用户ID
     */
    private Long userId;
}
