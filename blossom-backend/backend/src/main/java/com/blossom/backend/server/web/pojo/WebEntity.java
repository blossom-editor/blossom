package com.blossom.backend.server.web.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 网站收藏
 *
 * @author xzzz
 */
@Data
@TableName("blossom_web")
@EqualsAndHashCode(callSuper = true)
public class WebEntity extends AbstractPOJO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 网站名称
     */
    private String name;
    /**
     * 网站链接
     */
    private String url;
    /**
     * 图标
     */
    private String icon;
    /**
     * 图片, 图片的优先级比图标高
     */
    private String img;
    /**
     * 类型
     */
    private String type;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 创建时间
     */
    private Date creTime;

    private Long userId;
}

