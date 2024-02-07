package com.blossom.common.iaas.blos;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.URLDecoder;
import com.blossom.common.base.exception.XzException500;
import com.blossom.common.iaas.AbstractOSManager;
import com.blossom.common.iaas.IaasEnum;
import com.blossom.common.iaas.IaasProperties;
import com.blossom.common.iaas.OSManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * blossom 本地图片存储
 *
 * @author xzzz
 */
@Slf4j
@Component
public class BLOSManager extends AbstractOSManager implements OSManager {

    private final String domain;
    private final String defaultPath;

    public BLOSManager(IaasProperties prop) {
        super(prop);
        if (prop == null) {
            String msg = "初始化 [BLOS] 错误, 请检查配置文件 [iaas] 是否配置";
            log.error(msg);
            throw new NullPointerException(msg);
        }
        if (!IaasEnum.BLOSSOM.getType().equals(prop.getOsType())) {
            String msg = String.format("初始化 [BLOS] 错误, 请检查配置文件 [iaas.osType] 配置项是否为 \"blossom\", 当前为 \"%s\"", prop.getOsType());
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (prop.getBlos() == null) {
            String msg = "初始化 [BLOS] 错误, 未配置 [BLOS] 参数, 请检查配置文件 [iaas.BLOS] 配置项";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        } else {
            domain = prop.getBlos().getDomain();
            defaultPath = prop.getBlos().getDefaultPath();
        }
    }

    @Override
    public String getDefaultPath() {
        return defaultPath;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    /**
     * 上传图片, 并返回文件的访问地址
     * <p>但需要注意的是, 调用方可以保存自己的文件地址前缀, 而不使用 {@link BLOSManager#getDomain()},
     *
     * @param filename    文件名
     * @param inputStream 输入流
     */
    @Override
    public String put(String filename, InputStream inputStream) {
        File file = FileUtil.newFile(filename);
        FileUtil.writeFromStream(inputStream, file);
        return getDomain() + filename;
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

    /**
     * 获取文件
     *
     * @param filename 文件名
     * @return 返回文件
     */
    @Override
    public File get(String filename) {
        filename = URLDecoder.decode(filename, StandardCharsets.UTF_8);
        File file = FileUtil.newFile(filename);
        if (!file.exists()) {
            throw new XzException500("文件[" + filename + "]不存在, 请检查文件名或文件路径");
        }
        return file;
    }

    /**
     * 删除文件
     *
     * @param filename 文件名
     * @return 是否删除
     */
    @Override
    public boolean delete(String filename) {
        File file = FileUtil.newFile(filename);
        return file.delete();
    }

    @Override
    public boolean deletePath(String pathname) {
        return FileUtil.del(pathname);
    }
}
