package com.blossom.backend.server.folder.pojo;


import com.blossom.backend.server.folder.FolderTypeEnum;
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
    // @Min(value = 1, message = "文件夹的排序不能小于0")
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
     * 文件夹类型 {@link FolderTypeEnum}
     * <p>文件夹类型确认后无法修改
     *
     * @see com.blossom.backend.server.folder.FolderTypeEnum
     */
    @NotNull(message = "文件夹类型为必填项")
    @Min(value = 1, message = "文件夹类型必须为[1/2]")
    @Max(value = 2, message = "文件夹类型必须为[1/2]")
    private Integer type;
    /**
     * 是否新增到尾部, 将忽略传入的 sort, 使用最大 sort
     *
     * @since 1.10.0
     */
    private Boolean addToLast;
}
