package com.blossom.common.iaas;

import cn.hutool.core.util.StrUtil;
import com.blossom.common.base.exception.XzException400;

/**
 * 抽象OSS处理类
 *
 * @author xzzz
 */
public abstract class AbstractOSManager implements OSManager {

    protected IaasProperties prop;

    public AbstractOSManager(IaasProperties prop) {
        this.prop = prop;
    }

    @Override
    public IaasProperties getProp() {
        return prop;
    }

    /**
     * 清除文件名前的 "/", 以及拼接 DefaultPath
     */
    protected String pathAndFilename(String filename) {
        if (StrUtil.isBlank(filename)) {
            throw new XzException400("文件名不得为空");
        }
        // 对象存储中, 文件名不能以 "/" 开头
        if (filename.startsWith("/")) {
            filename = filename.substring(1);
        }
        if (filename.startsWith(getDefaultPath())) {
            return filename;
        }
        return getDefaultPath() + filename;
    }

    protected String subPrefixSeparator(String filename) {
        if (StrUtil.isBlank(filename)) {
            throw new XzException400("文件名不得为空");
        }
        // 对象存储中, 文件名不能以 "/" 开头
        if (filename.startsWith("/")) {
            filename = filename.substring(1);
        }
        return filename;
    }

    /**
     * 处理最终的文件名
     *
     * @param filename
     * @return
     */
    protected String finalUrl(String filename) {
        return getDomain() + filename;
    }

    protected String matchContentType(String fileName) {
//        if (StrUtil.containsAny(fileName,".jpg",".png",".gif",".ico",".jpeg",".webp")) {
//            return  BjsConstants.CONTENT_TYPE_IMAGE_JPG;
//        } else if (StrUtil.containsAny(fileName,".word",".doc",".docx")) {
//            return "application/msword";
//        }

        if (StrUtil.containsAny(fileName, ".xls", ".xlsx")) {
            return "application/vnd.ms-excel";
        } else if (StrUtil.containsAny(fileName, ".xls", ".xlsx")) {
            return "application/pdf";
        } else if (StrUtil.containsAny(fileName, ".mp3")) {
            return "audio/mp3";
        } else if (StrUtil.containsAny(fileName, ".mp4")) {
            return "video/mpeg4";
        } else {
            return "application/octet-stream";
        }
    }

    @Override
    public boolean deletePath(String pathname) {
        return false;
    }
}
