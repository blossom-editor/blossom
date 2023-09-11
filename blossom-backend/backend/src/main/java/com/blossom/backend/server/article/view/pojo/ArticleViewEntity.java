package com.blossom.backend.server.article.view.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;


/**
 * 文章访问记录
 *
 * @author xzzz
 */
@Data
@TableName("blossom_article_view")
@EqualsAndHashCode(callSuper = true)
public class ArticleViewEntity extends AbstractPOJO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @TableId
    private Integer id;
    /** 文章ID */
    private Long articleId;
    /** 事件类型 1:uv; 2:like */
    private Integer type;
    /** 地址  */
    private String ip;
    /** 设备 */
    private String userAgent;
    /**
     * 国家
     */
    private String country;
    /** 省 */
    private String province;
    /** 市 */
    private String city;
    /** 日期 yyyy-MM-dd */
    private Date creDay;
    /** 日期 */
    private Date creTime;
}
