package com.blossom.backend.server.article.open.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 公开文章
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("blossom_article_open")
public class ArticleOpenEntity extends AbstractPOJO {

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
     * 公开版本的字数
     */
    private Integer words;
    /**
     * 公开版本
     */
    private Integer openVersion;
    /**
     * 公开时间
     */
    private Date openTime;
    /**
     * 同步时间
     */
    private Date syncTime;
    /**
     * 目录
     */
    private String toc;
    /**
     * markdown 内容
     */
    private String markdown;
    /**
     * html 内容
     */
    private String html;
}
