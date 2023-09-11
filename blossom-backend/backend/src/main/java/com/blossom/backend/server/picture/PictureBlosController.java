package com.blossom.backend.server.picture;

import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.auth.annotation.AuthIgnore;
import com.blossom.backend.server.picture.pojo.PictureEntity;
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
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
     * 查看图片 [OP]
     *
     * @param filename 文件名
     */
    @AuthIgnore
    @GetMapping("/pic/{filename}/**")
    public ResponseEntity<StreamingResponseBody> getFile(@PathVariable String filename,
                                                         HttpServletRequest request, HttpServletResponse resp) {
        filename = getFilename(filename, request);
        // sendfile 方式下载图片
        sendfile(filename, resp);
        return ResponseEntity.ok(null);

//        File file = osManager.get(filename);
//        resp.setContentType("");
//        resp.setContentLengthLong(file.length());
//        try {
//            InputStream is = new FileInputStream(file);
//            StreamingResponseBody body = os -> {
//                int nRead;
//                byte[] data = new byte[1024];
//                while ((nRead = is.read(data, 0, data.length)) != -1) {
//                    os.write(data, 0, nRead);
//                }
//            };
//            return ResponseEntity.ok().body(body);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return ResponseEntity.ok().body(null);
//        }
    }


    private void sendfile(String filename, HttpServletResponse resp) {
        Path file = Paths.get(filename);

        try (FileChannel fileChannel = FileChannel.open(file); ServletOutputStream os = resp.getOutputStream()) {
            long size = fileChannel.size();
            String contentType = Files.probeContentType(file);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            resp.setContentType(contentType);
            resp.setContentLengthLong(size);

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

    // endregion
}
