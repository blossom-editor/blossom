package com.blossom.backend.server.picture.pojo;

import com.blossom.common.db.pojo.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 图片查询请求
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PicturePageReq extends PageReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 文件夹ID */
    private Long pid;
    /** 原文件名 */
    private String sourceName;
    /** 文件名 */
    private String name;
    /** 文件路径 */
    private String pathName;
    /** 评分 {0,1,2,3,4,5} */
    private Integer rate;
    /** 收藏 0:否,1:是 */
    private Integer starStatus;
}
