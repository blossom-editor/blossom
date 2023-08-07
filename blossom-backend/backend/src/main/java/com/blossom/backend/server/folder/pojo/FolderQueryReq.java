package com.blossom.backend.server.folder.pojo;

import com.blossom.common.db.pojo.PageReq;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 文件夹查询请求
 *
 * @author xzzz
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FolderQueryReq extends PageReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tags;

    private Integer type;

    private Long userId;
}
