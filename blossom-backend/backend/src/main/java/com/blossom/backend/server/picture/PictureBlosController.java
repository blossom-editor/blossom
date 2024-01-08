package com.blossom.backend.server.picture;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.auth.annotation.AuthIgnore;
import com.blossom.backend.server.picture.pojo.PictureEntity;
import com.blossom.common.base.exception.XzException400;
import com.blossom.common.base.pojo.R;
import com.blossom.common.base.util.spring.AntPathMatcherUtil;
import com.blossom.common.iaas.OSManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 图片上传查看 [P#Blos]
 *
 * @author xzzz
 * @order 21
 * @apiNote 图片上传下载, 当使用本地图片存储时, 简短的接口名有助于优化文档布局
 */
@Slf4j
@RestController
@AllArgsConstructor
public class PictureBlosController {

    private final PictureService baseService;
    private final OSManager osManager;

    // region 上传下载

    /**
     * 上传文件
     *
     * @param file         文件
     * @param filename     文件名
     * @param pid          图片上级ID
     * @param repeatUpload 是否允许重复上传 @since 1.6.0
     */
    @PostMapping("/picture/file/upload")
    public R<String> uploadFile(@RequestParam("file") MultipartFile file,
                                @RequestParam(value = "filename", required = false) String filename,
                                @RequestParam(value = "pid", required = false) Long pid,
                                @RequestParam(value = "repeatUpload", required = false) Boolean repeatUpload) {
        if (repeatUpload == null) {
            repeatUpload = false;
        }
        log.warn("上传文件: {}, {}", pid, file.getOriginalFilename());
        PictureEntity picture = baseService.insert(file, filename, pid, AuthContext.getUserId(), repeatUpload);
        if (picture.getId() == null) {
            return R.ok("上传失败");
        }

        return R.ok(picture.getUrl());
    }

    private String getFilename(String filename, HttpServletRequest request) {
        final String path = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
        final String bestMatchingPattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();
        String arguments = AntPathMatcherUtil.getAntPathMatcher().extractPathWithinPattern(bestMatchingPattern, path);
        if (!arguments.isEmpty()) {
            filename = "/" + filename + '/' + arguments;
        }
        return filename;
    }

    /**
     * 检查文件路径
     *
     * @param filename 文件名称
     */
    private void checkFilename(String filename) {
        if (StrUtil.isBlank(filename)) {
            throw new XzException400("未知文件");
        }
        if (!filename.startsWith(osManager.getDefaultPath())) {
            // 如果图片前缀不是配置的前缀，则去数据库查询文件是否上传过。
            log.error("路径必须以配置的前缀开头");
            throw new XzException400("无法访问");
        }
        if (!filename.startsWith("/")) {
            log.error("路径必须是绝对路径");
            throw new XzException400("无法访问");
        }
        if (!FileUtil.exist(filename)) {
            log.error("文件不存在");
            throw new XzException400("未知文件");
        }
    }

    /**
     * 查看图片 [OP]
     *
     * @param filename 文件名, 注意: 如果使用 Blossom 保存图片, 那么生成的图片地址就是访问该接口的地址, 无需再拼接图片地址
     */
    @AuthIgnore
    @GetMapping("/pic/{filename}/**")
    public ResponseEntity<StreamingResponseBody> getFile(@PathVariable String filename,
                                                         HttpServletRequest request, HttpServletResponse resp) {
        filename = getFilename(filename, request);
        checkFilename(filename);
        // sendfile 方式下载图片
        sendfile(filename, resp);
        return ResponseEntity.ok(null);
    }


    private void sendfile(String filename, HttpServletResponse resp) {
        Path file = Paths.get(filename);

        try (FileChannel fileChannel = FileChannel.open(file); ServletOutputStream os = resp.getOutputStream()) {
            long size = fileChannel.size();
            String contentType = Files.probeContentType(file);
            if (contentType == null) {
                contentType = probContentType(filename);
            }
            resp.setContentType(contentType);
            resp.setContentLengthLong(size);
            // resp.setHeader(HttpHeaders.CACHE_CONTROL, "max-age=13600");

            long position = 0;
            WritableByteChannel channel = Channels.newChannel(os);
            while (size > 0) {
                long count = fileChannel.transferTo(position, size, channel);
                if (count > 0) {
                    position += count;
                    size -= count;
                }
            }
        } catch (IOException e) {
            log.info(e.getMessage());
        }
    }

    private String probContentType(String filename) {
        if (filename.endsWith(".svg")) {
            return "image/svg+xml";
        }
        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    // endregion
}
