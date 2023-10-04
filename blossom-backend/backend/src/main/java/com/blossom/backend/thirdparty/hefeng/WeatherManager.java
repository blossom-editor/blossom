package com.blossom.backend.thirdparty.hefeng;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.blossom.backend.base.param.ParamEnum;
import com.blossom.backend.base.param.ParamService;
import com.blossom.common.base.exception.XzException400;
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

    @Autowired
    private ParamService paramService;

    /**
     * 查询天气信息
     */
    @Cacheable(cacheNames = WEATHER_ALL, key = "'location_' + #location", unless = "#result == null")
    public HeWeatherDTO findWeatherAll(String location) {
        Map<String, Object> maps = initParam(location);
        if (maps == null) {
            log.info("未配置天气信息, 忽略天气查询");
            return null;
        }
        HttpResponse city;
        HttpResponse now;
        HttpResponse daily;
        HttpResponse hourly;
        try {
            city = HttpRequest.get(URL_CITY).form(maps).execute();
            now = HttpRequest.get(URL_NOW).form(maps).execute();
            daily = HttpRequest.get(URL_DAILY).form(maps).execute();
            hourly = HttpRequest.get(URL_HOURLY).form(maps).execute();
        } catch (Exception e) {
            throw new XzException400("获取天气信息失败:" + e.getMessage());
        }

        JSONObject locationJson = JSONUtil.parseObj(city.body());
        JSONObject nowJson = JSONUtil.parseObj(now.body());
        JSONObject dailyJson = JSONUtil.parseObj(daily.body());
        JSONObject hourlyJson = JSONUtil.parseObj(hourly.body());

        HeWeatherDTO weather = new HeWeatherDTO();

        if ("200".equals(locationJson.getStr("code"))) {
            List<HeWeatherDTO.Location> los = JSONUtil.toList(locationJson.getJSONArray("location"), HeWeatherDTO.Location.class);
            if (CollUtil.isNotEmpty(los)) {
                weather.setLocation(los.get(0));
            }
        } else {
            log.error("获取城市信息失败,body : [{}]", locationJson.toString());
        }

        // 1. 当前时间
        if ("200".equals(nowJson.getStr("code"))) {
            HeWeatherDTO.Now heNow = JSONUtil.toBean(nowJson.getJSONObject("now"), HeWeatherDTO.Now.class);
            heNow.setIconValue(HeCondCode.getIcon(heNow.getIcon()));
            weather.setNow(heNow);
        } else {
            log.error("获取当天气失败,body : [{}]", nowJson.toString());
        }

        // 2. 天气预报
        if ("200".equals(dailyJson.getStr("code"))) {
            List<HeWeatherDTO.Daily> daily3D = JSONUtil.toList(dailyJson.getJSONArray("daily"), HeWeatherDTO.Daily.class);
            for (int i = 0; i < 3; i++) {
                daily3D.get(i).setIconValueDay(HeCondCode.getIcon(daily3D.get(i).getIconDay()));
                daily3D.get(i).setIconValueNight(HeCondCode.getIcon(daily3D.get(i).getIconNight()));
            }
            weather.setDaily(daily3D);
        } else {
            log.error("获取3日天气预报失败,body : [{}]", dailyJson.toString());
        }

        // 3. 小时预报
        if ("200".equals(hourlyJson.getStr("code"))) {
            List<HeWeatherDTO.Hourly> originalHourly = JSONUtil.toList(hourlyJson.getJSONArray("hourly"), HeWeatherDTO.Hourly.class);
            List<HeWeatherDTO.Hourly> newHourly = new ArrayList<>();
            newHourly.add(originalHourly.get(0));
            for (int i = 0; i < 1; i++) {
                newHourly.get(i).setIconValue(HeCondCode.getIcon(newHourly.get(i).getIcon()));
            }
            weather.setHourly(newHourly);
        } else {
            log.error("获取小时预报失败,body : [{}]}", hourlyJson.toString());
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
    public Map<String, Object> initParam(String location) {
        Map<String, String> paramMap = paramService.selectMap(false, ParamEnum.HEFENG_KEY);
        if (MapUtil.isNotEmpty(paramMap) && StrUtil.isBlank(paramMap.get(ParamEnum.HEFENG_KEY.name()))) {
            Map<String, Object> map = new HashMap<>(2);
            map.put("location", location);
            map.put("key", paramMap.get(ParamEnum.HEFENG_KEY.name()));
            return map;
        }
        return null;
    }
}
