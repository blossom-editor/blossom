package com.blossom.common.iaas.minio;


import cn.hutool.core.util.StrUtil;
import com.blossom.common.base.exception.XzException500;
import com.blossom.common.iaas.*;
import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


/**
 * minio
 *
 * @author Ake
 */
@Component
@Slf4j
public class MinioManager extends AbstractOSManager implements OSManager {
    private final String bucketName;
    private final String domain;
    private final MinioClient minioClient;

    public MinioManager(IaasProperties prop) {
        super(prop);
        if (prop == null) {
            String msg = "初始化 [BLOS] 错误, 请检查配置文件 [iaas] 是否配置";
            log.error(msg);
            throw new NullPointerException(msg);
        }
        if (!IaasEnum.MINIO.getType().equals(prop.getOsType())) {
            String msg = String.format("初始化 [BLOS] 错误, 请检查配置文件 [iaas.osType] 配置项是否为 \"minio\", 当前为 \"%s\"", prop.getOsType());
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (prop.getMinio() == null) {
            String msg = "初始化 [BLOS] 错误, 未配置 [BLOS] 参数, 请检查配置文件 [iaas.BLOS] 配置项";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        } else {
            minioClient = MinioClient
                    .builder()
                    .endpoint(prop.getMinio().getEndpoint())
                    .credentials(prop.getMinio().getAccessKey(), prop.getMinio().getSecretKey())
                    .build();
            //桶不存在，初始化桶
            this.bucketName = prop.getMinio().getBucket();
            this.domain = prop.getMinio().getDomain();
            boolean exists;
            try {
                exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
                if (!exists) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                }
            } catch (Exception e) {
                throw new XzException500(e.getMessage());
            }
        }

    }

    @Override
    public String getDefaultPath() {
        return "";
    }

    @Override
    public String getDomain() {
        // 是minio的ip+bucket名
        return domain + "/" + bucketName;
    }

    @Override
    public String put(String filename, InputStream inputStream) {

        filename = StrUtil.isBlank(filename) ? UUID.randomUUID() + ".jpg" : filename;
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)//保存文件的桶
                            .stream(inputStream, inputStream.available(), -1)//上传的文件信息：参数1-文件输入流 参数2-文件的字节数  参数3-分片 -1不分片
                            .object(filename) //桶内保存文件的路径+文件名称
                            .build()
            );
        } catch (Exception e) {
            throw new XzException500(e.getMessage());
        }

        return getDomain() + filename;
    }

    @Override
    public String put(String filename, MultipartFile file) {
        try {
            return put(filename, file.getInputStream());
        } catch (IOException e) {
            throw new XzException500(e.getMessage());
        }
    }

    @Override
    public boolean delete(String filename) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)  //删除文件所在的桶
                            .object(filename) //删除文件在桶内的路径 +文件名
                            .build()
            );

            return true;
        } catch (Exception e) {
            throw new XzException500(e.getMessage());
        }
    }
}