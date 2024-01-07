package com.blossom.backend.base.search;

import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;

/**
 * 全文搜索配置项
 */
@Component
public class SearchProperties {

    private static final String USER_HOME = "user.dir";

    /**
     * 根据用户ID, 获取对应索引库 Path
     *
     * @param userId 用户ID
     */
    public Path getUserIndexDirectory(Long userId) {
        File file = new File(addSeparator("/lucene/" + userId));
        return file.toPath();
    }

    public static String addSeparator(String dir) {
        if (!dir.endsWith(File.separator)) {
            dir += File.separator;
        }
        return System.getProperty(USER_HOME) + dir;
    }
}
