package com.blossom.backend.server.article;

import cn.hutool.core.util.StrUtil;

/**
 * 文档中有特殊意义的标签
 *
 * @author xzzz
 */
public enum TagEnum {
    /**
     * 标识该文件夹是个专题
     */
    subject,
    /**
     * 标识该文章是个目录
     */
    toc,
    ;

    public static boolean isSubject(String tags) {
        if (StrUtil.isBlank(tags)) {
            return false;
        }
        return tags.toLowerCase().contains(TagEnum.subject.name());
    }

    public static boolean isToc(String tags) {
        if (StrUtil.isBlank(tags)) {
            return false;
        }
        return tags.toLowerCase().contains(TagEnum.toc.name());
    }
}
