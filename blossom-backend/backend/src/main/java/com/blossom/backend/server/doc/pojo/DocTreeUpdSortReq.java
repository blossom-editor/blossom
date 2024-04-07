package com.blossom.backend.server.doc.pojo;

import com.blossom.backend.server.doc.DocTypeEnum;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 修改文档排序
 *
 * @since 1.14.0
 */
@Data
public class DocTreeUpdSortReq {

    /**
     * 需要修改排序的文档列表
     */
    private List<Doc> docs;
    /**
     * 文件夹类型 1:文章文件夹; 2:图片文件夹
     *
     * @see com.blossom.backend.server.folder.FolderTypeEnum
     */
    @NotNull(message = "文件夹类型为必填项")
    @Min(value = 1, message = "文件夹类型错误")
    @Max(value = 2, message = "文件夹类型错误")
    private Integer folderType;
    /**
     * [Picture + Article] 只查询图片文件夹, 以及含有图片的文章文件夹
     */
    @NotNull(message = "文件夹类型为必填项")
    private Boolean onlyPicture;

    /**
     * 获取查询的文件夹类型
     *
     * @return 返回结果不会为 null
     */
    public Boolean getOnlyPicture() {
        if (onlyPicture == null) {
            return false;
        }
        return onlyPicture;
    }

    @Data
    public static class Doc {
        /**
         * id
         */
        private Long i;
        /**
         * 父id
         */
        private Long p;
        /**
         * 名称, 文件夹名称或文章名称
         */
        private String n;
        /**
         * 排序
         */
        private Integer s;
        /**
         * 类型 {@link DocTypeEnum}
         *
         * @see com.blossom.backend.server.doc.DocTypeEnum
         */
        private Integer ty;
        /**
         * 存储路径
         */
        private String sp;
    }


}
