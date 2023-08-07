package com.blossom.backend.server.article.open.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文章公开响应
 *
 * @author xzzz
 */
@Data
public class ArticleOpenRes implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 文件夹ID
     */
    private Long pid;
    /**
     * 字数
     */
    private Integer words;
    /**
     * 公开文章版本
     */
    private Integer openVersion;
    /**
     * 初次公开时间
     */
    private Date openTime;
    /**
     * 同步时间
     */
    private Date syncTime;
    /**
     * markdown 正文
     */
    private String markdown;
    /**
     * html 正文
     */
    private String html;
}
