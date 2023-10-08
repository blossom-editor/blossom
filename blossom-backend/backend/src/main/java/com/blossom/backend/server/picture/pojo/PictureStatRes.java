package com.blossom.backend.server.picture.pojo;

import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 图片统计
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PictureStatRes extends AbstractPOJO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 图片个数
     */
    private Long pictureCount;
    /**
     * 图片大小 byte
     */
    private Long pictureSize;
}
