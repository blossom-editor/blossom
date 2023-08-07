package com.blossom.common.base.pojo;

import com.blossom.common.base.util.BeanUtil;

/**
 * @author xzzz
 */
public class AbstractPOJO {

    public <T> T to(Class<T> clazz) {
        return BeanUtil.toObj(this, clazz);
    }

}
