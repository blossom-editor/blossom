package com.blossom.backend.thirdparty.hefeng;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.blossom.backend.base.param.ParamEnum;
import com.blossom.backend.base.param.ParamService;
import com.blossom.backend.thirdparty.hefeng.pojo.*;
import com.blossom.common.base.exception.XzException400;
import com.blossom.common.base.util.json.JsonUtil;
import com.blossom.common.base.util.okhttp.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 天气查询
 * <p>cURL 查询方式:
 * <pre>{@code
 * curl -L -X GET --compressed 'https://geoapi.qweather.com/v2/city/lookup?location=101100101&key='
 * }</pre>
 *
 * @author : xzzz
 */
@Component
public class WeatherManager {
    private static final Logger log = LoggerFactory.getLogger(WeatherManager.class);

    private static final String URL_CITY = "https://geoapi.qweather.com/v2/city/lookup";
    private static final String URL_NOW = "https://devapi.heweather.net/v7/weather/now";
    private static final String URL_DAILY = "https://devapi.heweather.net/v7/weather/3d";
    private static final String URL_HOURLY = "https://devapi.heweather.net/v7/weather/24h";

    private static final String WEATHER_ALL = "weather_all";

    private static final String SUCCESS = "200";

    @Autowired
    private ParamService paramService;

    /**
     * 查询天气信息
     */
    @Cacheable(cacheNames = WEATHER_ALL, key = "'location_' + #location", unless = "#result == null")
    public WeatherRes findWeatherAll(String location) {
        Map<String, String> maps = initParam(location);
        if (maps == null) {
            log.info("未配置天气信息, 忽略天气查询");
            WeatherRes weather = new WeatherRes();
            CityRes.Location l = new CityRes.Location();
            l.setName("未配置天气");
            weather.setLocation(l);
            return weather;
        }
        String cityStr, nowStr, dailyStr, hourlyStr;
        CityRes city = null;
        NowRes now = null;
        DailyRes daily = null;
        HourlyRes hourly = null;
        try {
            cityStr = HttpUtil.get(URL_CITY, maps);
            if (StrUtil.isNotBlank(cityStr)) {
                city = JsonUtil.toObj(cityStr, CityRes.class);
            }

            nowStr = HttpUtil.get(URL_NOW, maps);
            if (StrUtil.isNotBlank(nowStr)) {
                now = JsonUtil.toObj(nowStr, NowRes.class);
            }

            dailyStr = HttpUtil.get(URL_DAILY, maps);
            if (StrUtil.isNotBlank(dailyStr)) {
                daily = JsonUtil.toObj(dailyStr, DailyRes.class);
            }

            hourlyStr = HttpUtil.get(URL_HOURLY, maps);
            if (StrUtil.isNotBlank(hourlyStr)) {
                hourly = JsonUtil.toObj(hourlyStr, HourlyRes.class);
            }
        } catch (Exception e) {
            throw new XzException400("获取天气信息失败:" + e.getMessage());
        }

        WeatherRes weather = new WeatherRes();

        if (city != null && city.getCode().equals(SUCCESS) && CollUtil.isNotEmpty(city.getLocation())) {
            weather.setLocation(city.getLocation().get(0));
        } else {
            log.error("获取城市信息失败, resp: {}", cityStr);
        }

        if (now != null && now.getCode().equals(SUCCESS)) {
            now.getNow().setIconValue(HeCondCode.getIcon(now.getNow().getIcon()));
            weather.setNow(now.getNow());
        } else {
            log.error("获取当前天气失败, resp: [{}]", nowStr);
        }

        if (daily != null && daily.getCode().equals(SUCCESS) && CollUtil.isNotEmpty(daily.getDaily()) && daily.getDaily().size() >= 3) {
            List<DailyRes.Daily> d3ds = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                DailyRes.Daily d3d = daily.getDaily().get(i);
                d3d.setIconValueDay(HeCondCode.getIcon(d3d.getIconDay()));
                d3d.setIconValueNight(HeCondCode.getIcon(d3d.getIconNight()));
                d3ds.add(d3d);
            }
            weather.setDaily(d3ds);
        } else {
            log.error("获取3日天气预报失败, resp: {}", cityStr);
        }

        if (hourly != null && hourly.getCode().equals(SUCCESS) && CollUtil.isNotEmpty(hourly.getHourly()) && hourly.getHourly().size() >= 1) {
            List<HourlyRes.Hourly> hours = new ArrayList<>();
            for (int i = 0; i < 1; i++) {
                HourlyRes.Hourly hour = hourly.getHourly().get(i);
                hour.setIconValue(HeCondCode.getIcon(hour.getIcon()));
                hours.add(hour);
            }
            weather.setHourly(hours);
        } else {
            log.error("获取小时预报失败, resp: {}", cityStr);
        }
        return weather;
    }

    /**
     * 清除缓存
     */
    @CacheEvict(cacheNames = WEATHER_ALL, key = "'location_' + #location")
    public void clearAll(String location) {

    }

    /**
     * 获取参数
     *
     * @return 返回查询参数
     */
    public Map<String, String> initParam(String location) {
        Map<String, String> paramMap = paramService.selectMap(false, ParamEnum.HEFENG_KEY);
        if (MapUtil.isNotEmpty(paramMap) && StrUtil.isNotBlank(paramMap.get(ParamEnum.HEFENG_KEY.name()))) {
            Map<String, String> map = new HashMap<>(2);
            map.put("location", location);
            map.put("key", paramMap.get(ParamEnum.HEFENG_KEY.name()));
            return map;
        }
        return null;
    }
}
