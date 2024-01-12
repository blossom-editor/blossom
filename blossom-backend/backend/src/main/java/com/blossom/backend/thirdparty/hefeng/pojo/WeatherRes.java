package com.blossom.backend.thirdparty.hefeng.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 天气信息
 *
 * @author xzzz
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeatherRes {
    private CityRes.Location location;
    private NowRes.Now now;
    private List<HourlyRes.Hourly> hourly;
    private List<DailyRes.Daily> daily;
}
