package com.blossom.backend.thirdparty;

import cn.hutool.core.util.StrUtil;
import com.blossom.backend.base.user.UserService;
import com.blossom.backend.base.user.pojo.UserEntity;
import com.blossom.backend.thirdparty.hefeng.WeatherManager;
import com.blossom.common.base.pojo.R;
import com.blossom.expand.tracker.core.adapter.aspect.TrackerStart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 三方接口定时任务
 *
 * @author : xzzz
 */
@RestController
@RequestMapping("/thirdparty/scheduled")
public class ThirdPartyScheduled {

    @Autowired
    private WeatherManager weatherManager;

    @Autowired
    private UserService userService;

    /**
     * 刷新天气缓存
     *
     * @apiNote 每30分钟刷新, 请求会立即刷新
     */
    @TrackerStart
    @PostMapping("/weather")
    @Scheduled(cron = "0 0/30 * * * ?")
    public R<?> refreshWeather() {
        List<UserEntity> users = userService.listAll();
        Set<String> locations = users.stream().collect(Collectors.groupingBy(UserEntity::getLocation)).keySet();
        for (String location : locations) {
            if (StrUtil.isBlank(location)) {
                continue;
            }
            weatherManager.clear(location);
            weatherManager.findWeatherAll(location);
        }
        return R.ok();
    }
}
