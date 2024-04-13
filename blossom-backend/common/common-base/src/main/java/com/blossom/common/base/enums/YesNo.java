package com.blossom.common.base.enums;

import lombok.Getter;

/**
 * 是否枚举
 *
 * @author xzzz
 */
@Getter
public enum YesNo {

    /**
     * 是, TRUE
     */
    YES(1, "是", true),
    /**
     * 否, FALSE
     */
    NO(0, "否", false);

    Integer value;
    String name;
    Boolean aBoolean;

    YesNo(Integer value, String name, Boolean aBoolean) {
        this.value = value;
        this.name = name;
        this.aBoolean = aBoolean;
    }

    public static Integer toInt(Boolean aBoolean) {
        if (aBoolean) {
            return YES.value;
        }
        return NO.value;
    }

    public static Boolean is(int value) {
        if (value == 1) {
            return YES.aBoolean;
        }
        return NO.aBoolean;
    }

    public static YesNo getValue(Integer value) {
        for (YesNo yesNo : YesNo.values()) {
            if (yesNo.getValue().equals(value)) {
                return yesNo;
            }
        }
        return YesNo.NO;
    }
}
