package com.blossom.common.base.exception;

import com.blossom.common.base.pojo.IRCode;

/**
 * 异常抽象类
 *
 * @author xzzz
 */
public abstract class XzAbstractException extends RuntimeException {

    /**
     * 响应码
     */
    private final String code;

    /**
     * 自定义响应码和响应信息
     *
     * @param code 响应码
     * @param msg  响应信息
     */
    public XzAbstractException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    /**
     * 直接返回响应码枚举
     */
    public XzAbstractException(IRCode irCode) {
        super(irCode.getMsg());
        this.code = irCode.getCode();
    }

    public String getCode() {
        return code;
    }

}
