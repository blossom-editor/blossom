package com.blossom.backend.base.search;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "project.search")
public class SearchProperties {

    private String path = "";
}
