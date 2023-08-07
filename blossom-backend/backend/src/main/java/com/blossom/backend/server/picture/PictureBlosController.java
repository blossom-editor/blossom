package com.blossom.backend.server.picture;

import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.IoUtil;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.auth.annotation.AuthIgnore;
import com.blossom.backend.server.picture.pojo.PictureEntity;
import com.blossom.common.base.pojo.R;
import com.blossom.common.base.util.ServletUtil;
import com.blossom.common.base.util.spring.AntPathMatcherUtil;
import com.blossom.common.iaas.OSManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * 图片上传下载, 当使用本地图片存储时, 简短的接口名有助于优化文档布局
 *
 * @author xzzz
 */
@Slf4j
@RestController
@AllArgsConstructor
public class PictureBlosController {

    private final PictureService baseService;
    private final OSManager osManager;
    /**
     * 大于 300KB 的图片才进行压缩
     */
    private final static long COMPRESS_MIN_SIZE = 307200;

    // region 上传下载

    /**
     * 上传文件
     *
     * @param file 文件
     * @apiNote 上传成功返回文件对象存储地址, 该接口需授权后才可使用。[Content-Type=multipart/form-data]
     */
    @PostMapping("/picture/file/upload")
    public R<String> uploadFile(@RequestParam("file") MultipartFile file,
                                @RequestParam(value = "filename", required = false) String filename,
                                @RequestParam(value = "pid", required = false) Long pid) {
        log.warn("上传文件: {}, {}", pid, file.getOriginalFilename());
        PictureEntity picture = baseService.insert(file, filename, pid, AuthContext.getUserId());
        if (picture.getId() == null) {
            return R.ok("上传失败");
        }
        try (InputStream inputStream = file.getInputStream()) {
            return R.ok(osManager.put(picture.getPathName(), inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.ok(picture.getUrl());
    }

    /**
     * 查看图片 [OP]
     *
     * @param filename 文件名
     * @param scale    图片分辨率缩放
     * @param quality  图片质量缩放
     */
    @AuthIgnore
    @GetMapping("/pic/{filename}/**")
    public void getFile(
            @PathVariable String filename,
            @RequestParam(value = "scale", required = false) Float scale,
            @RequestParam(value = "quality", required = false) Float quality,
            HttpServletRequest request,
            HttpServletResponse resp) {
        if (scale == null) {
            scale = 1f;
        }
        if (quality == null) {
            quality = 1f;
        }

        final String path = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
        final String bestMatchingPattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();
        String arguments = AntPathMatcherUtil.getAntPathMatcher().extractPathWithinPattern(bestMatchingPattern, path);
        if (!arguments.isEmpty()) {
            filename = "/" + filename + '/' + arguments;
        }
        File file = osManager.get(filename);

        InputStream ips = null;
        FastByteArrayOutputStream ops = null;
        try {
            // 图片大于 COMPRESS_MIN_SIZE 大小, 且请求进行压缩时
            if (file.length() > COMPRESS_MIN_SIZE && (scale < 1f || quality < 1f)) {
                try {
                    ops = new FastByteArrayOutputStream();
                    Thumbnails.of(file)
                            // 图片大小（长宽）压缩比例 从0-1，1表示原图
                            .scale(scale)
                            // 图片质量压缩比例 从0-1，越接近1质量越好
                            .outputQuality(quality)
                            .toOutputStream(ops);
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error(e.toString());
                    ips = new FileInputStream(file);
                    ops = IoUtil.read(ips, true);
                }
            } else {
                ips = new FileInputStream(file);
                ops = IoUtil.read(ips, true);
            }

            resp.setContentType(ServletUtil.getContentTypeImage(filename));
            OutputStream os = resp.getOutputStream();
            os.write(ops.toByteArray());
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ips != null) {
                    ips.close();
                }
                if (ops != null) {
                    ops.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // endregion
}
