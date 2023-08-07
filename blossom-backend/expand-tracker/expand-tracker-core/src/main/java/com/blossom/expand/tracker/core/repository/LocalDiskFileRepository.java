package com.blossom.expand.tracker.core.repository;

import com.blossom.expand.tracker.core.SpanNode;
import com.blossom.expand.tracker.core.TrackerProperties;
import com.blossom.common.base.util.json.JsonUtil;
import com.blossom.common.base.util.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;

/**
 * 本地存储追踪信息
 *
 * @author xzzz
 */
@Slf4j
public class LocalDiskFileRepository implements TrackerRepository, EnvironmentAware {

    private static final String USER_HOME = "user.dir";

    private static String logBaseDir;

    private final TrackerProperties.Disk diskProperties;

    private TrackerFileWriter trackerFileWriter;

    private Environment env;

    public LocalDiskFileRepository(TrackerProperties properties) {
        log.info("[TRACKERS] 开启追踪信息本地磁盘持久化");
        if (properties.getRepository().getDisk() == null) {
            diskProperties = new TrackerProperties.Disk();
        } else {
            diskProperties = properties.getRepository().getDisk();
        }
    }

    @PostConstruct
    public void init() {
        // 获取配置的日志路径
        logBaseDir = diskProperties.getLogDir();
        // 路径判断增加前缀
        logBaseDir = addSeparator(logBaseDir);
        // 判断文件夹是否存在, 不存在则创建
        File dir = new File(logBaseDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.err.println("ERROR: create Sentinel log base directory error: " + logBaseDir);
            }
        }
        String appName = env.getProperty(SpringUtil.APP_NAME);

        trackerFileWriter = new TrackerFileWriter(
                diskProperties.getSingleFileSize(),
                diskProperties.getTotalFileCount(),
                logBaseDir,
                appName,
                diskProperties.getUsePid());
    }

    @Override
    public boolean save(List<SpanNode> spanNodes) {
        StringBuilder sb = new StringBuilder();
        for (SpanNode spanNode : spanNodes) {
            sb.append(spanNode.getAppName()).append("|");
            sb.append(spanNode.getSpanStart()).append("|");
            sb.append(spanNode.getSpanEnd()).append("|");
            sb.append(spanNode.getTraceId()).append("|");
            sb.append(spanNode.getSpanId()).append("|");
            sb.append((spanNode.getSpanParentId() == null ? "" : spanNode.getSpanParentId())).append("|");
            sb.append(spanNode.getSpanInterval()).append("|");
            sb.append(spanNode.getSpanType()).append("|");
            sb.append(spanNode.getSpanName()).append("|");

            // 最终以 record + /r/n结尾
            sb.append(JsonUtil.toJson(spanNode.getRecords())).append("\r\n");
        }
        try {
            trackerFileWriter.write(System.currentTimeMillis(), sb.toString());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long expire() {
        return 0;
    }

    public static String addSeparator(String dir) {
        if (!dir.endsWith(File.separator)) {
            dir += File.separator;
        }
        return System.getProperty(USER_HOME) + dir;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}
