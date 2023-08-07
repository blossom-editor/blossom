package com.blossom.common.base.exception;

import cn.hutool.core.util.StrUtil;
import com.blossom.common.base.pojo.RCode;

/**
 * @author xzzz
 */
public class XzException400 extends XzAbstractException {

    public XzException400(String message) {
        super(RCode.BAD_REQUEST.getCode(), message);
    }

    public XzException400(String message, String... args) {
        super(RCode.BAD_REQUEST.getCode(), String.format(message, args));
    }

    public static void throwBy(boolean expression, String msg) {
        if (expression) {
            if (StrUtil.isBlank(msg)) {
                msg = RCode.BAD_REQUEST.getMsg();
            }
            throw new XzException400(msg);
        }
    }
}
