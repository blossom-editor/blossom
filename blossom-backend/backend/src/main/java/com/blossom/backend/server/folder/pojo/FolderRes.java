package com.blossom.backend.server.folder.pojo;

import com.blossom.backend.server.folder.FolderTypeEnum;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 文件夹响应
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FolderRes extends AbstractPOJO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
    /**
     * 父id
     */
    private Long pid;
    /**
     * 文件夹名称
     */
    private String name;
    /**
     * 图标
     */
    private String icon;
    /**
     * 标签
     */
    private List<String> tags;
    /**
     * star状态
     */
    private Integer starStatus;
    /**
     * 是否公开文件夹 [0:未公开，1:公开]
     */
    private Integer openStatus;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 封面图片
     */
    private String cover;
    /**
     * 颜色
     */
    private String color;
    /**
     * 备注
     */
    private String describes;
    /**
     * 存储地址
     */
    private String storePath;
    /**
     * 专题字数
     */
    private Integer subjectWords;
    /**
     * 专题的最后修改时间
     */
    private Date subjectUpdTime;
    /**
     * 文件夹类型 {@link FolderTypeEnum}
     * @see FolderTypeEnum
     */
    private Integer type;
    /**
     * 创建时间
     */
    private Date creTime;
    /**
     * 修改时间
     */
    private Date updTime;
}
