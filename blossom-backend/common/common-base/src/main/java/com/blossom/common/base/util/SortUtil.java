package com.blossom.common.base.util;

import cn.hutool.core.util.StrUtil;

import java.util.*;

/**
 * @author xzzz
 */
public class SortUtil {

    /**
     * int 类型的排序, 也可以直接使用 Integer::compareTo
     */
    public static Comparator<Integer> intSort = Integer::compareTo;

    public static Comparator<Long> longSort = Long::compareTo;

    public static Comparator<Date> dateSort = Date::compareTo;

    /**
     * 字符串排序功能
     */
    public static Comparator<String> strSort = (str1, str2) -> {
        // 处理数据为null的情况
        if (str1 == null && str2 == null) {
            return 0;
        }
        if (str1 == null) {
            return -1;
        }
        if (str2 == null) {
            return 1;
        }
        // 比较字符串中的每个字符
        char c1;
        char c2;
        // 逐字比较返回结果
        for (int i = 0; i < str1.length(); i++) {
            c1 = str1.charAt(i);
            try {
                c2 = str2.charAt(i);
            } catch (StringIndexOutOfBoundsException e) { // 如果在该字符前，两个串都一样，str2更短，则str1较大
                return 1;
            }
            // 如果都是数字的话，则需要考虑多位数的情况，取出完整的数字字符串，转化为数字再进行比较
            if (Character.isDigit(c1) && Character.isDigit(c2)) {
                StringBuilder numStr1 = new StringBuilder();
                StringBuilder numStr2 = new StringBuilder();
                // 获取数字部分字符串
                for (int j = i; j < str1.length(); j++) {
                    c1 = str1.charAt(j);
                    if (!Character.isDigit(c1)) { // 不是数字则直接退出循环
                        break;
                    }
                    numStr1.append(c1);
                }
                for (int j = i; j < str2.length(); j++) {
                    c2 = str2.charAt(j);
                    if (!Character.isDigit(c2) && c2 != '.') { // 考虑可能带小数的情况
                        break;
                    }
                    numStr2.append(c2);
                }
                // 转换成数字数组进行比较 适配 1.25.3.5 这种情况
                String[] numberArray1 = numStr1.toString().split("\\.");
                String[] numberArray2 = numStr2.toString().split("\\.");
                return compareNumberArray(numberArray1, numberArray2);
            }

            // 不是数字的比较方式
            if (c1 != c2) {
                return c1 - c2;
            }
        }
        return 0;
    };

    public static int compareNumberArray(String[] numberArray1, String[] numberArray2) {
        for (int i = 0; i < numberArray1.length; i++) {
            // 此时数字数组2比1短，直接返回
            if (numberArray2.length < i + 1) {
                return 1;
            }
            int compareResult = Integer.valueOf(numberArray1[i]).compareTo(Integer.valueOf(numberArray2[i]));
            if (compareResult != 0) {
                return compareResult;
            }
        }
        // 说明数组1比数组2短，返回小于
        return -1;
    }

    /**
     * 返回的是 SortedMap，一个排序后的 treeMap
     *
     * @param paramMap 参数集合
     * @return Map<String, String> 排序后的参数集合
     */
    public static Map<String, String> sortMap(Map<String, String> paramMap) {
        SortedMap<String, String> map = new TreeMap<>();
        String value;
        for (String key : paramMap.keySet()) {
            if (key != null) {
                value = paramMap.get(key);
                if (StrUtil.isNotEmpty(value)) {
                    map.put(key, value);
                }
            }
        }
        return map;
    }
}
