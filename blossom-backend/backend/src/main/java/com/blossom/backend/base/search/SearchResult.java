package com.blossom.backend.base.search;

import lombok.Data;

/**
 * 全文搜索返回对象
 */
@Data
public class SearchResult {
    /**
     * 主键
     */
    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 标签
     */
    private String tags;
    /**
     * 正文
     */
    private String content;

}
