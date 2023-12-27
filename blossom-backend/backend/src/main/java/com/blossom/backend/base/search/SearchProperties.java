package com.blossom.backend.base.search;

import cn.hutool.core.convert.Convert;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Data
@Configuration
@ConfigurationProperties(prefix = "project.search")
public class SearchProperties {

    private String path = "";

    /**
     * 根据用户id， 获取对应索引库path
     * @param userId 用户id
     * @return 索引库path
     */
    public Path getUserIndexDirectory(Long userId){
        return Paths.get(this.path, Convert.toStr(userId));
    }
}
