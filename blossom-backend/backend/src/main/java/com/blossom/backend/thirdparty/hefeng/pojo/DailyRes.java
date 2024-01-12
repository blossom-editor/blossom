package com.blossom.backend.thirdparty.hefeng.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class DailyRes {

    private String code;

    private List<Daily> daily;

    /**
     * 天气预报
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Daily {
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
}
