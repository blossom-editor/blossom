package com.blossom.common.base.exception;

import cn.hutool.core.util.StrUtil;
import com.blossom.common.base.pojo.IRCode;

/**
 * 异常, 通常不使用该异常
 *
 * @author xzzz
 */
public class XzException extends XzAbstractException {

    public XzException(IRCode code) {
        super(code.getCode(), code.getMsg());
    }

    public XzException(String code, String msg) {
        super(code, msg);
    }

    public static void throwBy(boolean expression, String code, String msg) {
        if (expression) {
            if (StrUtil.isBlank(msg)) {
                msg = "";
            }
            throw new XzException(code, msg);
        }
    }
}
