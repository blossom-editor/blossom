package com.blossom.common.base.util.spi;

import java.util.ServiceLoader;

/**
 * 根据接口或抽象类获取该类的全部实现类
 *
 * @author xzzz
 */
public final class ServiceLoaderUtil {

    private static final String CLASSLOADER_DEFAULT = "default";
    private static final String CLASSLOADER_CONTEXT = "context";
    private static final String CLASSLOADER_PATH = "";

    public static <S> ServiceLoader<S> getServiceLoader(Class<S> clazz) {
        return ServiceLoader.load(clazz, clazz.getClassLoader());
    }

    private ServiceLoaderUtil() {}
}
