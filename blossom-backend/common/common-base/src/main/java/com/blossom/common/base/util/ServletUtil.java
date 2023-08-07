package com.blossom.common.base.util;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * @author xzzz
 */
public class ServletUtil extends cn.hutool.extra.servlet.ServletUtil {

    private static final String UNKNOWN = "unknown";

    private static final String HEADER_USER_AGENT = "User-Agent";

    /**
     * 获取 HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes == null) {
            return null;
        }
        return servletRequestAttributes.getRequest();
    }

    /**
     * 获取 HttpServletResponse
     */
    public static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    /**
     * 判断是否ajax请求
     * spring ajax 返回含有 ResponseBody 或者 RestController注解
     *
     * @param handlerMethod HandlerMethod
     * @return 是否ajax请求
     */
    public static boolean isBody(HandlerMethod handlerMethod) {
        ResponseBody responseBody = handlerMethod.getMethodAnnotation(ResponseBody.class);
        return responseBody != null;
    }

    /**
     * 获取请求IP
     */
    public static String getIP() {
        return getIP(getRequest());
    }

    /**
     * 获取请求IP
     */
    public static String getIP(HttpServletRequest request) {
        Assert.notNull(request, "HttpServletRequest is null");
        String ip = request.getHeader("X-Requested-For");
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return StrUtil.isBlank(ip) ? null : ipWrapper(ip.split(",")[0]);
    }

    /**
     * 获取请求头 user-agent
     */
    public static String getUserAgent() {
        return getRequest().getHeader(HEADER_USER_AGENT);
    }

    /**
     * 获取请求头 user-agent
     */
    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader(HEADER_USER_AGENT);
    }

    /**
     * 获取请求方法
     */
    public static String getMethod() {
        return methodWrapper(Objects.requireNonNull(getRequest()).getMethod());
    }

    /**
     * 根据传入的文件名, 获取对应的图片 content-type, 文件不为图片文件, 则返回 ""
     *
     * @param filename 文件名
     * @return content-type
     */
    public static String getContentTypeImage(String filename) {
        String suffix = FileUtil.getSuffix(filename);
        if ("jpg".equals(suffix) || "jpeg".equals(suffix)) {
            return "image/jpeg";
        }
        if ("png".equals(suffix)) {
            return "image/png";
        }
        if ("gif".equals(suffix)) {
            return "image/gif";
        }
        if ("tiff".equals(suffix) || "tif".equals(suffix)) {
            return "image/tiff";
        }
        if ("jfif".equals(suffix)) {
            return "image/jpeg";
        }
        if ("ico".equals(suffix)) {
            return "image/x-icon";
        }
        return suffix;
    }

    /**
     * 获取请求URI
     * <p>URL：GET http://localhost:8080/user
     * <p>URI：/user
     */
    public static String getUri() {
        return getRequest().getRequestURI().toLowerCase();
    }

    /**
     * 将请求方法按统一长度返回
     * <p>只支持GET,POST,PUT,DELETE
     */
    public static String methodWrapper(String method) {
        switch (method) {
            case "GET":
                return "GET ";
            case "POST":
                return "POST";
            case "PUT":
                return "PUT ";
            case "DELETE":
                return "DEL ";
            default:
                return method;
        }
    }

    /**
     * 将IP按15位长度返回
     * <pre>{@code
     * "255.255.255.255" 将返回 "255.255.255.255"
     * "192.168.0.1"     将返回 "192.168.0.1    "
     * }</pre>
     */
    public static String ipWrapper(String ip) {
        int ipLength = 15;
        if (ip.length() >= ipLength) {
            return ip;
        }
        int difference = ipLength - ip.length();
        StringBuilder ipBuilder = new StringBuilder(ip);
        for (int i = 0; i < difference; i++) {
            ipBuilder.append(" ");
        }
        ip = ipBuilder.toString();
        return ip;
    }

    /**
     * 获取请求Body, 需要谨慎使用, Body只能获取一次
     */
    public static String getBodyString(ServletRequest request) {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = request.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

}
