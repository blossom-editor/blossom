package com.blossom.backend.server.article.draft.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.blossom.backend.server.article.reference.pojo.ArticleReferenceReq;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 文章
 *
 * @author xzzz
 */
@Data
@TableName("blossom_article")
@EqualsAndHashCode(callSuper = true)
public class ArticleEntity extends AbstractPOJO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
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
     * star状态
     */
    private Integer starStatus;
    /**
     * 公开状态
     */
    private Integer openStatus;
    /**
     * 公开版本
     */
    private Integer openVersion;
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
     * 目录
     */
    private String toc;
    /**
     * Markdown 内容
     */
    private String markdown;
    /**
     * Html 内容
     */
    private String html;
    /**
     * 版本
     */
    private Date creTime;
    /**
     * 修改时间
     */
    private Date updTime;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 文章内容的修改时间
     */
    private Date updMarkdownTime;

    //region ============================== 非数据库字段 ==============================
    /**
     * 引用集合
     */
    @TableField(exist = false)
    private List<ArticleReferenceReq> references;

    /**
     * 父ID集合
     */
    @TableField(exist = false)
    private List<Long> pids;

    //endregion
}
