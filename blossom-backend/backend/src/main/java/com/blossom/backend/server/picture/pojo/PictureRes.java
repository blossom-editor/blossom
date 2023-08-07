package com.blossom.backend.server.picture.pojo;

import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 图片响应
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PictureRes extends AbstractPOJO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;
    /** 文件夹ID */
    private Long pid;
    /** 文件名 */
    private String name;
    /** 文件路径 */
    private String pathName;
    /** 文件访问url */
    private String url;
    /** 收藏 0:否,1:是 */
    private Integer starStatus;
    /** 文件大小 */
    private Long size;
    /** 创建日期 */
    private Date creTime;
    /**
     * 使用了该图片的文章名称
     */
    private String articleNames;
}
