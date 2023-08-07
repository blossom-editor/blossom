package com.blossom.backend.thirdparty.gitee;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.blossom.backend.base.param.ParamEnum;
import com.blossom.backend.base.param.ParamService;
import com.blossom.backend.thirdparty.gitee.pojo.Events;
import com.blossom.backend.thirdparty.gitee.pojo.HeatmapRes;
import com.blossom.common.base.exception.XzException404;
import com.blossom.common.base.util.json.JsonUtil;
import com.blossom.common.base.util.okhttp.HttpUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 码云服务
 *
 * @author jasmine
 */
@Slf4j
@Service
public class GiteeManager {
    private static final String CACHE_HEATMAP = "cache_heatmap";
    private static final Map<String, HeatmapRes> CACHE_MAP = new HashMap<>();

    private static final String EVENTS_URL = "https://gitee.com/api/v5/users/jasminexz/events?access_token=%s&limit=50&prev_id=%s";

    private static String accessToken;

    @Autowired
    private ParamService paramService;

    /**
     * 获取事件列表, 用于获取事件发生时间, 并构造成日历热力图
     * <ul>
     * <li>码云: 查询3个月内的事件, 并按天分组</li>
     * </ul>
     *
     * @return 热力图
     */
    public HeatmapRes heatmap() {
        if (checkToken()) {
            log.warn("未配置GITEE信息");
            return new HeatmapRes();
        }

        HeatmapRes cache = CACHE_MAP.get(CACHE_HEATMAP);
        if (cache != null) {
            return cache;
        }

        DateTime today = DateUtil.date();
        DateTime thisMonthLastDay = DateUtil.endOfMonth(today);
        DateTime pass3Month = DateUtil.offsetMonth(today, -2);
        DateTime pass3MonthFirstDay = DateUtil.beginOfMonth(pass3Month);
        // 3个月内的 + 本月的
        List<DateTime> dateRange = DateUtil.rangeToList(pass3MonthFirstDay, thisMonthLastDay, DateField.DAY_OF_MONTH);

        List<Events> all = new ArrayList<>();
        // 分页循环查询动态，知道查询到3个月之前的数据则停止
        int prev_id = 0;
        while (true) {
            String url = String.format(EVENTS_URL, accessToken, prev_id);
            List<Events> res = JsonUtil.toObj(getDate(url), new TypeReference<List<Events>>() {});
            if (CollUtil.isEmpty(res)) {
                break;
            }
            all.addAll(res);
            Integer id = res.get(res.size() - 1).getId();
            Date lastIndexDate = DateUtil.parseDate(res.get(res.size() - 1).getCreated_at());
            if (DateUtil.compare(lastIndexDate, pass3MonthFirstDay) < 0) {
                break;
            }
            prev_id = id;
        }

        Map<String, List<Events>> maps = all.stream().peek(doc -> {
            Date date = DateUtil.parse(doc.getCreated_at(), "yyyy-MM-dd");
            doc.setCreated_at(DateUtil.format(date, "yyyy-MM-dd"));
        }).collect(Collectors.groupingBy(Events::getCreated_at));

        List<Object[]> resultList = new ArrayList<>();
        int maxUpdateNum = 0;
        for (DateTime dateTime : dateRange) {
            String ymdDate = dateTime.toString("yyyy-MM-dd");
            List<?> list = maps.get(ymdDate);
            int size = list == null ? 0 : list.size();
            if (size > maxUpdateNum) {
                maxUpdateNum = size;
            }
            resultList.add(new Object[]{ymdDate, size});
        }

        HeatmapRes res = new HeatmapRes();
        res.setDateBegin(pass3MonthFirstDay.toString("yyyy-MM-dd"));
        res.setDateEnd(thisMonthLastDay.toString("yyyy-MM-dd"));
        res.setMaxUpdateNum(maxUpdateNum);
        res.setData(resultList);

        CACHE_MAP.put(CACHE_HEATMAP, res);

        return res;
    }

    public void clearCache() {
        CACHE_MAP.put(CACHE_HEATMAP, null);
    }

    private String getDate(String url) {
        String body = HttpUtil.get(url);
        if (StrUtil.isBlank(body)) {
            throw new XzException404("未获取到响应数据");
        }
        return body;
    }

    private boolean checkToken() {
        Map<String, String> paramMap = paramService.selectMap(false, ParamEnum.GITEE_ACCESS_TOKEN);
        if (MapUtil.isNotEmpty(paramMap)) {
            accessToken = paramMap.get(ParamEnum.GITEE_ACCESS_TOKEN.name());
        }
        return StrUtil.isBlank(accessToken);
    }
}
