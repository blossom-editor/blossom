package com.blossom.backend.server.doc.pojo;

import com.blossom.common.base.pojo.AbstractPOJO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文档树状列表查询请求
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DocTreeReq extends AbstractPOJO {
    /**
     * [Folder] 只查询文件夹, 包含文章文件夹和图片文件夹
     */
    private Boolean onlyFolder;
    /**
     * [Article] 只查询公开的文章和文件夹
     */
    private Boolean onlyOpen;
    /**
     * [Article] 只查询专题文章和文件夹
     */
    private Boolean onlySubject;
    /**
     * [Article] 只查询 star 文章和文件夹
     */
    private Boolean onlyStar;
    /**
     * [Article] 文章ID, 指查询指定文章
     */
    private Long articleId;
    /**
     * [Picture + Article] 只查询图片文件夹, 以及含有图片的文章文件夹
     */
    private Boolean onlyPicture;
    /**
     * 用户ID
     */
    private Long userId;

    public Boolean getOnlyFolder() {
        if (onlyFolder == null) {
            return false;
        }
        return onlyFolder;
    }

    public Boolean getOnlyPicture() {
        if (onlyPicture == null) {
            return false;
        }
        return onlyPicture;
    }

    public Boolean getOnlyOpen() {
        if (onlyOpen == null) {
            return false;
        }
        return onlyOpen;
    }

    public Boolean getOnlySubject() {
        if (onlySubject == null) {
            return false;
        }
        return onlySubject;
    }

    public Boolean getOnlyStar() {
        if (onlyStar == null) {
            return false;
        }
        return onlyStar;
    }
}
