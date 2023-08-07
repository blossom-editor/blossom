package com.blossom.common.base.log;

import lombok.Data;

/**
 * 动态日志级别的存储方式
 *
 * @author xzzz
 */
public interface DynamicLogRepository {

    /**
     * 保存日志级别
     *
     * @param levelWrapper 日志封装
     */
    void save(LevelWrapper levelWrapper);


    @Data
    class LevelWrapper {
        /**
         * 日志路径
         */
        private String path;
        /**
         * 过期时间
         */
        private Long expire;
        /**
         * 该路径所要执行的日志级别
         */
        private String level;

        public LevelWrapper() {
        }

        public LevelWrapper(String path, String level, Long expire) {
            this.path = path;
            this.expire = expire;
            this.level = level;
        }

    }
}
