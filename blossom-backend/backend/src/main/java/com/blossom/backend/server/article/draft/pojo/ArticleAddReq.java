package com.blossom.backend.server.article.draft.pojo;

import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 文章新增请求
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleAddReq extends AbstractPOJO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章所在的文件夹ID
     */
    @Min(value = 0, message = "[上级菜单ID] 不能小于0")
    @NotNull(message = "[上级菜单] 为必填项")
    private Long pid;
    /**
     * 文章名称
     */
    @NotBlank(message = "[文章名称] 为必填项")
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
     * 颜色
     */
    private String color;
    /**
     * 是否新增到尾部, 将忽略传入的 sort, 使用最大 sort
     *
     * @since 1.10.0
     */
    private Boolean addToLast;
}
