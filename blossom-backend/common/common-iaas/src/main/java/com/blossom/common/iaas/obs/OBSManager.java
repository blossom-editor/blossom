package com.blossom.common.iaas.obs;

import com.blossom.common.base.exception.XzException500;
import com.blossom.common.iaas.AbstractOSManager;
import com.blossom.common.iaas.IaasEnum;
import com.blossom.common.iaas.IaasProperties;
import com.blossom.common.iaas.OSManager;
import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * 华为云
 *
 * @author xzzz
 */
@Slf4j
@Component
public class OBSManager extends AbstractOSManager implements OSManager {

    private final String endpoint;
    private final String accessKey;
    private final String secretKey;
    private final String bucketName;
    private final String domain;
    private final String defaultPath;
    private final ObsClient client;

    public static void main(String[] args) {
        IaasProperties prop = new IaasProperties();
        IaasProperties.OBS obs = new IaasProperties.OBS();
        obs.setEndpoint("");
        obs.setAccessKey("");
        obs.setSecretKey("");
        obs.setBucketName("");
        obs.setDomain("");
        obs.setDefaultPath("");
        prop.setOsType("");
        prop.setObs(obs);
        OBSManager obsManager = new OBSManager(prop);
    }

    public OBSManager(IaasProperties prop) {
        super(prop);
        if (prop == null) {
            String msg = "初始化 [OBS] 错误, 请检查配置文件 [iaas] 是否配置";
            log.error(msg);
            throw new NullPointerException(msg);
        }
        if (!IaasEnum.HUAWEI.getType().equals(prop.getOsType())) {
            String msg = String.format("初始化 [OBS] 错误, 请检查配置文件 [iaas.osType] 配置项是否为 \"huawei\", 当前为 \"%s\"", prop.getOsType());
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (prop.getObs() == null) {
            String msg = "初始化 [OBS] 错误, 未配置 [OBS] 参数, 请检查配置文件 [iaas.obs] 配置项";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        } else {
            endpoint = prop.getObs().getEndpoint();
            accessKey = prop.getObs().getAccessKey();
            secretKey = prop.getObs().getSecretKey();
            bucketName = prop.getObs().getBucketName();
            domain = prop.getObs().getDomain();
            defaultPath = prop.getObs().getDefaultPath();

            client = new ObsClient(accessKey, secretKey, endpoint);
        }
    }

    @Override
    public String getDefaultPath() {
        return defaultPath;
    }

    @Override
    public String getDomain() {
        return this.domain;
    }

    /**
     * 上传文件
     *
     * @param fileName 文件名称
     * @return 文件路径
     */
    @Override
    public String put(String filename, MultipartFile file) {
        try {
            return put(subPrefixSeparator(filename), file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new XzException500(e.getMessage());
        } catch (ObsException e) {
            log.error("[{}/{}]:{}", e.getResponseCode(), e.getErrorCode(), e.getErrorMessage());
            throw e;
        }
    }

    @Override
    public String put(String filename, InputStream inputStream) {
        client.putObject(bucketName, subPrefixSeparator(filename), inputStream);
        return finalUrl(filename);
    }

    @Override
    public boolean delete(String filename) {
        try {
            client.deleteObject(bucketName, subPrefixSeparator(filename));
            return true;
        } catch (ObsException e) {
            e.printStackTrace();
            return false;
        }
    }
}
