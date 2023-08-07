package com.blossom.expand.sentinel.metric.pojo;

import lombok.Data;
import lombok.Getter;

/**
 * 资源聚合
 *
 * @author xzzz
 */
@Data
public class ResourceAggsRes {

    private String resourceName;
    private Integer value;
    private Integer rt;
    private Integer count;

    public ResourceAggsRes(String resourceName, Integer value, Integer rt) {
        this.resourceName = resourceName;
        this.value = value;
        this.rt = rt;
    }

    @Getter
    public static class StatisticDTO {
        // 统计次数
        private Integer count;
        // 请求数
        private Integer value;
        // 响应时间总和
        private Integer rt;

        public StatisticDTO() {
            this.count = 0;
            this.rt = 0;
            this.value = 0;
        }

        public void increaseValue(Integer value) {
            this.value += value;
        }

        public void increaseRt(Integer rt) {
            this.rt += rt;
            this.count++;
        }
    }
}
