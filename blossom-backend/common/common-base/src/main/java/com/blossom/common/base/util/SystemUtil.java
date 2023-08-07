/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blossom.common.base.util;

import com.sun.management.OperatingSystemMXBean;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.util.Enumeration;

/**
 * Get host name and ip of the host.
 *
 * @author xzzz
 */
@Slf4j
public final class SystemUtil {

    private static String ip;
    private static String hostName;

    static {
        try {
            // Init the host information.
            resolveHost();
        } catch (Exception e) {
            log.info("Failed to get local host", e);
        }
    }

    private static void resolveHost() throws Exception {
        InetAddress addr = InetAddress.getLocalHost();
        hostName = addr.getHostName();
        ip = addr.getHostAddress();
        if (addr.isLoopbackAddress()) {
            // find the first IPv4 Address that not loopback
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface in = interfaces.nextElement();
                Enumeration<InetAddress> addrs = in.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress address = addrs.nextElement();
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                        ip = address.getHostAddress();
                    }
                }
            }
        }
    }

    /**
     * 获取本地IP
     */
    public static String getIp() {
        return ip;
    }

    /**
     * 获取本地名称
     */
    public static String getHostName() {
        return hostName;
    }


    /**
     * Resolve and get current process ID.
     *
     * @return current process ID
     */
    public static int getPid() {
        // Note: this will trigger local host resolve, which might be slow.
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return Integer.parseInt(name.split("@")[0]);
    }

    /**
     * 逗号
     */
    private static final String COMMA = ",";
    private static final String UNKNOWN = "unknown";
    private static final String LOCAL_IPV4 = "unknown";
    private static final String LOCAL_IPV6 = "unknown";

    /**
     * 获取远程客户端的真实 IP 地址
     * @param request 请求对象
     * @return String 真实的 IP 地址
     */
    public static String getRemoteIp(HttpServletRequest request) {
        // X-Forwarded-For 记录一个请求从客户端出发到目标服务器过程中经历的代理，或者负载平衡设备的IP。
        String ip = request.getHeader("x-forwarded-for");
        if (null != ip && ip.length() != 0 && !UNKNOWN.equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个IP才是真实IP,它们按照英文逗号','分割
            if (ip.contains(COMMA)) {
                ip = ip.split(COMMA)[0];
            }
        }
        // Proxy-Client-IP / WL-Proxy-Client-IP 这个一般是经过apache http服务器的请求才会有，
        // 用apache http做代理时一般会加上Proxy-Client-IP请求头，而WL-Proxy-Client-IP是他的weblogic插件加上的头。
        if (isInvalidIp(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isInvalidIp(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        // HTTP_CLIENT_IP  有些代理服务器会加上此请求头。
        if (isInvalidIp(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (isInvalidIp(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        // X-Real-IP nginx代理一般会加上此请求头。
        if (isInvalidIp(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (isInvalidIp(ip)) {
            ip = request.getRemoteAddr();
            // 127.0.0.1 ipv4, 0:0:0:0:0:0:0:1 ipv6
            if (LOCAL_IPV4.equals(ip) || LOCAL_IPV6.equals(ip)) {
                // 根据网卡取本机配置的IP
                try {
                    InetAddress inetAddress = InetAddress.getLocalHost();
                    if(null != inetAddress) {
                        ip = inetAddress.getHostAddress();
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
        return ip;
    }

    /**
     * 判断 IP 是否无效
     * @param ip ip 地址
     * @return IP 地址为 null、空字符串、unknown 是： true
     */
    private static boolean isInvalidIp(String ip) {
        return null == ip || ip.trim().length() == 0 || "unknown".equalsIgnoreCase(ip);
    }

    /**
     * 获取本机 IP 地址
     *
     * @return String ip地址
     * @throws UnknownHostException,SocketException 未知名称或服务异常，网络异常
     */
    public static String getLocalIpAddress() throws UnknownHostException, SocketException {
        // 判断为windows系统
        if (isWindows()) {
            return InetAddress.getLocalHost().getHostAddress();
        } else {
            return getLinuxLocalIpAddress();
        }
    }

    /**
     * 获取 Linux下的 IP 地址
     *
     * @return String linux系统下的Ip地址
     * @throws UnknownHostException,SocketException 未知名称或服务异常，网络异常
     */
    private static String getLinuxLocalIpAddress() throws UnknownHostException, SocketException {
        InetAddress inetAddress = InetAddress.getLocalHost();
        if (!inetAddress.isLoopbackAddress()) {
            return inetAddress.getHostAddress();
        }
        String ip = "";
        NetworkInterface networkInterface;
        String name;
        Enumeration<InetAddress> enumIpAddr;
        String ipaddress;
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
            networkInterface = en.nextElement();
            name = networkInterface.getName();
            if (!name.contains("docker") && !name.contains("lo")) {
                for (enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        ipaddress = inetAddress.getHostAddress();
                        if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
                            ip = ipaddress;
                        }
                    }
                }
            }
        }
        return ip;
    }

    /**
     * WINDOWS系统
     */
    private static final String WINDOWS = "windows";

    /**
     * Linux系统
     */
    private static final String LINUX = "linux";

    /**
     * 操作系统属性名称
     */
    private static final String OS_NAME = "os.name";

    /**
     * 判断是否是windows操作系统
     *
     * @return boolean 【true：是】
     */
    public static boolean isWindows() {
        return System.getProperty(OS_NAME).toLowerCase().contains(WINDOWS);
    }

    /**
     * 判断是否是Linux操作系统
     *
     * @return boolean 【true：是】
     */
    public static boolean isLinux() {
        return System.getProperty(OS_NAME).toLowerCase().contains(LINUX);
    }

    /**
     * 获取服务器的硬盘使用情况
     *
     * @return String 硬盘的使用信息
     */
    public static String getDiskInfo() {
        StringBuilder sb = new StringBuilder();
        // 获取磁盘分区列表
        File[] roots = File.listRoots();
        for (File file : roots) {
            long totalSpace = file.getTotalSpace();
            long usableSpace = file.getUsableSpace();
            if (totalSpace > 0) {
                sb.append(file.getPath()).append("(总计：");
                sb.append(Math.round(((double) totalSpace / (1024 * 1024 * 1024)) * 100 / 100.0)).append("GB  ");
                if (Math.round((((double) usableSpace / (1024 * 1024 * 1024)) * 100) / 100.0) <= 1) {
                    sb.append("剩余：").append(Math.round((((double) usableSpace / (1024 * 1024)) * 100) / 100.0)).append("MB)<br>");
                } else {
                    sb.append("剩余：").append(Math.round((((double) usableSpace / (1024 * 1024 * 1024)) * 100) / 100.0)).append("GB)<br>");
                }
            }
        }
        return sb.toString();
    }

    /**
     * 获取服务器的内存使用情况
     *
     * @return String 内存的使用信息
     */
    public static String getMemoryInfo() {
        StringBuilder sb = new StringBuilder();
        OperatingSystemMXBean osmb = (OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();
        sb.append("&&&&内存情况：系统内存总计：").append(osmb.getTotalPhysicalMemorySize() / 1024 / 1024).append("MB，");
        sb.append("可用内存：").append(osmb.getFreePhysicalMemorySize() / 1024 / 1024).append("MB");
        return sb.toString();
    }

    /**
     * 获取服务器硬盘分区下的目录和文件列表
     *
     * @return String 服务器硬盘分区下的目录和文件列表
     */
    public static String getDiskFileList() {
        StringBuilder sb = new StringBuilder();
        String[] fileList;
        // 获取硬盘分区列表；
        File[] roots = File.listRoots();
        long totalSpace;
        for (File file : roots) {
            totalSpace = file.getTotalSpace();
            fileList = file.list();
            if (totalSpace > 0 && fileList != null && fileList.length > 0) {
                sb.append(file.getPath()).append("下目录和文件：");
                for (String s : fileList) {
                    sb.append(s).append("/n");
                }
            }
        }
        return sb.toString();
    }
}
