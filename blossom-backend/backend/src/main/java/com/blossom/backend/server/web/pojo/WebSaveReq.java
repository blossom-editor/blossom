package com.blossom.backend.server.web.pojo;

import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 网站保存请求
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WebSaveReq extends AbstractPOJO {
    /**
     * ID 新增非必填, 修改必填
     */
    private Long id;
    /**
     * 网站名称
     */
    @NotBlank(message = "网站名称为必填项")
    private String name;
    /**
     * 网站链接
     */
    @NotBlank(message = "网站地址为必填项")
    private String url;
    /**
     * 网站图标
     */
    private String icon;
    /**
     * 网站图片
     */
    private String img;
    /**
     * 网站类型
     */
    @NotBlank(message = "网站类型为必填项")
    private String type;
    /**
     * 网站排序
     */
    @NotNull(message = "排序为必填项")
    private Integer sort;
    /**
     * 用户ID
     */
    private Long userId;
}
