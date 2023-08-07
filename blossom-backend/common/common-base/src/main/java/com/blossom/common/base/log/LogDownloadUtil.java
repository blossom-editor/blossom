package com.blossom.common.base.log;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.blossom.common.base.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.system.ApplicationHome;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 下载日志文件
 *
 * @author xzzz
 */
@Slf4j
public class LogDownloadUtil {

    private static final List<String> downloadLogFileNames = new ArrayList<String>() {{
        this.add("info.log");
        this.add("error.log");
        this.add("slowsql.log");
    }};

    public static void download(HttpServletResponse response, Class<?> clazz) {
        ApplicationHome home = new ApplicationHome(clazz);
        File dir = home.getDir();
        log.info("jar 包所在路径:{}", dir);
        String logsPath = null;

        if (SystemUtil.isWindows()) {
            logsPath = dir.getAbsolutePath();
            for (int i = 0; i < 3; i++) {
                logsPath = StrUtil.sub(logsPath, 0, StrUtil.lastIndexOf(logsPath, "\\", logsPath.length(), true));
            }
            logsPath = logsPath + File.separator + "logs";
        } else if (SystemUtil.isLinux()) {
            logsPath = dir.getAbsolutePath() + File.separator + "logs";
        }

        if (StrUtil.isBlank(logsPath)) {
            return;
        }

        File file = FileUtil.newFile(logsPath);
        File[] files = FileUtil.ls(file.getAbsolutePath());

        List<File> downloadLogs = new ArrayList<>();
        for (File logFile : files) {
            if (downloadLogFileNames.contains(logFile.getName())) {
                downloadLogs.add(logFile);
            }
        }
        if (CollUtil.isNotEmpty(downloadLogs)) {
            File zip = new File(logsPath + File.separator + "logzip.zip");
            ZipUtil.zip(zip, false, ArrayUtil.toArray(downloadLogs, File.class));
            ServletUtil.write(response, zip);
        }
    }
}
