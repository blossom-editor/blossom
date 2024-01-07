package com.blossom.backend.base.search;

import lombok.Data;

import java.util.List;

/**
 * 全文搜索结果
 */
@Data
public class SearchRes {

    /**
     * 命中总数
     */
    private Long totalHit;

    /**
     * 命中数据
     */
    private List<Hit> hits;

    /**
     * 命中数据
     */
    @Data
    public static class Hit {
        /**
         * 主键
         */
        private Long id;
        /**
         * 文章名称
         */
        private String name;
        /**
         * 源文章名称
         */
        private String originName;
        /**
         * 标签
         */
        private List<String> tags;
        /**
         * 正文
         */
        private String markdown;
        /**
         * 命中分数
         */
        private Float score;
    }
}
