package com.blossom.common.base.pojo;

import cn.hutool.core.util.StrUtil;
import com.blossom.common.base.util.BeanUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 返回对象
 *
 * @author : xzzz
 */
@Getter
@Setter
public class R<T> implements Serializable {
    /**
     * [公共响应] 自定义响应码, [20000]为正常, 其他请参阅错误码列表
     * @mock 20000
     */
    private String code;
    /**
     * [公共响应] 说明, 正常时返回 "成功"
     * @mock 成功
     */
    private String msg;
    /**
     * [公共响应] 异常信息, 无后台错误时无该字段
     * @mock Exception by xxx
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String ex;
    /**
     * [公共响应] 响应内容, 响应类如果为[?]则代表无 data
     */
    private T data;

    private R() {
    }

    // region 返回成功

    /**
     * 返回成功
     */
    public static R<?> ok() {
        return R.initR(RCode.SUCCESS.getCode(), RCode.SUCCESS.getMsg(), null, null);
    }

    /**
     * 返回成功
     *
     * @param data 对象类型的data数据
     */
    public static <T> R<T> ok(T data) {
        return R.initR(RCode.SUCCESS.getCode(), RCode.SUCCESS.getMsg(), null, data);
    }

    /**
     * 返回成功，并转换成 clazz 类型对象
     *
     * @param data  数据
     * @param clazz 转换结果
     * @return clazz 类型对象
     */
    public static <T> R<T> ok(Object data, Class<T> clazz) {
        return R.initR(RCode.SUCCESS.getCode(), RCode.SUCCESS.getMsg(), null, BeanUtil.toObj(data, clazz));
    }

    /**
     * 返回成功，并将集合中的对象转换成 clazz 对象
     *
     * @param data  集合数据
     * @param clazz 转换结果
     * @param <T>   集合泛型
     * @return 集合结果
     */
    public static <T> R<List<T>> ok(List<?> data, Class<T> clazz) {
        return R.initR(RCode.SUCCESS.getCode(), RCode.SUCCESS.getMsg(), null, BeanUtil.toList(data, clazz));
    }

    /**
     * 返回成功分页对象
     *
     * @param page 分页对象
     * @param <E>  分页对象泛型
     * @return 分页成功结果
     */
    public static <E> R<PageRes<E>> ok(PageInfo<E> page) {
        PageRes<E> pageRes = new PageRes<>();
        pageRes.setPageNum(page.getPageNum());
        pageRes.setPageSize(page.getPageSize());
        pageRes.setTotal(page.getTotal());
        pageRes.setTotalPage(page.getPages());
        pageRes.setDatas(page.getList());
        return R.ok(pageRes);
    }

    /**
     * 返回成功分页对象，并将分页集合中的对象转换成 clazz 对象
     *
     * @param page  集合数据
     * @param clazz 转换结果
     * @param <E>   转换后的分页数据泛型
     * @return 分页成功结果
     */
    public static <E> R<PageRes<E>> ok(PageInfo<?> page, Class<E> clazz) {
        PageRes<E> pageRes = new PageRes<>();
        pageRes.setPageNum(page.getPageNum());
        pageRes.setPageSize(page.getPageSize());
        pageRes.setTotal(page.getTotal());
        pageRes.setTotalPage(page.getPages());
        pageRes.setDatas(BeanUtil.toList(page.getList(), clazz));
        pageRes.setHasNextPage(page.isHasNextPage());
        return R.ok(pageRes);
    }

    // endregion

    // region 返回失败

    /**
     * 50000 默认响应信息
     */
    public static R fault() {
        return R.initR(RCode.INTERNAL_SERVER_ERROR.getCode(), RCode.INTERNAL_SERVER_ERROR.getMsg(), null, null);
    }

    /**
     * 50000/自定义响应信息
     */
    public static R fault(String msg) {
        return R.initR(RCode.INTERNAL_SERVER_ERROR.getCode(), msg, null, null);
    }

    /**
     * 失败, 自定义响应码和响应信息
     *
     * @param code 响应码
     * @param msg  响应信息
     */
    public static R fault(String code, String msg) {
        return R.initR(code, msg != null ? clearStr(msg) : RCode.byCode(code).getMsg(), null, null);
    }

    /**
     * 自定义返回值/自定义返回信息
     */
    public static R fault(String code, String msg, String ex) {
        return R.initR(code, msg != null ? clearStr(msg) : RCode.byCode(code).getMsg(), ex, null);
    }

    // endregion

    /**
     * 初始化返回对象
     */
    private static <T> R<T> initR(String code, String msg, String ex, T data) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setEx(ex);
        r.setData(data);
        return r;
    }

    /**
     * 对返回字符串进行一些格式处理
     * - 例如:
     * - gateway返回的错误信息都包含双引号,序列化后会有多引号的情况 : ""内容""
     *
     * @param str 字符串对象
     * @return 处理后的对象
     */
    private static String clearStr(String str) {
        return StrUtil.replace(str, "\"", "");
    }

}
