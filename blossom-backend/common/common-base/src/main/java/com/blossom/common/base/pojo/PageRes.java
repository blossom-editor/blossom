package com.blossom.common.base.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 共用分页对象
 *
 * @author xzzz
 */
@Getter
@Setter
public class PageRes<T> {

    /**
     * 页码，0或1均表示第一页
     */
    private int pageNum;
    /**
     * 每页结果数
     */
    private int pageSize;
    /**
     * 总页数
     */
    private int totalPage;
    /**
     * 总行数
     */
    private long total;
    /**
     * 是否有下一页
     */
    private Boolean hasNextPage;
    /**
     * 分页数据
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<T> datas;


    public PageRes() {
    }

    public PageRes(List<T> datas) {
        this.datas = datas;
    }
}
