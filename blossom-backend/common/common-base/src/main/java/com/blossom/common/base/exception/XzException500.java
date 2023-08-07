package com.blossom.common.base.exception;

import cn.hutool.core.util.StrUtil;
import com.blossom.common.base.pojo.RCode;

/**
 * @author xzzz
 */
public class XzException500 extends XzAbstractException {

    public XzException500(String message) {
        super(RCode.INTERNAL_SERVER_ERROR.getCode(), message);
    }

    public static void throwBy(boolean expression, String msg) {
        if (expression) {
            if (StrUtil.isBlank(msg)) {
                msg = RCode.INTERNAL_SERVER_ERROR.getMsg();
            }
            throw new XzException404(msg);
        }
    }
}
