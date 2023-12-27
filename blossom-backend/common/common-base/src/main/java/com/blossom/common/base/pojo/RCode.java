package com.blossom.common.base.pojo;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * httpCode : httpCode返回码
 * code     : 系统自定义返回码
 * msg      : 异常信息
 * @author : xzzz
 */
@SuppressWarnings("ALL")
public enum RCode implements IRCode {

    /* 成功状态码 */
    SUCCESS                  ("20000", "成功"),

    /* ──────────────────────────────────────────── 400 ────────────────────────────────────────────────*/
    BAD_REQUEST              ("40000", "错误的请求"),

    TOO_MANY_REQUESTS        ("42901", "请求频繁, 请稍后再试"),

    /* ──────────────────────────────────────────── 403 ────────────────────────────────────────────────*/
    SERVER_DENIED            ("40301", "服务器拒绝访问"),
    SERVER_DENIED_CUR_ENV    ("40302", "当前环境无法访问该资源"),

    /* ──────────────────────────────────────────── 404 ────────────────────────────────────────────────────────────*/
    NOT_FOUND                ("40400", "未找到您的请求"),

    /* ──────────────────────────────────────────── 500 ────────────────────────────────────────*/
    INTERNAL_SERVER_ERROR    ("50000", "服务器处理错误"),
    INTERNAL_SQL_ERROR       ("50001", "服务器语句执行错误"),

    /* ──────────────────────────────────────────── 503 ────────────────────────────────────────*/
    SERVER_UNAVAILABLE       ("50300", "服务器暂时无法处理请求, 请稍后再试"),
    ;

    @Getter
    private String code;
    @Getter
    private String msg;

    RCode(String code, String message) {
        this.code = code;
        this.msg = message;
    }

    /**
     * 根据code返回信息
     * @param code
     * @return
     */
    public static RCode byCode(String code) {
        for (RCode item : RCode.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return RCode.INTERNAL_SERVER_ERROR;
    }

    @Override
    public String toString() {
        return this.name();
    }

    //校验重复的code值
    public static void main(String[] args) {
        RCode[] apiResultCodes = RCode.values();
        List<String> codeList = new ArrayList<>();
        for (RCode apiResultCode : apiResultCodes) {
            if (codeList.contains(apiResultCode.code)) {
                System.out.println(apiResultCode.code);
            } else {
                codeList.add(apiResultCode.getCode());
            }
        }
    }
}
