//package com.blossom.common.db.p6spy;
//
//import cn.hutool.core.util.StrUtil;
//
//public class P6SpyLogger implements MessageFormattingStrategy {
//
//    /**
//     * 格式化日志
//     *
//     * @param connectionId 链接ID
//     * @param now          当前时间
//     * @param elapsed      用时
//     * @param category     科目
//     * @param prepared     error,info,batch,debug,statement,commit,rollback,result,resultset
//     * @param sql          sql
//     * @param url          数据库连接
//     * @return 日志格式
//     */
//    @Override
//    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
//        if (StrUtil.isNotBlank(sql)) {
//            return String.format("SQL >> [% 4dms]%s", elapsed, sql.replaceAll("[\\s]+", " "));
//        }
//        return "";
//    }
//}