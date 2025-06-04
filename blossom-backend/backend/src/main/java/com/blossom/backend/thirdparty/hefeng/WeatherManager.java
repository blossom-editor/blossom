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
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    private static final String URL_CITY = "https://%s/geo/v2/city/lookup";
    private static final String URL_NOW = "https://%s/v7/weather/now";
    private static final String URL_DAILY = "https://%s/v7/weather/3d";
    private static final String URL_HOURLY = "https://%s/v7/weather/24h";
    private static final String SUCCESS = "200";

    /**
     *
     */
    private final Cache<String, WeatherRes> weatherCache = Caffeine.newBuilder()
            .initialCapacity(50)
            .expireAfterWrite(45, TimeUnit.MINUTES)
            .removalListener((String location, WeatherRes weather, RemovalCause cause) ->
                    log.info("Weather cache [" + location + "] has been deleted")
            )
            .build();

    @Autowired
    private ParamService paramService;

    /**
     * 查询天气信息
     */
    public WeatherRes findWeatherAll(String location) {
        WeatherRes cache = weatherCache.getIfPresent(location);
        if (cache != null) {
            log.debug("[BLOSSOM] get weather from cache: {}", location);
            return cache;
        }
        log.info("[BLOSSOM] refresh weather: {}", location);
        HeFengReq params = initParam(location);
        if (params == null) {
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
            cityStr = HttpUtil.get(String.format(URL_CITY, params.getHost()), params.getApiParam());
            if (StrUtil.isNotBlank(cityStr)) {
                city = JsonUtil.toObj(cityStr, CityRes.class);
            }

            nowStr = HttpUtil.get(String.format(URL_NOW, params.getHost()), params.getApiParam());
            if (StrUtil.isNotBlank(nowStr)) {
                now = JsonUtil.toObj(nowStr, NowRes.class);
            }

            dailyStr = HttpUtil.get(String.format(URL_DAILY, params.getHost()), params.getApiParam());
            if (StrUtil.isNotBlank(dailyStr)) {
                daily = JsonUtil.toObj(dailyStr, DailyRes.class);
            }

            hourlyStr = HttpUtil.get(String.format(URL_HOURLY, params.getHost()), params.getApiParam());
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
        weatherCache.put(location, weather);
        return weather;
    }

    /**
     * 清除缓存
     */
    public void clear(String location) {
        weatherCache.invalidate(location);
    }

    /**
     * 获取参数
     *
     * @return 返回查询参数
     */
    public HeFengReq initParam(String location) {
        Map<String, String> paramMap = paramService.selectMap(false, ParamEnum.HEFENG_KEY, ParamEnum.HEFENG_HOST);
        if (MapUtil.isNotEmpty(paramMap)
                && StrUtil.isNotBlank(paramMap.get(ParamEnum.HEFENG_KEY.name()))
                && StrUtil.isNotBlank(paramMap.get(ParamEnum.HEFENG_HOST.name()))
        ) {
            HeFengReq req = new HeFengReq();
            req.setHost(paramMap.get(ParamEnum.HEFENG_HOST.name()));

            Map<String, String> map = new HashMap<>(2);
            map.put("location", location);
            map.put("key", paramMap.get(ParamEnum.HEFENG_KEY.name()));
            req.setApiParam(map);
            return req;
        }
        return null;
    }
}
