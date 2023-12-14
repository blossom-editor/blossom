package com.blossom.backend.server.picture.pojo;

import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片统计
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PictureDelBatchRes extends AbstractPOJO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 删除成功的图片数量
     */
    private Integer success;

    /**
     * 成功删除的图片ID
     */
    private List<Long> successIds;

    /**
     * 删除失败的图片数量
     */
    private Integer fault;

    /**
     * 使用中的图片数量
     */
    private Integer inuse;

    public static PictureDelBatchRes build() {
        PictureDelBatchRes result = new PictureDelBatchRes();
        result.setSuccess(0);
        result.setFault(0);
        result.setInuse(0);
        result.setSuccessIds(new ArrayList<>());
        return result;
    }

    public void incSuccess() {
        if (this.success == null) {
            this.success = 1;
        }
        this.success++;
    }

    public void incFault() {
        if (this.fault == null) {
            this.fault = 1;
        }
        this.fault++;
    }

    public void incInuse() {
        if (this.inuse == null) {
            this.inuse = 1;
        }
        this.inuse++;
    }
}
