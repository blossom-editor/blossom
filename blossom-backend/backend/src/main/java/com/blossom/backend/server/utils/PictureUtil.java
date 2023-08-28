package com.blossom.backend.server.utils;

import com.blossom.backend.server.FolderTypeEnum;
import com.blossom.backend.server.doc.pojo.DocTreeRes;
import net.coobird.thumbnailator.Thumbnails;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 图片工具类
 *
 * @author xzzz
 */
public class PictureUtil {

    /**
     * 获取默认图片文件夹
     *
     * @return 默认图片文件夹
     */
    public static DocTreeRes getDefaultFolder(Long userId) {
        DocTreeRes defaultFolder = new DocTreeRes();
        defaultFolder.setS((int) (userId * -1));
        defaultFolder.setI(userId * -1);
        defaultFolder.setP(0L);
        defaultFolder.setN("\uD83C\uDF0C 默认文件夹");
        defaultFolder.setO(0);
        defaultFolder.setT(new ArrayList<>());
        defaultFolder.setIcon("");
        defaultFolder.setTy(FolderTypeEnum.PICTURE.getType());
        defaultFolder.setStar(0);
        return defaultFolder;
    }

    public static void compress(String path, String to) {
        File file = new File(path);
        File toFile = new File(to);
        // 图片大于 COMPRESS_MIN_SIZE 大小, 且请求进行压缩时
        try {
            Thumbnails.of(file)
                    // 图片大小（长宽）压缩比例 从0-1，1表示原图
                    .scale(0.5)
                    // 图片质量压缩比例 从0-1，越接近1质量越好
                    .outputQuality(0.8)
                    .toFile(toFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        compress(
                "C:\\Users\\Administrator\\Desktop\\{964B32FD-1790-48bc-A6E4-486C3614F929}.png",
                "C:\\Users\\Administrator\\Desktop\\{964B32FD-1790-48bc-A6E4-486C3614F929}.png"
        );
    }
}
