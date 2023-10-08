package com.blossom.backend.server.article.draft.pojo;

import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 文章响应
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleInfoRes extends AbstractPOJO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
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
    private List<String> tags;
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
     * 版本
     */
    private Integer version;
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
     * 颜色
     */
    private String color;
    /**
     * 目录
     */
    private String toc;
    /**
     * 类型
     */
    private Integer type;
    /**
     * 公开状态
     */
    private Integer openStatus;
    /**
     * 公开同步时间
     */
    private Date syncTime;
    /**
     * 公开时间
     */
    private Date openTime;
    /**
     * 公开版本
     */
    private Integer openVersion;
}
