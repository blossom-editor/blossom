package com.blossom.backend.base.auth.exception;

import cn.hutool.core.util.StrUtil;
import com.blossom.common.base.exception.XzAbstractException;
import com.blossom.common.base.pojo.IRCode;
import com.blossom.common.base.pojo.RCode;

/**
 * 授权相关异常
 *
 * @author xzzz
 */
public class AuthException extends XzAbstractException {

    public AuthException(AuthRCode authRCode) {
        super(authRCode);
    }

    public AuthException(String code, String message) {
        super(code, message);
    }

    public static void throwBy(boolean expression, IRCode authRCode) {
        if (authRCode == null) {
            authRCode = RCode.BAD_REQUEST;
        }
        throwBy(expression, authRCode.getCode(), authRCode.getMsg());
    }

    public static void throwBy(boolean expression, String code, String msg) {
        if (expression) {
            if (StrUtil.isBlank(msg)) {
                msg = RCode.BAD_REQUEST.getMsg();
            }
            throw new AuthException(code, msg);
        }
    }

}