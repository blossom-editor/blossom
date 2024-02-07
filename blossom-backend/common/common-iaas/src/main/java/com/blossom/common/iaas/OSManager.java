package com.blossom.common.iaas;


import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

/**
 * OSS 处理
 *
 * @author xzzz
 */
public interface OSManager {

    /**
     * 获取配置
     *
     * @return IAAS 配置对象
     */
    IaasProperties getProp();

    /**
     * 获取默认的上传路径, 返回结果结尾不包含 "/"
     * <p>如:
     * <pre>{@code /home/bl/img }</pre>
     *
     * @return 上传路径
     */
    String getDefaultPath();

    /**
     * 获取访问地址, 返回结果结尾不包含 "/"
     * <p>如:
     * <pre>{@code http://www.youdomain.com/ }</pre>
     *
     * @return 访问地址
     */
    String getDomain();

    /**
     * 上传文件
     *
     * @param filename    文件名
     * @param inputStream 输入流
     * @return 文件路径
     */
    String put(String filename, InputStream inputStream);

    /**
     * 上传文件
     *
     * @param filename 文件名
     * @param file     springboot 文件
     * @return 文件路径
     */
    String put(String filename, MultipartFile file);

    /**
     * 获取文件
     *
     * @param filename 文件名
     * @return 返回文件对象
     */
    default File get(String filename) {
        return null;
    }

    /**
     * 删除文件
     *
     * @param filename 文件名
     * @return 是否删除
     */
    boolean delete(String filename);

    /**
     * 删除文件夹
     *
     * @param pathname 文件夹路径
     */
    boolean deletePath(String pathname);
}
