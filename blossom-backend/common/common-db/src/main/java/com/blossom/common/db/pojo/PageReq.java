package com.blossom.common.db.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分页参数
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageReq extends AbstractPOJO {

    /**
     * [分页参数] 页码, 0与1都表示第一页, 超过最大页时只显示最后一页
     */
    @TableField(exist = false)
    private int pageNum;
    /**
     * [分页参数] 每页结果数, 不传则为10, 最大为200, 超过200会自动替换为200
     */
    @TableField(exist = false)
    private int pageSize;
    /**
     * [分页参数] 排序字段, 需将驼峰类型字段改为下换线分隔字段,如 [userId > user_id]
     */
    @TableField(exist = false)
    private String sortField;
    /**
     * [分页参数] 排序方式 asc(升序) 或 desc(降序)
     */
    @TableField(exist = false)
    private String order;
}
