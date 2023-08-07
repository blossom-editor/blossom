package com.blossom.common.base.util;

import com.blossom.common.base.exception.XzException500;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.cglib.beans.BeanCopier;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xzzz
 * @since 0.0.1
 */
@SuppressWarnings("all")
public class BeanUtil extends org.springframework.beans.BeanUtils {

    private static final Logger log = LoggerFactory.getLogger(BeanUtil.class);

    /**
     * 对象拷贝 (数据源为空返回空)
     *
     * @param from 数据源
     * @param to   目标对象
     * @param <T>  范型
     * @return 数据源为空返回空
     */
    public static <T> T toObj(Object from, Class to) throws BeansException {
        if (null == from) {
            return null;
        }
        Object dest;
        try {
            dest = to.newInstance();
            copyProperties(from, dest);
        } catch (Exception e) {
            throw new XzException500("BeanUtil.copyObject 转换异常: " + e.getMessage());
        }
        return (T) dest;
    }

    /**
     * Spirng 原生的 bean copy, 比{@link BeanUtil#toObj(Object, Class)} 快, 但麻烦的是需要在外部创建对象
     *
     * @param from 源文件的, 需要先创建对象
     * @param to   目标文件, 需要先创建对象
     */
    public static void toObj(Object from, Object to) throws BeansException {
        if (null == from || to == null) {
            return;
        }
        try {
            copyProperties(from, to);
        } catch (Exception e) {
            throw new XzException500("BeanUtil.copyObject 转换异常: " + e.getMessage());
        }
    }


    /**
     * BeanCopier的copy, 父类字段无法复制, 但是速度最快
     *
     * @param from 源文件的, 需要先创建对象
     * @param to   目标文件, 需要先创建对象
     */
    public static void toObjByCopier(Object from, Object to) {
        BeanCopier beanCopier = BeanCopier.create(from.getClass(), to.getClass(), false);
        beanCopier.copy(from, to, null);
    }

    /**
     * 拷贝集合
     *
     * @param from 数据源
     * @param to   目标
     * @param <T>  范型
     * @return 数据源为空返回空
     */
    public static <T> List<T> toList(List from, Class to) {
        if (null == from || from.isEmpty()) {
            return null;
        }

        List<T> dest = new ArrayList<>();
        from.forEach((Object fromItem) -> {
            try {
                T toObject = (T) to.newInstance();
                copyProperties(fromItem, toObject);
                dest.add(toObject);
            } catch (Exception e) {
                throw new XzException500("BeanUtil.copyObject 转换异常: " + e.getMessage());
            }
        });
        return dest;
    }
}
