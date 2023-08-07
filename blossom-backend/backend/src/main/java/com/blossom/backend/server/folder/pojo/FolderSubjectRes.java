package com.blossom.backend.server.folder.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 专题列表
 *
 * @author xzzz
 */
@Data
public class FolderSubjectRes implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;
    /**
     * 目录文章的ID
     */
    private Long tocId;
    /**
     * 文件夹名称
     */
    private String name;
    /**
     * 颜色
     */
    private String color;
    /**
     * 封面
     */
    private String cover;
    /**
     * 图标
     */
    private String icon;
    /**
     * 备注
     */
    private String describes;
    /**
     * 专题字数
     */
    private Integer subjectWords;
    /**
     * 专题的最后修改时间
     */
    private Date subjectUpdTime;
}
