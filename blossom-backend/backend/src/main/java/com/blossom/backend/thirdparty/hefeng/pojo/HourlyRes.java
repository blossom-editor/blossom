package com.blossom.backend.thirdparty.hefeng.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class HourlyRes {

    private String code;

    private List<Hourly> hourly;

    /**
     * 逐小时预报
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Hourly {
        /**
         * 逐小时预报时间
         */
        private String fxTime;
        /**
         * 逐小时预报温度
         */
        private String temp;
        /**
         * 逐小时预报天气状况图标代码
         */
        private String icon;
        /**
         * 图标
         */
        private String iconValue;
        /**
         * 逐小时预报天气状况文字描述
         */
        private String text;
    }
}
