package com.blossom.common.base.exception;

import cn.hutool.core.util.StrUtil;
import com.blossom.common.base.pojo.RCode;

/**
 * @author xzzz
 */
public class XzExceptionSys extends XzAbstractException {

    public XzExceptionSys(String message) {
        super(RCode.SYSTEM_INITIATE_ERROR.getCode(), message);
    }

    public XzExceptionSys(String message, String... args) {
        super(RCode.SYSTEM_INITIATE_ERROR.getCode(), String.format(message, args));
    }

    public static void throwBy(boolean expression, String msg) {
        if (expression) {
            if (StrUtil.isBlank(msg)) {
                msg = RCode.SYSTEM_INITIATE_ERROR.getMsg();
            }
            throw new XzExceptionSys(msg);
        }
    }
}
