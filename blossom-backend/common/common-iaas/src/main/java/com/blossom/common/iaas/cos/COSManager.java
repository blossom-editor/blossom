package com.blossom.common.iaas.cos;

import cn.hutool.core.util.StrUtil;
import com.blossom.common.base.exception.XzException500;
import com.blossom.common.iaas.AbstractOSManager;
import com.blossom.common.iaas.IaasEnum;
import com.blossom.common.iaas.IaasProperties;
import com.blossom.common.iaas.OSManager;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 腾讯COS
 *
 * @author xzzz
 */
@Slf4j
@Component
public class COSManager extends AbstractOSManager implements OSManager {

    private final String regionName;
    private final String accessKey;
    private final String secretKey;
    private final String bucketName;
    private final String domain;
    private final String defaultPath;
    private final COSClient client;

    public static void main(String[] args) {
        IaasProperties prop = new IaasProperties();
        IaasProperties.COS cos = new IaasProperties.COS();
        cos.setRegionName("");
        cos.setAccessKey("");
        cos.setSecretKey("");
        cos.setBucketName("");
        cos.setDomain("");
        cos.setDefaultPath("");
        prop.setOsType("");
        prop.setCos(cos);
        COSManager cosManager = new COSManager(prop);
    }

    public COSManager(IaasProperties prop) {
        super(prop);
        if (prop == null) {
            String msg = "初始化 [COS] 错误, 请检查配置文件 [iaas] 是否配置";
            log.error(msg);
            throw new NullPointerException(msg);
        }
        if (!IaasEnum.TENCENT.getType().equals(prop.getOsType())) {
            String msg = String.format("初始化 [COS] 错误, 请检查配置文件 [iaas.osType] 配置项是否为 \"tencent\", 当前为 \"%s\"", prop.getOsType());
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (prop.getCos() == null) {
            String msg = "初始化 [COS] 错误, 未配置 [COS] 参数, 请检查配置文件 [iaas.cos] 配置项";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        } else {
            regionName = prop.getCos().getRegionName();
            accessKey = prop.getCos().getAccessKey();
            secretKey = prop.getCos().getSecretKey();
            bucketName = prop.getCos().getBucketName();
            domain = prop.getCos().getDomain();
            defaultPath = prop.getCos().getDefaultPath();
        }

        COSCredentials cred = new BasicCOSCredentials(accessKey, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(regionName));
        client = new COSClient(cred, clientConfig);
    }

    @Override
    public String getDefaultPath() {
        return defaultPath;
    }

    @Override
    public String getDomain() {
        return this.domain;
    }

    @Override

    public String put(String filename, InputStream inputStream) {
        ObjectMetadata objectMetadata = initObjectMetadata(filename);
        final String finalFilename = defaultPath + (StrUtil.isBlank(filename) ? UUID.randomUUID().toString() + ".jpg" : filename);
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName,
                finalFilename,
                inputStream,
                objectMetadata);
        client.putObject(putObjectRequest).getETag();
        return domain + finalFilename;

    }

    @Override
    public String put(String filename, MultipartFile file) {
        String name = null;
        try (InputStream inputStream = file.getInputStream()) {
            return put(filename, inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new XzException500("COS 上传失败:" + e.getMessage());
        }
    }

    @Override
    public boolean delete(String fileName) {
        String objName = StrUtil.removeAll(fileName, domain);
        try {
            client.deleteObject(bucketName, objName);
            return true;
        } finally {
            client.shutdown();
        }
    }

    private ObjectMetadata initObjectMetadata(String fileName) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(matchContentType(fileName));
        return objectMetadata;
    }
}
