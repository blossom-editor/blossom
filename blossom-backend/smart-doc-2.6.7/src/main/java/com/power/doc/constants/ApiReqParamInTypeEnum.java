package com.power.doc.constants;

/**
 * @author chen qi 2021-07-15 10:55
 **/
public enum ApiReqParamInTypeEnum {

    /**
     * header param
     */
    HEADER("header"),

    /**
     * query param
     */
    QUERY("query"),

    /**
     * path param
     */
    PATH("path");

    private final String value;

    ApiReqParamInTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
