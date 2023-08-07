package com.blossom.backend.thirdparty.hefeng;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 天气信息
 *
 * @author xzzz
 */
@Data
@SuppressWarnings("all")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HeWeatherDTO {

    private Location location;
    private Now now;
    private List<Hourly> hourly;
    private List<Daily> daily;

    /**
     * 位置
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class Location {
        /**
         * 地区-城市名称
         */
        private String name;
    }

    /**
     * 当前天气
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class Now {
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

    /**
     * 天气预报
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class Daily {
        /**
         * 预报日期2013-12-30
         */
        private String fxDate;
        /**
         * 预报当天最高温度
         */
        private String tempMax;
        /**
         * 预报当天最低温度
         */
        private String tempMin;
        private String iconDay;
        private String iconValueDay;
        private String textDay;
        private String iconNight;
        private String iconValueNight;
        private String textNight;
    }

    /**
     * 逐小时预报
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class Hourly {
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
