package com.blossom.backend.thirdparty.hefeng.pojo;

import lombok.Data;

import java.util.Map;

/**
 * 和风天气请求接口
 */
@Data
public class HeFengReq {

    private Boolean enabled;
    /**
     * 和风用户域名
     */
    private String host;
    /**
     * 和风接口请求参数
     */
    private Map<String,String> apiParam;

}
