package com.blossom.common.base.exception;

import cn.hutool.core.util.StrUtil;
import com.blossom.common.base.pojo.RCode;

/**
 * @author xzzz
 */
public class XzException404 extends XzAbstractException {

    public XzException404(String message) {
        super(RCode.NOT_FOUND.getCode(), message);
    }

    public static void throwBy(boolean expression, String msg) {
        if (expression) {
            if (StrUtil.isBlank(msg)) {
                msg = RCode.NOT_FOUND.getMsg();
            }
            throw new XzException404(msg);
        }
    }
}
