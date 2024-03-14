package com.blossom.backend.server.folder.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.blossom.backend.server.folder.FolderTypeEnum;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 文件夹
 *
 * @author xzzz
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("blossom_folder")
public class FolderEntity extends AbstractPOJO implements Serializable {

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
    private String tags;
    /**
     * 开放状态
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
     * 存储地址, 以 / 开头, 以 / 结尾, 保存时会进行格式校验
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
     * 文件夹类型, 1:文章文件夹; 2:图片文件夹;
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
    /**
     * 用户ID
     */
    private Long userId;

    //region ============================== 非数据库字段 ==============================

    /**
     * ID 集合
     */
    @TableField(exist = false)
    private List<Long> ids;

    //endregion

}
