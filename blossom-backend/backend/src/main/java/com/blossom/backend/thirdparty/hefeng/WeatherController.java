package com.blossom.backend.thirdparty.hefeng;

import cn.hutool.core.util.StrUtil;
import com.blossom.backend.thirdparty.hefeng.pojo.WeatherRes;
import com.blossom.common.base.pojo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 和风天气
 *
 * @author xzzz
 */
@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private WeatherManager weatherManager;

    /**
     * 获取天气信息
     *
     * @param location 用户位置
     */
    @GetMapping
    public R<WeatherRes> weather(@RequestParam(value = "location", required = false) String location) {
        if (StrUtil.isBlank(location)) {
            return R.ok(new WeatherRes());
        }
        return R.ok(weatherManager.findWeatherAll(location));
    }

}
