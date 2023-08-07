package com.blossom.backend.server.utils;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.blossom.common.base.util.DateUtils;

import java.util.Date;

/**
 * 文章工具类
 *
 * @author xzzz
 */
public class ArticleUtil {

    /**
     * 统计文章字数, 只统计中文, 英文
     *
     * @param markdown markdown 内容
     * @return 字数
     */
    public static Integer statWords(String markdown) {
        if (StrUtil.isBlank(markdown)) {
            return 0;
        }
        String[] lines = markdown.split("\n");
        if (ArrayUtil.isEmpty(lines)) {
            return 0;
        }
        int ch = 0;// 中文字符
        int en = 0;// 英文字符
        int space = 0;// 空格
        int number = 0;// 数字
        int other = 0;// 其他字符

        // 是否多行代码块内, 多行代码块内的内容不计算在内.
        boolean isPreTag = false;
        for (String line : lines) {
            if (line.length() == 0) {
                continue;
            }
            if (!isPreTag && line.startsWith("```")) {
                isPreTag = true;
            } else if (isPreTag && line.startsWith("```")) {
                isPreTag = false;
            }

            if (isPreTag) {
                continue;
            }

            for (int i = 0; i < line.length(); i++) {
                char tmp = line.charAt(i);
                if ((tmp >= 'A' && tmp <= 'Z') || (tmp >= 'a' && tmp <= 'z')) {
                    en++;
                } else if ((tmp >= '0') && (tmp <= '9')) {
                    number++;
                } else if (tmp == ' ') {
                    space++;
                } else if (isChinese(tmp)) {
                    ch++;
                } else {
                    other++;
                }
            }
        }
//        System.out.println("中文: " + ch + "个");
//        System.out.println("英文: " + en + "个");
//        System.out.println("数字: " + number + "");
//        System.out.println("空格: " + space + "个");
//        System.out.println("其他: " + other + "个");
        return ch + en + number;
    }

    /**
     * 字符是否中文
     *
     * @param ch 字符
     * @return 是否中文
     */
    private static boolean isChinese(char ch) {
        //获取此字符的UniCodeBlock
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(ch);
        // GENERAL_PUNCTUATION 判断中文的“号
        // CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;

    }

    public static void main(String[] args) {
//        genHeatmap();
//        genWords();
        System.out.println(DateUtils.today().substring(0, 7) + "-01");
    }

    private static void genHeatmap() {
        Date begin = DateUtils.parse("2023-04-01", DateUtils.PATTERN_YYYYMMDD);
        Date end = DateUtils.parse("2023-06-30", DateUtils.PATTERN_YYYYMMDD);
        for (; DateUtils.compare(begin, end) <= 0; begin = DateUtils.offsetDay(begin, 1)) {
            String dt = DateUtils.format(begin, DateUtils.PATTERN_YYYYMMDD);
            int value = RandomUtil.randomInt(0, 10);
            System.out.println(String.format("insert into blossom_stat values(null, 1, '%s' ,%s);", dt, value));
        }
    }

    private static void genWords() {
        int value = 203012;
        Date begin = DateUtils.parse("2019-01-01", DateUtils.PATTERN_YYYYMMDD);
        Date end = DateUtils.parse("2023-06-30", DateUtils.PATTERN_YYYYMMDD);
        for (; DateUtils.compare(begin, end) <= 0; begin = DateUtils.offsetMonth(begin, 1)) {
            String dt = DateUtils.format(begin, DateUtils.PATTERN_YYYYMMDD);
            value = value + RandomUtil.randomInt(0, 10000);
            System.out.println(String.format("insert into blossom_stat values(null, 2, '%s' ,%s);", dt, value));
        }
    }
}
