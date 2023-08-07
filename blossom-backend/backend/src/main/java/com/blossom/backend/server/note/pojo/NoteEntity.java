package com.blossom.backend.server.note.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 便签
 *
 * @author xzzz
 */
@Data
@TableName("blossom_note")
@EqualsAndHashCode(callSuper = true)
public class NoteEntity extends AbstractPOJO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 内容
     */
    private String content;
    /**
     * 置顶
     */
    private Integer top;
    /**
     * 置顶时间
     */
    private Date topTime;
    /**
     * 创建时间
     */
    private Date creTime;
    /**
     * 用户ID
     */
    private Long userId;
}
