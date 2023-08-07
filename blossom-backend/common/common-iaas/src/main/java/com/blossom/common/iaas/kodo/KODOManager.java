package com.blossom.common.iaas.kodo;

import com.blossom.common.base.exception.XzException400;
import com.blossom.common.base.exception.XzException500;
import com.blossom.common.iaas.AbstractOSManager;
import com.blossom.common.iaas.IaasEnum;
import com.blossom.common.iaas.IaasProperties;
import com.blossom.common.iaas.OSManager;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * 七牛云
 *
 * @author xzzz
 */
@Slf4j
@Component
public class KODOManager extends AbstractOSManager implements OSManager {

    private final String endpoint;
    private final String accessKey;
    private final String secretKey;
    private final String bucketName;
    private final String domain;

    private final String defaultPath;

    private final Auth auth;
    private final Configuration cfg;
    private final UploadManager uploadManager;

    public static void main(String[] args) {
        IaasProperties prop = new IaasProperties();
        IaasProperties.KODO kodo = new IaasProperties.KODO();
        kodo.setAccessKey("");
        kodo.setSecretKey("");
        kodo.setBucketName("");
        kodo.setDomain("");
        kodo.setDefaultPath("");
        prop.setOsType("");
        prop.setKodo(kodo);
        KODOManager kodoManager = new KODOManager(prop);
    }

    public KODOManager(IaasProperties prop) {
        super(prop);
        if (prop == null) {
            String msg = "初始化 [KODO] 错误, 请检查配置文件 [iaas] 是否配置";
            log.error(msg);
            throw new NullPointerException(msg);
        }
        if (!IaasEnum.QINIU.getType().equals(prop.getOsType())) {
            String msg = String.format("初始化 [KODO] 错误, 请检查配置文件 [iaas.osType] 配置项是否为 \"qiniu\", 当前为 \"%s\"", prop.getOsType());
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (prop.getKodo() == null) {
            String msg = "初始化 [KODO] 错误, 未配置 [KODO] 参数, 请检查配置文件 [iaas.kodo] 配置项";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        } else {
            endpoint = "Region.huadong()";
            accessKey = prop.getKodo().getAccessKey();
            secretKey = prop.getKodo().getSecretKey();
            bucketName = prop.getKodo().getBucketName();
            domain = prop.getKodo().getDomain();
            defaultPath = prop.getKodo().getDefaultPath();

            cfg = new Configuration(Region.huadong());
            uploadManager = new UploadManager(cfg);
            auth = Auth.create(accessKey, secretKey);
        }
    }

    @Override
    public String getDefaultPath() {
        return this.defaultPath;
    }

    @Override
    public String getDomain() {
        return this.domain;
    }

    @Override
    public String put(String filename, InputStream inputStream) {
        String upToken = auth.uploadToken(bucketName);
        Response response;
        try {
            response = uploadManager.put(inputStream, subPrefixSeparator(filename), upToken, null, null);
            // 解析上传成功的结果
            String responseKey = Json.decode(response.bodyString()).get("key").toString();
            System.out.println(responseKey);
            return finalUrl(filename);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            throw new XzException400(ex.getMessage());
        }
    }

    @Override
    public String put(String filename, MultipartFile file) {
        try {
            return put(filename, file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new XzException500(e.getMessage());
        }
    }

    @Override
    public boolean delete(String filename) {
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(bucketName, subPrefixSeparator(filename));
        } catch (QiniuException ex) {
            throw new XzException400(ex.getMessage());
        }
        return true;
    }
}
