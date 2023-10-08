package com.blossom.backend.server.doc.pojo;

import com.blossom.backend.server.doc.DocTypeEnum;
import com.blossom.common.base.enums.YesNo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 文档树状列表查询响应, 为减少网络消耗使用字段简称
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode
public class DocTreeRes implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long i;
    /**
     * 父id
     */
    @JsonInclude
    private Long p;
    /**
     * 名称, 文件夹名称或文章名称
     */
    private String n;
    /**
     * open, 公开状态
     */
    private Integer o;
    /**
     * 版本有差异 version different
     */
    private Integer vd;
    /**
     * 图标
     */
    private String icon;
    /**
     * 标签, 字符串数组
     */
    private List<String> t;
    /**
     * 排序
     */
    private Integer s;
    /**
     * 是否收藏 {@link YesNo}
     * @see com.blossom.common.base.enums.YesNo
     */
    private Integer star;
    /**
     * 存储地址 storage path
     */
    private String sp;
    /**
     * 类型 {@link DocTypeEnum}
     * @see com.blossom.backend.server.doc.DocTypeEnum
     */
    private Integer ty;
    /**
     * 子菜单
     */
    private List<DocTreeRes> children;
}
