package com.blossom.backend.server.article.recycle.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 文章回收站
 *
 * @author xzzz
 * @since 1.10.0
 */
@Data
@TableName("blossom_article_recycle")
@EqualsAndHashCode(callSuper = true)
public class ArticleRecycleEntity extends AbstractPOJO implements Serializable {

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
     * 文章名称
     */
    private String name;
    /**
     * 文章图标
     */
    private String icon;
    /**
     * 标签集合
     */
    private String tags;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 封面
     */
    private String cover;
    /**
     * 描述
     */
    private String describes;
    /**
     * 页面的查看数
     */
    private Integer pv;
    /**
     * 独立的访问次数,每日IP重置
     */
    private Integer uv;
    /**
     * 点赞数
     */
    private Integer likes;
    /**
     * 文章字数
     */
    private Integer words;
    /**
     * 文章字数
     */
    private Integer version;
    /**
     * 颜色
     */
    private String color;
    /**
     * Markdown 内容
     */
    private String markdown;
    /**
     * 版本
     */
    private Date creTime;
    /**
     * 修改时间
     */
    private Date updTime;
    /**
     * 删除时间
     */
    private Date delTime;
    /**
     * 用户ID
     */
    private Long userId;
}
