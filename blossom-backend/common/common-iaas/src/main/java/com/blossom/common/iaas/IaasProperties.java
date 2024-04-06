package com.blossom.common.iaas;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;


/**
 * IAAS 配置项
 *
 * @author xzzz
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "project.iaas")
public class IaasProperties {
    /**
     * 对象存储类型
     */
    private String osType;
    /**
     * 阿里 OSS 配置
     */
    private OSS oss;
    /**
     * 华为 OBS 配置
     */
    private OBS obs;
    /**
     * 腾讯 COS 配置
     */
    private COS cos;
    /**
     * 七牛 KODO 配置
     */
    private KODO kodo;
    /**
     * IRDA BLOS 配置
     */
    private BLOS blos;

    /**
     * Ali OSS 配置
     */
    @Data
    public static class OSS {
        /**
         * oss endpoint
         */
        private String endpoint;
        /**
         * oss accessKeyId
         */
        private String accessKeyId;
        /**
         * oss accessKeySecret
         */
        private String secretAccessKey;
        /**
         * oss bucket名称
         */
        private String bucketName;
        /**
         * oss 的访问地址, 可以通过域名映射
         */
        private String domain;
        /**
         * oss 默认上传地址, 不填则默认上传至 bucket 根目录下, 以 / 结尾
         */
        private String defaultPath;
    }

    /**
     * 华为OBS配置
     */
    @Data
    public static class OBS {
        /**
         * obs endpoint
         */
        private String endpoint;
        /**
         * obs accessKeyId
         */
        private String accessKey;
        /**
         * obs secretKey
         */
        private String secretKey;
        /**
         * obs bucket名称
         */
        private String bucketName;
        /**
         * obs 的访问地址, 可以通过域名映射, 不要以 / 结尾
         */
        private String domain;
        /**
         * obs 默认上传地址, 不填则默认上传至 bucket 根目录下, 不要以 / 结尾
         */
        private String defaultPath;
    }

    /**
     * 腾讯COS配置
     */
    @Data
    public static class COS {
        /**
         * cos regionName
         */
        private String regionName;
        /**
         * cos accessKey
         */
        private String accessKey;
        /**
         * cos secretKey
         */
        private String secretKey;
        /**
         * cos bucket名称
         */
        private String bucketName;
        /**
         * cos 的访问地址, 可以通过域名映射, 不要以 / 结尾
         */
        private String domain;
        /**
         * cos 默认上传地址, 不填则默认上传至 bucket 根目录下, 不要以 / 结尾
         */
        private String defaultPath;
    }

    /**
     * 七牛KODO配置
     */
    @Data
    public static class KODO {
        // /**
        //  * kodo regionName
        //  */
        // private String regionName;
        /**
         * kodo accessKey
         */
        private String accessKey;
        /**
         * kodo secretKey
         */
        private String secretKey;
        /**
         * kodo bucket名称
         */
        private String bucketName;
        /**
         * kodo 的访问地址, 可以通过域名映射, 不要以 / 结尾
         */
        private String domain;
        /**
         * kodo 默认上传地址, 不填则默认上传至 bucket 根目录下, 不要以 / 结尾
         */
        private String defaultPath;
    }

    /**
     * IDRA blossom Object storage
     */
    @Data
    public static class BLOS {
        /**
         * BLOS 查看图片的接口的地址, 默认在 PictureController#getFile() 方法中, 末尾带有 "/" 会自动清除
         *
         * @deprecated 该配置项已转移至系统配置 base_sys_param {@link ParamEnum#BLOSSOM_OBJECT_STORAGE_DOMAIN},
         * 但在 base_sys_param 为配置时仍然生效
         */
        @Deprecated
        private String domain;
        /**
         * BLOS 默认上传地址, 不能为空, 注意不同系统的区分, 末尾带有 "/" 会自动清除
         */
        private String defaultPath;
    }


    @PostConstruct
    public void init() {
        if (oss != null) {
            oss.setDomain(formatDomain(oss.getDomain()));
            oss.setDefaultPath(formatPath(oss.getDefaultPath()));
        }
        if (obs != null) {
            obs.setDomain(formatDomain(obs.getDomain()));
            obs.setDefaultPath(formatPath(obs.getDefaultPath()));
        }
        if (cos != null) {
            cos.setDomain(formatDomain(cos.getDomain()));
            cos.setDefaultPath(formatPath(cos.getDefaultPath()));
        }
        if (kodo != null) {
            kodo.setDomain(formatDomain(kodo.getDomain()));
            kodo.setDefaultPath(formatPath(kodo.getDefaultPath()));
        }
        if (blos != null) {
            String domain = formatDomain(blos.getDomain());
            if (!StrUtil.endWith(domain, "/pic")) {
                domain = domain + "/pic";
            }
            blos.setDomain(domain);
            blos.setDefaultPath(formatPath(blos.getDefaultPath()));
        }
    }

    /**
     * 会将末尾的 "/" 删除
     */
    private String formatDomain(String str) {
        if (StrUtil.isNotBlank(str) && StrUtil.endWith(str, "/")) {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }

    private String formatPath(String str) {
        if (StrUtil.isNotBlank(str) && StrUtil.endWith(str, "/")) {
            str = str.substring(0, str.length() - 1);
        }
        if (StrUtil.isNotBlank(str) && !StrUtil.startWith(str, "/")) {
            str = "/" + str;
        }
        return str;
    }

}
