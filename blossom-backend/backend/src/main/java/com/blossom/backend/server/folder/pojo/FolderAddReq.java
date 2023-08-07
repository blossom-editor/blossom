package com.blossom.backend.server.folder.pojo;


import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 文件夹新增参数
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FolderAddReq extends AbstractPOJO {

    private static final long serialVersionUID = 1L;

    /**
     * 父ID
     */
    @Min(value = 0, message = "[上级菜单ID] 不能小于0")
    @NotNull(message = "[上级菜单] 为必填项")
    private Long pid;
    /**
     * 文件夹名称
     */
    @NotBlank(message = "[文件夹名称] 为必填项")
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
     * 排序
     */
    @NotNull(message = "排序为必填项")
    @Min(value = 1, message = "排序不能小于0")
    private Integer sort;
    /**
     * 封面图片
     */
    private String cover;
    /**
     * 备注
     */
    private String describes;
    /**
     * 存储地址
     */
    @NotBlank(message = "存储地址为必填项, 如不指定请使用 \"/\"")
    private String storePath;
    /**
     * 颜色
     */
    private String color;
    /**
     * 文件夹类型, 1:文章文件夹; 2:图片文件夹;
     * <p>文件夹类型确认后无法修改
     *
     * @see com.blossom.backend.server.FolderTypeEnum
     */
    @NotNull(message = "文件夹类型为必填项")
    @Min(value = 1, message = "文件夹类型必须为[1/2]")
    @Max(value = 2, message = "文件夹类型必须为[1/2]")
    private Integer type;
}
