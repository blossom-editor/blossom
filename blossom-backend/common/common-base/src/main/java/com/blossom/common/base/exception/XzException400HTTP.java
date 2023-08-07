package com.blossom.common.base.exception;

import cn.hutool.core.util.StrUtil;
import com.blossom.common.base.pojo.RCode;

/**
 * 抛出异常, 如果是 Rest 接口, 则 Http 响应码为 404
 *
 * @author xzzz
 */
public class XzException400HTTP extends XzAbstractException {

    public XzException400HTTP(String message) {
        super(RCode.BAD_REQUEST.getCode(), message);
    }

    public static void throwBy(boolean expression, String msg) {
        if (expression) {
            if (StrUtil.isBlank(msg)) {
                msg = RCode.BAD_REQUEST.getMsg();
            }
            throw new XzException400HTTP(msg);
        }
    }
}
