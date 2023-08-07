package com.blossom.common.iaas.oss;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.*;
import com.blossom.common.base.exception.XzException500;
import com.blossom.common.iaas.AbstractOSManager;
import com.blossom.common.iaas.IaasEnum;
import com.blossom.common.iaas.IaasProperties;
import com.blossom.common.iaas.OSManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 阿里云
 *
 * @author xzzz
 */
@Slf4j
@Component
public class OSSManager extends AbstractOSManager implements OSManager {

    private final String endpoint;
    private final String accessKeyId;
    private final String accessKeySecret;
    private final String bucketName;
    private final String domain;
    private final String defaultPath;
    private final OSS client;

    public static void main(String[] args) {
        IaasProperties prop = new IaasProperties();
        IaasProperties.OSS oss = new IaasProperties.OSS();
        oss.setEndpoint("");
        oss.setAccessKeyId("");
        oss.setSecretAccessKey("");
        oss.setBucketName("");
        oss.setDomain("");
        oss.setDefaultPath("");
        prop.setOsType("");
        prop.setOss(oss);
        OSSManager ossManager = new OSSManager(prop);
    }

    /**
     * constructor
     *
     * @param prop 配置
     */
    public OSSManager(IaasProperties prop) {
        super(prop);
        if (prop == null) {
            String msg = "初始化 [OSS] 错误, 请检查配置文件 [iaas] 是否配置";
            log.error(msg);
            throw new NullPointerException(msg);
        }
        if (!IaasEnum.ALIBABA.getType().equals(prop.getOsType())) {
            String msg = String.format("初始化 [OSS] 错误, 请检查配置文件 [iaas.osType] 配置项是否为 \"alibaba\", 当前为 \"%s\"", prop.getOsType());
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (prop.getOss() == null) {
            String msg = "初始化 [OSS] 错误, 未配置 [OSS] 参数, 请检查配置文件 [iaas.oss] 配置项";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        } else {
            endpoint = prop.getOss().getEndpoint();
            accessKeyId = prop.getOss().getAccessKeyId();
            accessKeySecret = prop.getOss().getSecretAccessKey();
            bucketName = prop.getOss().getBucketName();
            domain = prop.getOss().getDomain();
            defaultPath = prop.getOss().getDefaultPath();
            client = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
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

    /**
     * 上传文件
     *
     * @param filename    文件名, 文件名带有路径也会拼接在默认路径后
     * @param inputStream 输入流
     * @return 文件外网访问url
     */
    @Override
    public String put(String filename, InputStream inputStream) {
        client.putObject(bucketName, pathAndFilename(filename), inputStream);
        return finalUrl(filename);
    }

    /**
     * 上传文件流
     *
     * @param filename 文件名
     * @param file     springboot 文件
     * @return 文件外网访问url
     */
    @Override
    public String put(String filename, MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return put(filename, inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new XzException500("文件上传失败");
        }
    }

    /**
     * 上传本地文件
     *
     * @param filePath   本地文件路径
     * @param filename   文件名, 如果需要指定 oss 目录, 则在文件名前增加目录, 且不要以 "/" 开头
     * @param expiration 访问路径的过期时间
     * @return 文件外网访问url
     */
    public String put(String filePath, String filename, Date expiration) {
        client.putObject(bucketName, pathAndFilename(filename), new File(filePath));
        String expireUrl = client.generatePresignedUrl(bucketName, filename, expiration).toString();
        return finalUrl(filename);
    }

    /**
     * 上传文件
     *
     * @param fileBytes  文件字节
     * @param filename   文件名, 如果需要指定 oss 目录, 则在文件名前增加目录, 且不要以 "/" 开头
     * @param expiration 访问路径的过期时间
     * @return 文件外网访问url
     */
    public String put(String filename, byte[] fileBytes) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileBytes)) {
            client.putObject(bucketName, pathAndFilename(filename), byteArrayInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return finalUrl(filename);
    }

    /**
     * 删除文件
     *
     * @param filename 文件名
     */
    @Override
    public boolean delete(String filename) {
        client.deleteObject(bucketName, pathAndFilename(filename));
        return true;
    }

    /**
     * 判断指定的文件是否存在
     *
     * @param fileName 文件名, 如果需要指定 oss 目录, 则在文件名前增加目录, 且不要以 "/" 开头
     * @return 是否存在
     */
    public boolean isExist(String filename) {
        return client.doesObjectExist(bucketName, pathAndFilename(filename));
    }

    /**
     * @param filename       合成图保存名字
     * @param oldFilename    底图地址
     * @param waterMark      水印图objectName（必须为OSS中的图片，例如：watermark.jpg）
     * @param waterMarkParam 水印参数
     * @return
     */
    public String waterMark(String filename, String oldFilename, String waterMark, WaterMarkParam waterMarkParam) {
        StringBuilder styleType = new StringBuilder("image/watermark,image_");
        styleType.append(BinaryUtil.toBase64String(waterMark.getBytes()));

        String transparency = waterMarkParam.getTransparency();
        if (StrUtil.isNotBlank(transparency)) {
            styleType.append(",t_").append(transparency);
        }
        String position = waterMarkParam.getPosition();
        if (StrUtil.isNotBlank(position)) {
            styleType.append(",g_").append(position);
        }
        Integer x = waterMarkParam.getX();
        if (x != null) {
            styleType.append(",x_").append(x);
        }
        Integer y = waterMarkParam.getY();
        if (y != null) {
            styleType.append(",y_").append(y);
        }
        Integer voffset = waterMarkParam.getVoffset();
        if (voffset != null) {
            styleType.append(",voffset_").append(voffset);
        }

        StringBuilder sbStyle = new StringBuilder();
        Formatter styleFormatter = new Formatter(sbStyle);

        styleFormatter.format("%s|sys/saveas,o_%s,b_%s", styleType,
                BinaryUtil.toBase64String(filename.getBytes()),
                BinaryUtil.toBase64String(bucketName.getBytes()));

        ProcessObjectRequest request = new ProcessObjectRequest(bucketName, oldFilename, sbStyle.toString());
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.processObject(request);
        return domain + filename;
    }

    /**
     * 阿里云批量下载文件,以ZIP压缩包下载
     *
     * @param fileNameList 文件名,作为key去获取文件
     * @param zipName      压缩包名称
     * @param response     response
     */
    public void uploadZipToResponse(List<String> fileNameList, String zipName, HttpServletResponse response) {
        if (CollUtil.isEmpty(fileNameList)) {
            throw new XzException500("下载文件列表为空");
        }
        try {
            response.setCharacterEncoding("UTF-8");
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            // ZIP压缩格式
            response.setContentType("application/zip;charset=UTF-8");
            // 设置在下载框默认显示的文件名
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(zipName, "UTF-8"));
            List<OSSObject> ossArr = new ArrayList<>(fileNameList.size());
            for (String fileName : fileNameList) {
                // 2.从阿里云OSS获取文件
                ossArr.add(client.getObject(bucketName, fileName.replace(domain, "")));
            }
            // 3.写入流
            downloadToOutputStream(ossArr, response.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException("下载文件异常: " + e.getMessage());
        }
    }

    /**
     * 写入流
     *
     * @param ossObj       oss对象集合
     * @param outputStream outputStream
     * @throws ServerException 异常信息
     */
    private void downloadToOutputStream(List<OSSObject> ossObj, OutputStream outputStream) throws ServerException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            // 将对象循环写入压缩包
            for (OSSObject ossObject : ossObj) {
                try (InputStream ips = ossObject.getObjectContent()) {
                    zipOutputStream.putNextEntry(new ZipEntry(standardFileName(ossObject.getKey())));
                    byte[] buffer = new byte[1024];
                    int r = 0;
                    // 5.写入
                    while ((r = ips.read(buffer)) != -1) {
                        zipOutputStream.write(buffer, 0, r);
                    }
                    // 异常不影响其余文件处理
                } catch (OSSException oos) {
                    log.error("查询文件异常: {}, 异常信息: {}", ossObject.getKey(), oos.getMessage());
                } catch (Exception e) {
                    log.error("文件写入压缩包异常: {}, 异常信息: {}", ossObject.getKey(), e.getMessage());
                }
            }
            zipOutputStream.flush();
        } catch (Exception e) {
            throw new ServerException("下载文件异常: " + e.getMessage());
        }
    }

    /**
     * 阿里云批量下载文件,以ZIP流输出到 outputStream
     *
     * @param fileNameList 文件名,作为key去获取文件
     * @param outputStream outputStream
     */
    public void uploadZipOutputStream(List<String> fileNameList, OutputStream outputStream) {
        if (CollUtil.isEmpty(fileNameList)) {
            throw new XzException500("下载文件列表为空");
        }
        try {
            List<OSSObject> ossArr = new ArrayList<>(fileNameList.size());
            for (String fileName : fileNameList) {
                // 2.从阿里云OSS获取文件
                ossArr.add(client.getObject(bucketName, fileName.replace(domain, "")));
            }
            // 3.写入流
            downloadToOutputStream(ossArr, outputStream);
        } catch (Exception e) {
            throw new RuntimeException("下载文件异常: " + e.getMessage());
        }
    }

    /**
     * 处理文件名,阿里云的key不包含HTTP路径,只有文件路径+文件名,需要对路径进行处理
     * 且在添加入ZIP中时会,若包含路径则会在压缩包中添加对应文件夹
     *
     * @param fileName 文件名
     */
    private String standardFileName(String fileName) {
        fileName = fileName.replace(domain, "");
        fileName = fileName.replace("/", "");
        return fileName;
    }

    /**
     * 获取oss文件url
     *
     * @param ossKey
     * @return
     */
    public String getUrl(String ossKey) {
        try {
            Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);
            URL url = client.generatePresignedUrl(bucketName, ossKey, expiration);
            if (url != null) {
                return url.toString();
            }
        } catch (Exception e) {
            log.error("获取oss文件url异常, ossKey: {}, 异常信息: {}", ossKey, e.getMessage());
            throw new RuntimeException("获取oss文件url 异常: " + e.getMessage());
        }
        return null;
    }

    /**
     * 初始化分片
     *
     * @param ossKey
     * @return
     */
    public String initMultiPartUpload(String ossKey) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {

            // 创建InitiateMultipartUploadRequest对象。
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, ossKey);
            // 初始化分片。
            InitiateMultipartUploadResult upresult = ossClient.initiateMultipartUpload(request);
            // 返回uploadId，它是分片上传事件的唯一标识，您可以根据这个uploadId发起相关的操作，如取消分片上传、查询分片上传等。
            return upresult.getUploadId();
        } catch (Exception e) {
            log.error("初始化分片异常, ossKey: {}, 异常信息: {}", ossKey, e.getMessage());
            throw new RuntimeException("初始化分片异常: " + e.getMessage());
        }
    }

    /**
     * 上传分片
     *
     * @param ossKey
     * @return
     */
    public PartETag uploadPart(String ossKey, String uploadId, InputStream instream, Integer curPartSize, Integer partNumber) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(bucketName);
            uploadPartRequest.setKey(ossKey);
            uploadPartRequest.setUploadId(uploadId);
            uploadPartRequest.setInputStream(instream);
            // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB。
            uploadPartRequest.setPartSize(curPartSize);
            // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出这个范围，OSS将返回InvalidArgument的错误码。
            uploadPartRequest.setPartNumber(partNumber);
            // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
            UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
            // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在redis中。
            return uploadPartResult.getPartETag();
        } catch (Exception e) {
            log.error("上传分片异常, ossKey: {}, uploadId: {}, curPartSize: {}, partNumber: {}, 异常信息: {}", ossKey, uploadId, curPartSize, partNumber, e.getMessage());
            throw new RuntimeException("上传分片异常: " + e.getMessage());
        }
    }

    /**
     * 分片合并成完整的文件
     *
     * @param ossKey
     * @return
     */
    public CompleteMultipartUploadResult completeMultipartUpload(String ossKey, String uploadId, List<PartETag> partETags) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        // 创建CompleteMultipartUploadRequest对象。
        // 在执行完成分片上传操作时，需要提供所有有效的partETags。OSS收到提交的partETags后，会逐一验证每个分片的有效性。当所有的数据分片验证通过后，OSS将把这些分片组合成一个完整的文件。
        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(bucketName, ossKey, uploadId, partETags);
        try {
            // 如果需要在完成文件上传的同时设置文件访问权限，请参考以下示例代码。
            // completeMultipartUploadRequest.setObjectACL(CannedAccessControlList.PublicRead);
            // 完成分片上传。
            return ossClient.completeMultipartUpload(completeMultipartUploadRequest);
        } catch (Exception e) {
            //分片合并异常清理原有分片数据
            AbortMultipartUploadRequest abortMultipartUploadRequest = new AbortMultipartUploadRequest(bucketName, ossKey, uploadId);
            ossClient.abortMultipartUpload(abortMultipartUploadRequest);
            throw new RuntimeException("分片合并异常: " + e.getMessage());
        }
    }

    /**
     * 取消分片上传
     *
     * @param ossKey
     * @return
     */
    public void abortMultipartUpload(String ossKey, String uploadId) {
        try {
            // 创建abortMultipartUploadRequest对象。
            AbortMultipartUploadRequest abortMultipartUploadRequest = new AbortMultipartUploadRequest(bucketName, ossKey, uploadId);
            client.abortMultipartUpload(abortMultipartUploadRequest);
        } catch (Exception e) {
            log.error("取消分片上传异常, ossKey: {}, 异常信息: {}", ossKey, e.getMessage());
            throw new RuntimeException("取消分片上传异常: " + e.getMessage());
        }
    }

    /**
     * 追加类型文件追加上传
     *
     * @param ossKey      文件存储路径和文件名
     * @param inputStream 文件流
     * @return 文件路径
     */
    public String appendObjectUploadFile(String ossKey, InputStream inputStream) {
        long position = 0L;
        try {
            ObjectMetadata objectMetadata = client.getObjectMetadata(bucketName, ossKey);
            position = objectMetadata.getContentLength();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        AppendObjectRequest appendObjectRequest = new AppendObjectRequest(bucketName, ossKey, inputStream);
        appendObjectRequest.setPosition(position);
        AppendObjectResult appendObjectResult = client.appendObject(appendObjectRequest);
        return domain + ossKey;
    }

}
