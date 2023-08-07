package com.blossom.expand.sentinel.metric.util;

import cn.hutool.core.util.StrUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * 优化Sentinel使用的一些工具方法
 *
 * @author xzzz
 */
public class SentinelUtil {

    private static final Set<String> ignoreResource = new HashSet<String>() {{
        this.add("__total_inbound_traffic__");
        this.add("__system_load__");
        this.add("__cpu_usage__");
        this.add("/webStatus");
    }};

    /**
     * 判断资源是否需要忽略, 一般是全局的资源在某些情况下是需要忽略的
     * @param resourceName 资源名称
     * @return 是否忽略
     */
    public static boolean ignoreResource(String resourceName) {
        return ignoreResource.contains(resourceName);
    }

    /**
     * 简化的 resourceName
     *
     * <ol>
     * <li>http 资源会忽略路径的中间值</li>
     * <ul>
     * <li>完整资源名: /aaa/bbb/ccc</li>
     * <li>简化资源名: /aaa/.../ccc</li>
     * </ul>
     *
     * <li>dubbo 资源会简化只保留方法名和参数类名:</li>
     * <ul>
     *  <li>简化资源名: login(DubboLoginDTO)</li>
     * </ul>
     * </ol>
     *
     * @param resourceName 资源名称
     * @return 简化的资源名称
     */
    public static String tinyResourceName(String resourceName) {
        if (ignoreResource(resourceName)) {
            return resourceName;
        }

        try {
            // 简化HTTP资源名
            if (isHttp(resourceName)) {
                return tinyHttpResourceName(resourceName);
            }
            // 简化RPC资源名
            else if (isRpc(resourceName)) {
                return tinyRpcResourceName(resourceName);
            }
        } catch (Exception e) {
            // 简化失败
        }

        return resourceName;
    }


    // region 简化HTTP资源名

    /**
     * 检查是否是HTTP资源
     * @param resourceName 资源名称
     * @return 是否
     */
    public static boolean isHttp(String resourceName) {
        if (StrUtil.isBlank(resourceName)) {
            return false;
        }
        return resourceName.startsWith("/");
    }

    /**
     * 简化 HTTP 资源名称
     * @param resourceName 资源名
     * @return 简化资源名
     */
    public static String tinyHttpResourceName(String resourceName) {
        if (countStr(resourceName,"/") <= 2) {
            return resourceName;
        }

        // 字符串第二次出现的位置
        int second = index2of(resourceName, "/") + 1;
        int last = StrUtil.lastIndexOfIgnoreCase(resourceName, "/");

        return resourceName.substring(0, second) + "..." + resourceName.substring(last);
    }

    // endregion


    /**
     * 是否RPC请求, 如果资源中包含 "." 和 "()" 则认为是RPC请求
     * @param resourceName 资源名
     * @return 是否RPC
     */
    public static boolean isRpc(String resourceName) {
        if (StrUtil.isBlank(resourceName)) {
            return false;
        }
        if (StrUtil.indexOf(resourceName, '.') != -1 &&
            StrUtil.indexOf(resourceName, ':') != -1 &&
            StrUtil.indexOf(resourceName, '(') != -1 &&
            StrUtil.indexOf(resourceName, ')') != -1) {
            return true;
        }
        return false;
    }

    /**
     * 简化 DUBBO 资源名
     * @param resourceName 资源名
     * @return 简化资源名
     */
    public static String tinyRpcResourceName(String resourceName) {
        String method = resourceName.substring(resourceName.indexOf(":") + 1, resourceName.indexOf("("));

        String argsStr = resourceName.substring(resourceName.indexOf("(") + 1, resourceName.indexOf(")"));

        String[] args = argsStr.split(",");
        if (args.length <= 1) {
            return method + "(" + argsStr.substring(StrUtil.lastIndexOfIgnoreCase(argsStr,".") + 1) + ")";
        } else {
            StringBuilder argsBuilder = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    argsBuilder.append(",");
                }
                argsBuilder.append(args[i].substring(StrUtil.lastIndexOfIgnoreCase(args[i],".") + 1));
            }
            return method + "(" + argsBuilder.toString() + ")";
        }
    }


    /**
     * 查询字符第二次出现的位置
     * @param resourceName 字符串
     * @param searchStr 被查询的字符
     * @return 在字符串的位置
     */
    private static int index2of(String resourceName, String searchStr) {
        int first = StrUtil.indexOfIgnoreCase(resourceName, searchStr) + 1;
        return StrUtil.indexOfIgnoreCase(resourceName.substring(first), searchStr) + first;
    }

    /**
     * 计算字符串出现的次数
     * @param str 字符串
     * @param searchStr 被查询的字符
     * @return 次数
     */
    private static int countStr(String str, String searchStr) {
        if (StrUtil.isBlank(str) || StrUtil.isBlank(searchStr)) {
            return 0;
        }

        return str.split(searchStr).length - 1;
    }
}
