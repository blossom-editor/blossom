package com.blossom.backend.server.article.backup;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.auth.annotation.AuthIgnore;
import com.blossom.backend.base.param.ParamEnum;
import com.blossom.backend.base.param.ParamService;
import com.blossom.backend.base.param.pojo.ParamEntity;
import com.blossom.backend.server.article.backup.pojo.DownloadReq;
import com.blossom.backend.server.utils.DownloadUtil;
import com.blossom.common.base.enums.YesNo;
import com.blossom.common.base.exception.XzException404;
import com.blossom.common.base.exception.XzException500;
import com.blossom.common.base.pojo.R;
import com.blossom.common.base.util.SortUtil;
import lombok.AllArgsConstructor;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章 [Article]
 *
 * @author xzzz
 */
@RestController
@AllArgsConstructor
@RequestMapping("/article/backup")
public class ArticleBackupController {

    private final ParamService paramService;
    private final ArticleBackupService backupService;

    /**
     * 执行备份
     *
     * @param type    导出类型 MARKDOWN/HTML
     * @param toLocal 是否导出为本地图片映射 YES/NO
     */
    @GetMapping
    public R<ArticleBackupService.BackupFile> backup(
            @RequestParam("type") String type,
            @RequestParam("toLocal") String toLocal,
            @RequestParam(value = "articleId", required = false) Long articleId) {
        BackupTypeEnum typeEnum;
        if (StrUtil.isBlank(type)) {
            typeEnum = BackupTypeEnum.MARKDOWN;
        } else {
            typeEnum = BackupTypeEnum.valueOf(type.toUpperCase());
        }

        YesNo toLocalEnum;
        if (StrUtil.isBlank(toLocal)) {
            toLocalEnum = YesNo.NO;
        } else {
            toLocalEnum = YesNo.valueOf(toLocal.toUpperCase());
        }

        return R.ok(backupService.backup(AuthContext.getUserId(), typeEnum, toLocalEnum, articleId));
    }

    /**
     * 备份记录
     */
    @GetMapping("/list")
    public R<List<ArticleBackupService.BackupFile>> list() {
        return R.ok(backupService.listAll(AuthContext.getUserId())
                .stream()
                .sorted((b1, b2) -> SortUtil.dateSort.compare(b1.getDatetime(), b2.getDatetime()))
                .collect(Collectors.toList())
        );
    }

    /**
     * 下载压缩包
     *
     * @param filename 文件名称
     */
    @GetMapping("/download")
    public void download(@RequestParam("filename") String filename, HttpServletResponse response) {
        final ParamEntity param = paramService.getValue(ParamEnum.BACKUP_PATH);
        XzException500.throwBy(ObjUtil.isNull(param), ArticleBackupService.ERROR_MSG);
        final String rootPath = param.getParamValue();
        XzException500.throwBy(StrUtil.isBlank(rootPath), ArticleBackupService.ERROR_MSG);
        File file = new File(rootPath + "/" + filename);
        XzException404.throwBy(!file.exists(), "备份文件[" + filename + "]不存在");
        try (InputStream is = FileUtil.getInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(is)) {
            DownloadUtil.forceDownload(response, bis, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @AuthIgnore
    @GetMapping("/download/fragment")
    public ResponseEntity<ResourceRegion> downloadFragment(@RequestParam("filename") String filename,
                                                           HttpServletRequest request) {
        DownloadReq req = new DownloadReq();
        req.setFilename(filename);
        return downloadFragment(req, request);
    }

    @AuthIgnore
    @PostMapping("/download/fragment")
    public ResponseEntity<ResourceRegion> downloadFragment(@RequestBody DownloadReq req,
                                                           HttpServletRequest request) {
        final ParamEntity param = paramService.getValue(ParamEnum.BACKUP_PATH);
        XzException500.throwBy(ObjUtil.isNull(param), ArticleBackupService.ERROR_MSG);
        final String rootPath = param.getParamValue();
        XzException500.throwBy(StrUtil.isBlank(rootPath), ArticleBackupService.ERROR_MSG);
        String filename = rootPath + "/" + req.getFilename();
        File file = new File(filename);
        long contentLength = file.length();

        PathResource resource = new PathResource(filename);
        ResourceRegion resourceRegion;
        try {
            // Get the range header and validate it.
            String rangeHeader = request.getHeader(HttpHeaders.RANGE);
            List<HttpRange> ranges = HttpRange.parseRanges(rangeHeader);

            if (ranges.isEmpty() || ranges.get(0).equals(HttpRange.createByteRange(0))) {
                // Return full file.
                resourceRegion = new ResourceRegion(resource, 0, contentLength);
            } else {
                // Return partial content.
                HttpRange range = ranges.get(0);
                long start = range.getRangeStart(contentLength);
                long end = range.getRangeEnd(contentLength);
                // 最大一次 65536 byte
                long rangeLength = Math.min(65536L, end - start + 1);
                resourceRegion = new ResourceRegion(resource, start, rangeLength);
            }
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE).build();
        }

        String contentRange = "bytes " + resourceRegion.getPosition() + "-" +
                (resourceRegion.getPosition() + resourceRegion.getCount() - 1) + "/" + contentLength;

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)

                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(resourceRegion.getCount()))
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header(HttpHeaders.CONTENT_RANGE, contentRange)
                .body(resourceRegion);
    }


}
