package com.blossom.backend.server.picture.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 图片
 *
 * @author xzzz
 */
@Data
@TableName("blossom_picture")
@EqualsAndHashCode(callSuper = true)
public class PictureEntity extends AbstractPOJO implements Serializable {

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
     * 原文件名
     */
    private String sourceName;
    /**
     * 文件名
     */
    private String name;
    /**
     * 文件路径
     * linux: /usr/local/blossom/abc.png
     * os:
     */
    private String pathName;
    /**
     * 文件访问url
     * http://localhost:9999/picture/usr/local/blossom/abc.png
     */
    private String url;
    /**
     * 评分 {0,1,2,3,4,5}
     */
    private Integer rate;
    /**
     * 收藏 0:否,1:是
     */
    private Integer starStatus;
    /**
     * 文件后缀
     */
    private String suffix;
    /**
     * 文件大小
     */
    private Long size;
    /**
     * 创建日期
     */
    private Date creTime;
    /**
     * 用户ID
     */
    private Long userId;


    //region ============================== 非数据库字段 ==============================

    /**
     * 该图片被哪些文章引用
     */
    @TableField(exist = false)
    private List<String> referenceArticle;

    /**
     * 使用了该图片的文章名称
     */
    @TableField(exist = false)
    private String articleNames;

    /**
     * 通过访问路径查询批量查询
     */
    @TableField(exist = false)
    private List<String> urls;
    //endregion
}
