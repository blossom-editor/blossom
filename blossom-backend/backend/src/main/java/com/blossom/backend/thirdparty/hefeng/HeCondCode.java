package com.blossom.backend.thirdparty.hefeng;

import cn.hutool.core.util.StrUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * <a href="https://dev.qweather.com/docs/resource/icons/">图标地址</a>
 *
 * @author xzzz
 */
@SuppressWarnings("all")
public class HeCondCode {

    private static final Map<String,Node> WEATHER_CODE_MAP = new HashMap<>(100);

    static {
        WEATHER_CODE_MAP.put("100",new Node("#wt-qing",         "晴"));
        WEATHER_CODE_MAP.put("101",new Node("#wt-yin",          "多云"));
        WEATHER_CODE_MAP.put("102",new Node("#wt-yin",          "少云"));
        WEATHER_CODE_MAP.put("103",new Node("#wt-yin",          "晴间多云"));
        WEATHER_CODE_MAP.put("104",new Node("#wt-yin",          "阴"));
        WEATHER_CODE_MAP.put("150",new Node("#wt-qing",         "晴"));
        WEATHER_CODE_MAP.put("151",new Node("#wt-yin",          "多云"));
        WEATHER_CODE_MAP.put("152",new Node("#wt-yin",          "少云"));
        WEATHER_CODE_MAP.put("153",new Node("#wt-yin",          "晴间多云"));
        WEATHER_CODE_MAP.put("154",new Node("#wt-yin",          "阴"));
        WEATHER_CODE_MAP.put("200",new Node("#wt-feng",         "有风"));
        WEATHER_CODE_MAP.put("201",new Node("#wt-qing",         "平静"));
        WEATHER_CODE_MAP.put("202",new Node("#wt-qing",         "微风"));
        WEATHER_CODE_MAP.put("203",new Node("#wt-qing",         "和风"));
        WEATHER_CODE_MAP.put("204",new Node("#wt-qing",         "清风"));
        WEATHER_CODE_MAP.put("205",new Node("#wt-feng",         "强风劲风"));
        WEATHER_CODE_MAP.put("206",new Node("#wt-feng",         "疾风"));
        WEATHER_CODE_MAP.put("207",new Node("#wt-feng",         "大风"));
        WEATHER_CODE_MAP.put("208",new Node("#wt-feng",         "烈风"));
        WEATHER_CODE_MAP.put("209",new Node("#wt-feng",         "风暴"));
        WEATHER_CODE_MAP.put("210",new Node("#wt-feng",         "狂爆风"));
        WEATHER_CODE_MAP.put("211",new Node("#wt-feng",         "飓风"));
        WEATHER_CODE_MAP.put("212",new Node("#wt-feng",         "龙卷风"));
        WEATHER_CODE_MAP.put("213",new Node("#wt-feng",         "热带风暴"));
        WEATHER_CODE_MAP.put("300",new Node("#wt-yu",           "阵雨"));
        WEATHER_CODE_MAP.put("301",new Node("#wt-yu",           "强阵雨"));
        WEATHER_CODE_MAP.put("302",new Node("#wt-zhongyu",      "雷阵雨"));
        WEATHER_CODE_MAP.put("303",new Node("#wt-zhongyu",      "强雷阵雨"));
        WEATHER_CODE_MAP.put("304",new Node("#wt-zhongyu",      "阵雨冰雹"));
        WEATHER_CODE_MAP.put("305",new Node("#wt-yu",           "小雨"));
        WEATHER_CODE_MAP.put("306",new Node("#wt-yu",           "中雨"));
        WEATHER_CODE_MAP.put("307",new Node("#wt-zhongyu",      "大雨"));
        WEATHER_CODE_MAP.put("308",new Node("#wt-zhongyu",      "极端降雨"));
        WEATHER_CODE_MAP.put("309",new Node("#wt-yu    ",       "毛毛细雨"));
        WEATHER_CODE_MAP.put("310",new Node("#wt-zhongyu",      "暴雨"));
        WEATHER_CODE_MAP.put("311",new Node("#wt-zhongyu",      "大暴雨"));
        WEATHER_CODE_MAP.put("312",new Node("#wt-zhongyu",      "特大暴雨"));
        WEATHER_CODE_MAP.put("313",new Node("#wt-zhongyu",      "冻雨"));
        WEATHER_CODE_MAP.put("314",new Node("#wt-zhongyu",      "小到中雨"));
        WEATHER_CODE_MAP.put("315",new Node("#wt-zhongyu",      "中到大雨"));
        WEATHER_CODE_MAP.put("316",new Node("#wt-zhongyu",      "大到暴雨"));
        WEATHER_CODE_MAP.put("317",new Node("#wt-zhongyu",      "暴到大暴雨"));
        WEATHER_CODE_MAP.put("318",new Node("#wt-zhongyu",      "大暴特暴雨"));
        WEATHER_CODE_MAP.put("399",new Node("#wt-yu",           "雨"));
        WEATHER_CODE_MAP.put("400",new Node("#wt-xue",          "小雪"));
        WEATHER_CODE_MAP.put("401",new Node("#wt-xue",          "中雪"));
        WEATHER_CODE_MAP.put("402",new Node("#wt-xue",          "大雪"));
        WEATHER_CODE_MAP.put("403",new Node("#wt-xue",          "暴雪"));
        WEATHER_CODE_MAP.put("404",new Node("#wt-xue",          "雨夹雪"));
        WEATHER_CODE_MAP.put("405",new Node("#wt-xue",          "雨雪天气"));
        WEATHER_CODE_MAP.put("406",new Node("#wt-xue",          "阵雨夹雪"));
        WEATHER_CODE_MAP.put("407",new Node("#wt-xue",          "阵雪"));
        WEATHER_CODE_MAP.put("408",new Node("#wt-xue",          "小到中雪"));
        WEATHER_CODE_MAP.put("409",new Node("#wt-xue",          "中到大雪"));
        WEATHER_CODE_MAP.put("410",new Node("#wt-xue",          "大到暴雪"));
        WEATHER_CODE_MAP.put("499",new Node("#wt-xue",          "雪"));
        WEATHER_CODE_MAP.put("500",new Node("#wt-wu",           "薄雾"));
        WEATHER_CODE_MAP.put("501",new Node("#wt-wu",           "雾"));
        WEATHER_CODE_MAP.put("502",new Node("#wt-wu",           "霾"));
        WEATHER_CODE_MAP.put("503",new Node("#wt-wu",           "扬沙"));
        WEATHER_CODE_MAP.put("504",new Node("#wt-wu",           "浮尘"));
        WEATHER_CODE_MAP.put("507",new Node("#wt-wu",           "沙尘暴"));
        WEATHER_CODE_MAP.put("508",new Node("#wt-wu",           "强沙尘暴"));
        WEATHER_CODE_MAP.put("509",new Node("#wt-wu",           "浓雾"));
        WEATHER_CODE_MAP.put("510",new Node("#wt-wu",           "强浓雾"));
        WEATHER_CODE_MAP.put("511",new Node("#wt-wu",           "中度霾"));
        WEATHER_CODE_MAP.put("512",new Node("#wt-wu",           "重度霾"));
        WEATHER_CODE_MAP.put("513",new Node("#wt-wu",           "严重霾"));
        WEATHER_CODE_MAP.put("514",new Node("#wt-wu",           "大雾"));
        WEATHER_CODE_MAP.put("515",new Node("#wt-wu",           "特强浓雾"));
        WEATHER_CODE_MAP.put("900",new Node("#",                "热"));
        WEATHER_CODE_MAP.put("901",new Node("#",                "冷"));
        WEATHER_CODE_MAP.put("999",new Node("#",                "未知"));
    }


    static String getIcon(String code) {
        Node node = WEATHER_CODE_MAP.get(StrUtil.isBlank(code) ? "999" : code);
        return node == null ? "#" : node.getIcon();
    }

    static String getText(String code) {
        Node node = WEATHER_CODE_MAP.get(StrUtil.isBlank(code) ? "999" : code);
        return node == null ? "#" : node.getText();
    }

    static class Node {
        private String icon;
        private String text;

        Node(String icon, String text) {
            this.icon = icon;
            this.text = text;
        }

        public String getIcon() {
            return icon;
        }

        public String getText() {
            return text;
        }
    }
}
