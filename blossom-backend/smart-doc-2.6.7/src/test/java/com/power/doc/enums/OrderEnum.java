package com.power.doc.enums;

/**
 * @author yu 2019/10/31.
 */
public enum OrderEnum implements BaseEnum {
    SUCCESS("0000", "success"),

    PARAM_EMPTY("1001", "必选参数为空"),

    PARAM_ERROR("1002", "参数格式错误"),

    UNKNOWN_ERROR("9999", "系统繁忙，请稍后再试....");

    private String code;

    private String desc;

    OrderEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return this.code;
    }


    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "ErrorCodeEnum{" +
            "code='" + code + '\'' +
            ", desc='" + desc + '\'' +
            '}';
    }
}
