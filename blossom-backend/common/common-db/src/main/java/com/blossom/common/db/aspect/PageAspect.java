package com.blossom.common.db.aspect;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.blossom.common.base.exception.XzException400;
import com.blossom.common.db.pojo.PageReq;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 分页切面
 * <p>注意：该分页只对方法中的第一个查询语句生效，如果方法中包含多个查询语句需要分页，则人需要手动使用 {@link PageHelper#startPage(int, int)}
 *
 * @author xzzz
 */
@Slf4j
@Aspect
public class PageAspect {

    @Around("@annotation(com.blossom.common.db.aspect.Pages)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        for (Object arg : args) {
            if (arg instanceof PageReq) {
                PageReq page = (PageReq) arg;
                if (page.getPageNum() == 0) {
                    page.setPageNum(1);
                }
                if (page.getPageSize() == 0) {
                    page.setPageSize(10);
                }
                if (page.getPageSize() > 200) {
                    page.setPageSize(200);
                }
                String orderBy;
                if (StrUtil.isNotBlank(page.getSortField())) {
                    orderBy = page.getSortField();
                    if (StrUtil.isNotBlank(page.getOrder())) {
                        String order = page.getOrder().trim().toLowerCase();
                        if (!"asc".equals(order) && !"desc".equals(order)) {
                            throw new XzException400("Order参数错误,请使用[asc]或[desc],当前传入值[" + order + "]");
                        }
                        orderBy = orderBy + " " + order;
                    }
                    PageHelper.startPage(page.getPageNum(), page.getPageSize(), orderBy);
                } else {
                    PageHelper.startPage(page.getPageNum(), page.getPageSize());
                }
            }
        }
        try {
            return point.proceed(args);
        } finally {
//            PageHelper.clearPage();
        }
    }


}
