package com.blossom.common.base.util.okhttp;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.blossom.common.base.exception.XzException500;
import com.blossom.common.base.util.SortUtil;
import com.blossom.common.base.util.spi.ServiceLoaderUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;

/**
 * Http 请求工具类
 *
 * @author xzzz
 * @since 0.0.1
 */
@Slf4j
public class HttpUtil {

    public static final OkHttpClient OK_HTTP_CLIENT;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static final MediaType MULTIPART_FORM_DATA = MediaType.parse("multipart/form-data; charset=utf-8");

    static {
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient()
                .newBuilder()
                .sslSocketFactory(getSslSocketFactory(), getX509TrustManager())
                // 连接池大小
                // maxIdleConnections : 空闲连接的最大数量
                // keepAliveDuration : 连接空闲时间最多为 300 秒
                .connectionPool(new ConnectionPool(200, 300, TimeUnit.SECONDS))
                .connectTimeout(5L, TimeUnit.SECONDS)
                .readTimeout(5L, TimeUnit.SECONDS)
                .writeTimeout(5L, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);

        try {
            // 获取实现类
            ServiceLoader<OkHttpInterceptor> interceptors = ServiceLoaderUtil.getServiceLoader(OkHttpInterceptor.class);
            // 通过SPI获取
            if (CollUtil.isNotEmpty(interceptors)) {
                for (OkHttpInterceptor interceptor : interceptors) {
                    interceptor.instructions();
                    okHttpBuilder.addInterceptor(interceptor);
                }
            }
        } catch (Exception ignored) {
        }

        OK_HTTP_CLIENT = okHttpBuilder.build();

    }

    private static X509TrustManager getX509TrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    private static SSLSocketFactory getSslSocketFactory() {
        String algorithm = "TLS";
        try {
            //信任任何链接
            SSLContext sslContext = SSLContext.getInstance(algorithm);
            sslContext.init(null, new TrustManager[]{getX509TrustManager()}, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            throw new XzException500("没有" + algorithm + "这样的算法");
        } catch (KeyManagementException e) {
            throw new XzException500("秘钥管理异常:" + e.getMessage());
        }
    }

    // ----------------------------------- Get -----------------------------------

    /**
     * 同步 Get 请求
     *
     * @param url 请求路径
     * @return String 结果
     */
    public static String get(String url) {
        return newCall(buildRequest(url));
    }

    /**
     * 同步 Get 请求
     *
     * @param url       请求路径
     * @param basicAuth 基本认证 basicAuth[0]：username、basicAuth[1]：password
     * @return String 请求结果
     */
    public static String get(String url, String[] basicAuth) {
        String credential = Credentials.basic(basicAuth[0], basicAuth[1]);
        Map<String, String> headerMap = new HashMap<>(1);
        headerMap.put("Authorization", credential);
        return newCall(buildRequest(url, headerMap));
    }

    /**
     * 同步 Get 请求
     *
     * @param url      请求路径
     * @param paramMap 参数集合
     * @return String 请求结果
     */
    public static String get(String url, Map<String, String> paramMap) {
        return newCall(buildRequest(spliceGetRequestUrl(url, paramMap)));
    }

    /**
     * 同步 Get 请求
     *
     * @param url       请求路径
     * @param headerMap 请求头参数集合
     * @param paramMap  参数集合
     * @return String 请求结果
     */
    public static String get(String url, Map<String, String> headerMap, Map<String, String> paramMap) {
        return newCall(buildRequest(spliceGetRequestUrl(url, paramMap), headerMap));
    }

    /**
     * 异步 Get 方式请求
     *
     * @param url      请求路径
     * @param callback 回调响应处理对象
     */
    public static void getAsync(String url, Callback callback) {
        enqueueNewCall(OK_HTTP_CLIENT.newCall(buildRequest(url)), callback);
    }

    // ----------------------------------- Post -----------------------------------

    /**
     * 同步 Post 方式请求 json 提交参数
     *
     * @param url      请求路径
     * @param jsonBody 请求体 body 字符串
     * @return String 结果
     */
    public static String post(String url, String jsonBody) {
        return newCall(buildRequest(url, new HashMap<>(), createBody(jsonBody)));
    }

    /**
     * 同步 Post 方式请求 json 提交参数
     *
     * @param url      请求路径
     * @param jsonBody 请求体 body 字符串
     * @return byte数组结果
     */
    public static byte[] postResponseBytes(String url, String jsonBody) {
        return newCallResponseBytes(buildRequest(url, new HashMap<>(), createBody(jsonBody)));
    }

    /**
     * 同步 Post 请求
     *
     * @param url       请求路径
     * @param paramMap  请求参数集合
     * @param headerMap 对象头参数集合
     * @return String 响应结果
     */
    public static String post(String url, Map<String, String> headerMap, Map<String, String> paramMap) {
        return newCall(buildRequest(url, headerMap, paramMap));
    }

    /**
     * 同步 Post 方式请求 json 提交参数
     *
     * @param url       请求路径
     * @param jsonStr   请求参数 json 字符串
     * @param basicAuth 基本认证 basicAuth[0]：username、basicAuth[1]：password
     * @return String 结果
     */
    public static String post(String url, String jsonStr, String[] basicAuth) {
        String credential = Credentials.basic(basicAuth[0], basicAuth[1]);
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", credential);
        return post(url, jsonStr, headerMap);
    }

    /**
     * 同步 Post 请求
     *
     * @param url       请求路径
     * @param jsonStr   请求参数 json 字符串
     * @param headerMap 对象头参数集合
     * @return String 响应结果
     */
    public static String post(String url, String jsonStr, Map<String, String> headerMap) {
        return newCall(buildRequest(url, new HashMap<>(), createBody(jsonStr)));
    }

    /**
     * 同步 Post 方式请求 json 提交参数
     *
     * @param url      请求路径
     * @param paramMap 请求参数集合
     * @param appcode  APP 编码
     * @return String 结果
     */
    public static String post(String url, Map<String, String> paramMap, String appcode) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "APPCODE " + appcode);
        return post(url, paramMap, headerMap);
    }

    /**
     * 同步 Post 请求【文件上传】
     *
     * @param url       请求路径
     * @param paramMap  请求参数集合
     * @param headerMap 对象头参数集合
     * @return String 响应结果
     */
    public static String postFile(String url, Map<String, String> headerMap, Map<String, String> paramMap,
                                  String fileParam, File file) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (MapUtil.isNotEmpty(paramMap)) {
            String value;
            for (String key : paramMap.keySet()) {
                value = paramMap.get(key);
                if (StrUtil.isNotEmpty(value)) {
                    builder.addFormDataPart(key, value);
                }
            }
        }
        if (file.exists()) {
            builder.addFormDataPart(fileParam, file.getName(), RequestBody.create(MULTIPART_FORM_DATA, file));
        }

        MultipartBody body = builder.build();
        return newCall(buildRequest(url, headerMap, body));
    }

    /**
     * 异步post方式请求-json提交参数
     *
     * @param url     请求路径
     * @param jsonStr 请求参数
     */
    public static void postAsync(String url, String jsonStr, Callback callback) {
        enqueueNewCall(OK_HTTP_CLIENT.newCall(buildRequest(url, null, createBody(jsonStr))), callback);
    }

    /**
     * 异步post方式请求-form表单提交参数
     *
     * @param url      请求路径
     * @param paramMap 请求参数
     */
    public static void postAsync(String url, Map<String, String> paramMap, Callback callback) {
        enqueueNewCall(OK_HTTP_CLIENT.newCall(buildRequest(url, null, createBody(paramMap))), callback);
    }

    /**
     * 拼接 GET 请求的路径
     *
     * @param url      请求地址
     * @param paramMap 参数集合
     * @return String 请求地址
     */
    private static String spliceGetRequestUrl(String url, Map<String, String> paramMap) {
        if (MapUtil.isEmpty(paramMap)) {
            return url;
        }
        paramMap = SortUtil.sortMap(paramMap);
        StringBuilder builder = new StringBuilder();
        String value;
        for (String key : paramMap.keySet()) {
            value = paramMap.get(key);
            builder.append(key);
            builder.append('=');
            builder.append(value);
            builder.append('&');
        }
        String text = builder.toString();
        String paramStr = text.substring(0, text.length() - 1);
        if (StrUtil.isEmpty(paramStr)) {
            return url;
        }
        return url + "?" + paramStr;
    }

    /**
     * 执行请求
     *
     * @param request 请求
     * @return String 请求的结果
     */
    private static String newCall(Request request) {
        try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (null == body) {
                    return "";
                }
                return body.string();
            } else {
                throw new XzException500("请求[" + request.url() + "]失败, 结果: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new XzException500("请求[" + request.url() + "]异常");
        }
    }

    /**
     * 执行请求
     *
     * @param request 请求
     * @return String 请求的结果
     */
    private static byte[] newCallResponseBytes(Request request) {
        try (Response response = OK_HTTP_CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (null == body) {
                    return "".getBytes();
                }
                return body.bytes();
            } else {
                throw new XzException500("请求[" + request.url() + "]失败, 结果: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new XzException500("请求[" + request.url() + "]异常");
        }
    }

    /**
     * 将请求调用入队
     *
     * @param call     调用对象
     * @param callback 回调响应处理对象
     */
    private static void enqueueNewCall(Call call, Callback callback) {
        call.enqueue(callback);
    }

    /**
     * 封装 请求信息
     *
     * @param url           请求路径
     * @param multipartBody 多文本参数
     * @param headerMap     对象头参数
     * @return Request 请求对象
     */
    private static Request buildRequest(String url, Map<String, String> headerMap, MultipartBody multipartBody) {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        // 设置通用请求属性为默认浏览器编码类型
        builder.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        if (MapUtil.isNotEmpty(headerMap)) {
            String value;
            for (String key : headerMap.keySet()) {
                value = headerMap.get(key);
                if (StrUtil.isNotEmpty(value)) {
                    builder.header(key, value);
                }
            }
        }
        if (null != multipartBody) {
            builder.post(multipartBody);
        }
        return builder.build();
    }

    /**
     * 封装 请求信息
     *
     * @param url 请求路径
     * @return Request 请求对象
     */
    private static Request buildRequest(String url) {
        return buildRequest(url, null);
    }

    /**
     * 封装 请求信息
     *
     * @param url       请求路径
     * @param headerMap 请求头参数集合
     * @return Request 请求对象
     */
    private static Request buildRequest(String url, Map<String, String> headerMap) {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        // 设置通用请求属性为默认浏览器编码类型
        builder.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        if (MapUtil.isNotEmpty(headerMap)) {
            String value;
            for (String key : headerMap.keySet()) {
                value = headerMap.get(key);
                if (StrUtil.isNotEmpty(value)) {
                    builder.header(key, value);
                }
            }
        }
        return builder.build();
    }

    /**
     * 拼接 Post 请求参数
     *
     * @param body 参数 Json 字符串
     * @return String 文本
     */
    private static RequestBody createBody(String body) {
        if (StrUtil.isEmpty(body)) {
            return null;
        }
        return RequestBody.create(JSON, body);
    }

    /**
     * 拼接 Post 请求参数
     *
     * @param paramMap 参数集合
     * @return String 文本
     */
    private static RequestBody createBody(Map<String, String> paramMap) {
        if (MapUtil.isEmpty(paramMap)) {
            return null;
        }
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        String value;
        for (String key : paramMap.keySet()) {
            value = paramMap.get(key);
            if (StrUtil.isNotEmpty(value)) {
                formBodyBuilder.add(key, value);
            }
        }
        return formBodyBuilder.build();
    }

    /**
     * 封装 请求信息
     *
     * @param url       请求路径
     * @param paramMap  请求参数集合
     * @param headerMap 对象头参数集合
     * @return Request 请求对象
     */
    private static Request buildRequest(String url, Map<String, String> headerMap, Map<String, String> paramMap) {
        return buildRequest(url, headerMap, createBody(paramMap));
    }

    /**
     * 封装 请求信息
     *
     * @param url         请求路径
     * @param requestBody 请求参数
     * @param headerMap   对象头参数
     * @return Request 请求对象
     */
    private static Request buildRequest(String url, Map<String, String> headerMap, RequestBody requestBody) {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        // 设置通用请求属性为默认浏览器编码类型
        builder.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        if (MapUtil.isNotEmpty(headerMap)) {
            String value;
            for (String key : headerMap.keySet()) {
                value = headerMap.get(key);
                if (StrUtil.isNotEmpty(value)) {
                    builder.header(key, value);
                }
            }
        }
        if (null != requestBody) {
            builder.post(requestBody);
        }
        return builder.build();
    }

}
