package com.blossom.backend.server.article.backup.pojo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DownloadReq {

    /**
     *
     */
    @NotBlank(message = "文件名必填项")
    private String filename;
}
