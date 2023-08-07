package com.blossom.backend.base.auth.filters;

import com.blossom.backend.base.auth.exception.AuthException;
import com.blossom.backend.base.auth.exception.AuthRCode;
import org.springframework.http.HttpMethod;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 请求链接防火墙
 *
 * @author xzzz
 * @since 0.0.1
 */
public class HttpFirewall {

    /**
     * 请求地址中不允许出现「.」的 URL 编码 %2e
     */
    private static final List<String> FORBIDDEN_ENCODED_PERIOD =
            Collections.unmodifiableList(Arrays.asList("%2e", "%2E"));

    /**
     * 请求地址中不允许出现「;」以及「;」的 URL 编码 %3b
     */
    private static final List<String> FORBIDDEN_SEMICOLON =
            Collections.unmodifiableList(Arrays.asList(";", "%3b", "%3B"));

    /**
     * 请求地址中不允许出现「/」的 URL 编码 %2f
     */
    private static final List<String> FORBIDDEN_FORWARDSLASH =
            Collections.unmodifiableList(Arrays.asList("%2f", "%2F"));

    /**
     * 请求地址中不允许出现「//」以及「//」的 URL 编码 %2f%2f
     */
    private static final List<String> FORBIDDEN_DOUBLE_FORWARDSLASH =
            Collections.unmodifiableList(Arrays.asList("//", "%2f%2f", "%2f%2F", "%2F%2f", "%2F%2F"));

    /**
     * 请求地址中不允许出现「\」以及「\」的 URL 编码 %5c
     */
    private static final List<String> FORBIDDEN_BACKSLASH =
            Collections.unmodifiableList(Arrays.asList("\\", "%5c", "%5C"));

    /**
     * 请求地址中不允许出现「」以及「」的 URL 编码 %00
     * (不是 null 而是空) 可转码 %00 查看, 转码后什么都没有
     */
    private static final List<String> FORBIDDEN_NULL = Collections.unmodifiableList(Arrays.asList("\0", "%00"));

    /**
     * 请求地址中不允许出现「%」以及「%」的 URL 编码 %25
     */
    private static final String PERCENT = "%";
    private static final String ENCODED_PERCENT = "%25";

    private final Set<String> encodedUrlBlockList = new HashSet<>();
    private final Set<String> decodedUrlBlockList = new HashSet<>();

    public HttpFirewall() {
        // 请求地址中不允许出现「.」的 URL 编码 %2e
        this.encodedUrlBlockList.addAll(FORBIDDEN_ENCODED_PERIOD);
        this.decodedUrlBlockList.addAll(FORBIDDEN_ENCODED_PERIOD);
        // 请求地址中不允许出现「;」以及「;」的 URL 编码 %3b
        this.encodedUrlBlockList.addAll(FORBIDDEN_SEMICOLON);
        this.decodedUrlBlockList.addAll(FORBIDDEN_SEMICOLON);
        // 请求地址中不允许出现「/」的 URL 编码 %2f
        this.encodedUrlBlockList.addAll(FORBIDDEN_FORWARDSLASH);
        this.decodedUrlBlockList.addAll(FORBIDDEN_FORWARDSLASH);
        // 请求地址中不允许出现「//」以及「//」的 URL 编码 %2f%2f
        this.encodedUrlBlockList.addAll(FORBIDDEN_DOUBLE_FORWARDSLASH);
        this.decodedUrlBlockList.addAll(FORBIDDEN_DOUBLE_FORWARDSLASH);
        // 请求地址中不允许出现「\」以及「\」的 URL 编码 %5c
        this.encodedUrlBlockList.addAll(FORBIDDEN_BACKSLASH);
        this.decodedUrlBlockList.addAll(FORBIDDEN_BACKSLASH);
        // 请求地址中不允许出现「」以及「」的 URL 编码 %00
        this.encodedUrlBlockList.addAll(FORBIDDEN_NULL);
        this.decodedUrlBlockList.addAll(FORBIDDEN_NULL);
        // 请求地址中不允许出现 %, 以及 % 的 URL 编码 %25
        this.encodedUrlBlockList.add(ENCODED_PERCENT);
        this.decodedUrlBlockList.add(PERCENT);
    }

    /**
     * 允许的请求方式
     */
    private final Set<String> allowedHttpMethods = createDefaultAllowedHttpMethods();
    private static Set<String> createDefaultAllowedHttpMethods() {
        Set<String> result = new HashSet<>();
        result.add(HttpMethod.DELETE.name());
        result.add(HttpMethod.GET.name());
        result.add(HttpMethod.HEAD.name());
        result.add(HttpMethod.OPTIONS.name());
        result.add(HttpMethod.PATCH.name());
        result.add(HttpMethod.POST.name());
        result.add(HttpMethod.PUT.name());
        return result;
    }

    /**
     * 执行防火墙
     * @param servletRequest request
     */
    public void wall(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        // 判断请求方式
        boolean isForbiddenMethod = rejectForbiddenHttpMethod(request);
        if (!isForbiddenMethod) {
            throw new AuthException(AuthRCode.REQUEST_REJECTED.getCode(), "请求方式错误 [" + request.getMethod() + "], 只允许如下请求: " + this.allowedHttpMethods);
        }

        // 是否是无效的 url 请求
        boolean isNormalized = isNormalized(request);
        if (!isNormalized) {
            throw new AuthException(AuthRCode.REQUEST_REJECTED.getCode(), "请求链接格式错误, 请检查!");
        }

        // 判断请求连接中的非法字符
        rejectedBlockListedUrls(request);

        // 如果请求地址中包含不可打印的 ASCII 字符，请求则会被拒绝
        boolean isContainsOnlyPrintableAsciiCharacters = containsOnlyPrintableAsciiCharacters(request);
        if (!isContainsOnlyPrintableAsciiCharacters) {
            throw new AuthException(AuthRCode.REQUEST_REJECTED.getCode(), "请求链接包含非法字符, 请检查!");
        }
    }

    /**
     * 判断请求方式
     * @param request request
     */
    private boolean rejectForbiddenHttpMethod(HttpServletRequest request) {
        return this.allowedHttpMethods.contains(request.getMethod());
    }

    /**
     * 标准化 URL 请求, 路径中不能包含
     *   ./
     *   /../
     *   /.
     *
     * @param request 请求
     * @return 是否
     */
    private boolean isNormalized(HttpServletRequest request) {
        if (!isNormalized(request.getRequestURI())) {
            return false;
        }
        if (!isNormalized(request.getContextPath())) {
            return false;
        }
        if (!isNormalized(request.getServletPath())) {
            return false;
        }
        if (!isNormalized(request.getPathInfo())) {
            return false;
        }
        return true;
    }

    /**
     * 标准化 URL 请求, 路径中不能包含
     *   ./
     *   /../
     *   /.
     *
     * @param path 请求路径
     * @return 是否
     */
    private boolean isNormalized(String path) {
        if (path == null) {
            return true;
        }
        for (int i = path.length(); i > 0;) {
            int slashIndex = path.lastIndexOf('/', i - 1);
            int gap = i - slashIndex;
            if (gap == 2 && path.charAt(slashIndex + 1) == '.') {
                // ".", "/./" or "/."
                return false;
            }
            if (gap == 3 && path.charAt(slashIndex + 1) == '.' && path.charAt(slashIndex + 2) == '.') {
                return false;
            }
            i = slashIndex;
        }
        return true;
    }

    /**
     * 如果请求地址中包含不可打印的 ASCII 字符，请求则会被拒绝
     * @param request request
     * @return 是否
     */
    private static boolean containsOnlyPrintableAsciiCharacters(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        int length = requestUri.length();
        for (int i = 0; i < length; i++) {
            char c = requestUri.charAt(i);
            if (c < '\u0020' || c > '\u007e') {
                return false;
            }
        }
        return true;
    }

    /**
     * 请求中不能包含 // 双斜杠
     * @param request request
     */
    private void rejectedBlockListedUrls(HttpServletRequest request) {
        for (String forbidden : this.encodedUrlBlockList) {
            if (encodedUrlContains(request, forbidden)) {
                throw new AuthException(AuthRCode.REQUEST_REJECTED.getCode(), "请求被拒绝, 请求路径中包含非法字符 [" + forbidden + "]");
            }
        }
        for (String forbidden : this.decodedUrlBlockList) {
            if (decodedUrlContains(request, forbidden)) {
                throw new AuthException(AuthRCode.REQUEST_REJECTED.getCode(), "请求被拒绝, 请求路径中包含非法字符 [" + forbidden + "]");
            }
        }
    }



    private boolean encodedUrlContains(HttpServletRequest request, String value) {
        if (valueContains(request.getContextPath(), value)) {
            return true;
        }
        return valueContains(request.getRequestURI(), value);
    }

    private boolean decodedUrlContains(HttpServletRequest request, String value) {
        if (valueContains(request.getServletPath(), value)) {
            return true;
        }
        if (valueContains(request.getPathInfo(), value)) {
            return true;
        }
        return false;
    }

    private boolean valueContains(String value, String contains) {
        return value != null && value.contains(contains);
    }

}
