package com.blossom.backend.thirdparty;

import com.blossom.backend.base.user.UserService;
import com.blossom.backend.base.user.pojo.UserEntity;
import com.blossom.backend.thirdparty.hefeng.WeatherManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
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
@Component
@RestController
@RequestMapping("/thirdparty/scheduled")
public class ThirdPartyScheduled {
    private static final Logger log = LoggerFactory.getLogger(ThirdPartyScheduled.class);

    @Autowired
    private WeatherManager weatherManager;

    @Autowired
    private UserService userService;

    /**
     * 天气
     *
     * @apiNote 每30分钟刷新, 请求会立即刷新
     */
    @RequestMapping("/weather")
    @Scheduled(cron = "0 0/30 * * * ?")
    public void refreshWeather() {
        log.debug("[BLOSSOM] 刷新天气");
        List<UserEntity> users = userService.listAll();
        Set<String> locations = users.stream().collect(Collectors.groupingBy(UserEntity::getLocation)).keySet();
        for (String location : locations) {
            weatherManager.clearAll(location);
            weatherManager.findWeatherAll(location);
        }
    }
}
