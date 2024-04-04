package com.blossom.backend.server.folder.pojo;

import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 文件夹请求
 * <p>通常使用在 save 接口
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FolderUpdReq extends AbstractPOJO implements Serializable {

    private static final long serialVersionUID = 1L;
    /** id */
    @NotNull(message = "[文件夹ID] 为必填项")
    private Long id;
    /** 上级菜单ID */
    @Min(value = 0, message = "[上级菜单ID] 不能小于0")
    @NotNull(message = "[上级菜单] 为必填项")
    private Long pid;
    /** 文件夹名称 */
    @NotBlank(message = "[文件夹名称] 为必填项")
    private String name;
    /** 图标 */
    private String icon;
    /**
     * star状态
     */
    private Integer starStatus;
    /** 标签 */
    private List<String> tags;
    /** 排序 */
    @NotNull(message = "排序为必填项")
    // @Min(value = 0, message = "排序不能小于0")
    private Integer sort;
    /** 封面图片 */
    private String cover;
    /** 备注 */
    private String describes;
    /** 存储地址 */
    @NotBlank(message = "存储地址为必填项, 如不指定请使用 \"/\"")
    private String storePath;
    /**
     * 颜色
     */
    private String color;
}
