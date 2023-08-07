package com.blossom.common.base.util.spring;

import org.springframework.util.AntPathMatcher;

/**
 * 路径匹配工具类,依赖 Spring 的工具类
 *
 * @author xzzz
 * @since 0.0.1
 */
public class AntPathMatcherUtil {

    private static AntPathMatcher antPathMatcher = new AntPathMatcher();

    public static AntPathMatcher getAntPathMatcher() {
        return antPathMatcher;
    }

    /**
     * 匹配请求路径是否在模板中
     * @param pattern 模板路径
     * @param path 路径
     * @return true:在模板中, false:不在模板中
     */
    public static boolean match (String pattern,String path) {
        return antPathMatcher.match(pattern,path);
    }
}
