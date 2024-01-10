package com.blossom.backend.thirdparty.hefeng.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 当前天气
 */
@Data
public class NowRes {

    private String code;

    private Now now;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Now {
        /**
         * 实况观测时间
         */
        private String obsTime;
        /**
         * 实况温度，默认单位：摄氏度
         */
        private String temp;
        /**
         * 实况体感温度，默认单位：摄氏度
         */
        private String feelsLike;
        /**
         * 当前天气状况和图标的代码
         */
        private String icon;
        private String iconValue;
        /**
         * 实况天气状况的文字描述，包括阴晴雨雪等天气状态的描述
         */
        private String text;
        /**
         * 实况风向360角度
         */
        private String wind360;
        /**
         * 实况风向
         */
        private String windDir;
        /**
         * 实况风力等级
         */
        private String windScale;
        /**
         * 实况风速，公里/小时
         */
        private String windSpeed;
        /**
         * 实况相对湿度，百分比数值
         */
        private String humidity;
        /**
         * 实况降水量，默认单位：毫米
         */
        private String precip;
        /**
         * 实况大气压强，默认单位：百帕
         */
        private String pressure;
        /**
         * 实况能见度，默认单位：公里
         */
        private String vis;
        /**
         * 实况云量，百分比数值
         */
        private String cloud;
        /**
         * 实况露点温度
         */
        private String dew;

    }
}
