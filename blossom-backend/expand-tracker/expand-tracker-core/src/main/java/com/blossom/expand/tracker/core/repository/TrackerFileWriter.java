package com.blossom.expand.tracker.core.repository;

import com.blossom.common.base.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 指标文件内容写入, 文件将在JVM停止后终止写入, 并且不会继续在文件后添加内容, 即使文件大小不满足 {@link TrackerFileWriter#singleFileSize}
 * 也会重新创建全新的文件, 并在文件添加标识
 *
 * <h2>二. Demo</h2>
 * <ol>
 *     <li>单个文件大小为 [ 20MB ] </li>
 *     <li>最多保留 [ 100 ] 个文件</li>
 *     <li>路径为 [ D:\\logs\\ ] </li>
 *     <li>文件名为 [ testApp-metrics.log.yyyy-MM-dd.num ]</li>
 * </ol>
 * <pre>{@code
 * public static void main(String[] args) {
 *     MetricFileWriter writer = new MetricFileWriter(
 *      167772160,100,"D:\\logs\\","testApp");
 *     for (int i = 1; i <= 100; i++) {
 *         try {
 *             writer.write(System.currentTimeMillis(), "line" + i + "\r\n");
 *         } catch (Ex e) {
 *             e.printStackTrace();
 *         }
 *     }
 * }
 * }</pre>
 */
@Slf4j
@SuppressWarnings("all")
public class TrackerFileWriter {

    private static final String CHARSET = "UTF-8";

    /**
     * 指标度量文件名后缀
     * 文件名格式如下:
     * <pre>{@code
     * testApp-metrics.log.2022-10-30
     * testApp-metrics.log.2022-10-30.1
     * testApp-metrics.log.2022-10-30.2
     * testApp-metrics.log.2022-10-31
     * testApp-metrics.log.2022-10-31.1
     * }</pre>
     * {@link MetricFileNameComparator} 的实现依赖于指标度量文件名, 因此在更改度量文件名时应该小心.
     */
    public static final String METRIC_FILE = "tracker.log";

    /**
     * 指标度量文件的索引后缀
     * 文件名格式如下:
     * <pre>{@code
     * testApp-metrics.log.2022-10-30.idx
     * testApp-metrics.log.2022-10-30.1.idx
     * testApp-metrics.log.2022-10-30.2.idx
     * testApp-metrics.log.2022-10-31.idx
     * testApp-metrics.log.2022-10-31.1.idx
     * }</pre>
     */
    public static final String METRIC_FILE_INDEX_SUFFIX = ".idx";

    /**
     * 文件名比较器
     */
    public static final Comparator<String> METRIC_FILE_NAME_CMP = new MetricFileNameComparator();

    /**
     * 是否允许在文件后追加内容, 为保证写入效率, 所以每次启动代码都会新建文件, 即不允许在文件后追加内容
     */
    private static final boolean append = false;

    /**
     * 排除时差干扰
     */
    private long timeSecondBase;

    /**
     * 文件所在目录
     */
    private final String baseDir;

    /**
     * 文件名
     */
    private String baseFileName;

    /**
     * 文件前缀
     */
    private final String appName;

    /**
     * file must exist when writing
     */
    private File curMetricFile;
    private File curMetricIndexFile;

    private FileOutputStream outMetric;
    private DataOutputStream outIndex;
    private BufferedOutputStream outMetricBuf;
    /**
     * 单个文件大小, byte
     * <p>10MB: 83886080
     * <p>20MB: 167772160
     * <p>30MB: 251658240
     */
    private final long singleFileSize;

    /**
     * 保留的文件总数
     */
    private final int totalFileCount;

    /**
     * 应用 pid
     */
    private final int pid = SystemUtil.getPid();

    private final boolean usePid;

    /**
     * 秒级统计，忽略毫秒数。
     */
    private long lastSecond = -1;

    /**
     * @param singleFileSize 单个文件大小
     * @param totalFileCount 总文件大小
     * @param baseDir        文件路径
     * @param appName        文件名称
     */
    public TrackerFileWriter(long singleFileSize, int totalFileCount, String baseDir, String appName, boolean usePid) {
        if (singleFileSize <= 0 || totalFileCount <= 0) {
            throw new IllegalArgumentException();
        }
        log.info("[TRACKERS] 创建 FileWriter, 单文件大小[{}MB], 最大文件数[{}]", (singleFileSize / 8 / 1024 / 1024), totalFileCount);

        this.usePid = usePid;
        this.baseDir = baseDir;
        this.appName = appName;
        File dir = new File(baseDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        long time = System.currentTimeMillis();
        this.lastSecond = time / 1000;
        this.singleFileSize = singleFileSize;
        this.totalFileCount = totalFileCount;
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.timeSecondBase = df.parse("1970-01-01 00:00:00").getTime() / 1000;
        } catch (Exception e) {
            log.warn("[TRACKERS] 创建 FileWriter 失败", e);
        }
    }

    /**
     * 如果传入了time，就认为nodes中所有的时间时间戳都是time.
     */
    public synchronized void write(long time, String oneLineMsg) throws Exception {
        if (oneLineMsg == null) {
            return;
        }
        // first write, should create file
        if (curMetricFile == null) {
            baseFileName = formMetricFileName(appName, pid);
            closeAndNewFile(nextFileNameOfDay(time));
        }
        if (!(curMetricFile.exists() && curMetricIndexFile.exists())) {
            closeAndNewFile(nextFileNameOfDay(time));
        }

        long second = time / 1000;
        if (second < lastSecond) {
            // 时间靠前的直接忽略，不应该发生。
        } else if (second == lastSecond) {
            outMetricBuf.write(oneLineMsg.getBytes(CHARSET));
            outMetricBuf.flush();
            if (!validSize()) {
                closeAndNewFile(nextFileNameOfDay(time));
            }
        } else {
            writeIndex(second, outMetric.getChannel().position());
            if (isNewDay(lastSecond, second)) {
                closeAndNewFile(nextFileNameOfDay(time));
                outMetricBuf.write(oneLineMsg.getBytes(CHARSET));
                outMetricBuf.flush();
                if (!validSize()) {
                    closeAndNewFile(nextFileNameOfDay(time));
                }
            } else {
                outMetricBuf.write(oneLineMsg.getBytes(CHARSET));
                outMetricBuf.flush();
                if (!validSize()) {
                    closeAndNewFile(nextFileNameOfDay(time));
                }
            }
            lastSecond = second;
        }
    }

    public synchronized void close() throws Exception {
        if (outMetricBuf != null) {
            outMetricBuf.close();
        }
        if (outIndex != null) {
            outIndex.close();
        }
    }

    private void writeIndex(long time, long offset) throws Exception {
        outIndex.writeLong(time);
        outIndex.writeLong(offset);
        outIndex.flush();
    }

    private String nextFileNameOfDay(long time) {
        List<String> list = new ArrayList<>();
        File baseFile = new File(baseDir);
        DateFormat fileNameDf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = fileNameDf.format(new Date(time));
        String fileNameModel = baseFileName + "." + dateStr;
        for (File file : Objects.requireNonNull(baseFile.listFiles())) {
            String fileName = file.getName();
            if (fileName.contains(fileNameModel)
                    && !fileName.endsWith(METRIC_FILE_INDEX_SUFFIX)
                    && !fileName.endsWith(".lck")) {
                list.add(file.getAbsolutePath());
            }
        }
        Collections.sort(list, METRIC_FILE_NAME_CMP);
        if (list.isEmpty()) {
            return baseDir + fileNameModel;
        }
        String last = list.get(list.size() - 1);
        int n = 0;
        String[] strs = last.split("\\.");
        if (strs.length > 0 && strs[strs.length - 1].matches("[0-9]{1,10}")) {
            n = Integer.parseInt(strs[strs.length - 1]);
        }
        return baseDir + fileNameModel + "." + (n + 1);
    }

    /**
     * A comparator for metric file name. Metric file name is like: <br/>
     * <pre>
     * metrics.log.2018-03-06
     * metrics.log.2018-03-07
     * metrics.log.2018-03-07.10
     * metrics.log.2018-03-06.100
     * </pre>
     * <p>
     * File name with the early date is smaller, if date is same, the one with the small file number is smaller.
     * Note that if the name is an absolute path, only the fileName({@link File#getName()}) part will be considered.
     * So the above file names should be sorted as: <br/>
     * <pre>
     * metrics.log.2018-03-06
     * metrics.log.2018-03-06.100
     * metrics.log.2018-03-07
     * metrics.log.2018-03-07.10
     *
     * </pre>
     * </p>
     */
    private static final class MetricFileNameComparator implements Comparator<String> {

        private final String pid = "pid";

        @Override
        public int compare(String o1, String o2) {
            String name1 = new File(o1).getName();
            String name2 = new File(o2).getName();
            String dateStr1 = name1.split("\\.")[2];
            String dateStr2 = name2.split("\\.")[2];
            // in case of file name contains pid, skip it, like Sentinel-Admin-metrics.log.pid22568.2018-12-24
            if (dateStr1.startsWith(pid)) {
                dateStr1 = name1.split("\\.")[3];
                dateStr2 = name2.split("\\.")[3];
            }

            // compare date first
            int t = dateStr1.compareTo(dateStr2);
            if (t != 0) {
                return t;
            }

            // same date, compare file number
            t = name1.length() - name2.length();
            if (t != 0) {
                return t;
            }
            return name1.compareTo(name2);
        }
    }

    /**
     * Get all metric files' name in {@code baseDir}. The file name must like
     * <pre>
     * baseFileName + ".yyyy-MM-dd.number"
     * </pre>
     * and not endsWith {@link #METRIC_FILE_INDEX_SUFFIX} or ".lck".
     *
     * @param baseDir      the directory to search.
     * @param baseFileName the file name pattern.
     * @return the metric files' absolute path({@link File#getAbsolutePath()})
     * @throws Exception
     */
    static List<String> listMetricFiles(String baseDir, String baseFileName) throws Exception {
        List<String> list = new ArrayList<String>();
        File baseFile = new File(baseDir);
        File[] files = baseFile.listFiles();
        if (files == null) {
            return list;
        }
        for (File file : files) {
            String fileName = file.getName();
            if (file.isFile()
                    && fileNameMatches(fileName, baseFileName)
                    && !fileName.endsWith(TrackerFileWriter.METRIC_FILE_INDEX_SUFFIX)
                    && !fileName.endsWith(".lck")) {
                list.add(file.getAbsolutePath());
            }
        }
        Collections.sort(list, TrackerFileWriter.METRIC_FILE_NAME_CMP);
        return list;
    }

    /**
     * Test whether fileName matches baseFileName. fileName matches baseFileName when
     * <pre>
     * fileName = baseFileName + ".yyyy-MM-dd.number"
     * </pre>
     *
     * @param fileName     file name
     * @param baseFileName base file name.
     * @return if fileName matches baseFileName return true, else return false.
     */
    public static boolean fileNameMatches(String fileName, String baseFileName) {
        if (fileName.startsWith(baseFileName)) {
            String part = fileName.substring(baseFileName.length());
            // part is like: ".yyyy-MM-dd.number", eg. ".2018-12-24.11"
            return part.matches("\\.[0-9]{4}-[0-9]{2}-[0-9]{2}(\\.[0-9]*)?");
        } else {
            return false;
        }
    }

    private void removeMoreFiles() throws Exception {
        List<String> list = listMetricFiles(baseDir, baseFileName);
        if (list == null || list.isEmpty()) {
            return;
        }
        for (int i = 0; i < list.size() - totalFileCount + 1; i++) {
            String fileName = list.get(i);
            String indexFile = formIndexFileName(fileName);
            new File(fileName).delete();
            log.info("[TRACKERS] Removing metric file: " + fileName);
            new File(indexFile).delete();
            log.info("[TRACKERS] Removing metric index file: " + indexFile);
        }
    }

    /**
     * @param fileName
     * @throws Exception
     */
    private void closeAndNewFile(String fileName) throws Exception {
        removeMoreFiles();
        if (outMetricBuf != null) {
            outMetricBuf.close();
        }
        if (outIndex != null) {
            outIndex.close();
        }
        // 新的文件将是一个全新的文件, 无法在最后追加, 若流关闭再次打开相同文件将会清空, 但实际不会打开相同的文件.
        outMetric = new FileOutputStream(fileName, append);
        outMetricBuf = new BufferedOutputStream(outMetric);
        curMetricFile = new File(fileName);
        String idxFile = formIndexFileName(fileName);
        curMetricIndexFile = new File(idxFile);
        outIndex = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(idxFile, append)));
        log.info("[TRACKERS] New metric file created: " + fileName);
        log.info("[TRACKERS] New metric index file created: " + idxFile);
    }

    private boolean validSize() throws Exception {
        long size = outMetric.getChannel().size();
        return size < singleFileSize;
    }

    private boolean isNewDay(long lastSecond, long second) {
        long lastDay = (lastSecond - timeSecondBase) / 86400;
        long newDay = (second - timeSecondBase) / 86400;
        return newDay > lastDay;
    }

    /**
     * Form metric file name use the specific appName and pid. Note that only
     * form the file name, not include path.
     * <p>
     * Note: {@link MetricFileNameComparator}'s implementation relays on the metric file name,
     * we should be careful when changing the metric file name.
     *
     * @param appName
     * @param pid
     * @return metric file name.
     */
    public String formMetricFileName(String appName, int pid) {
        if (appName == null) {
            appName = "";
        }
        // dot is special char that should be replaced.
        final String dot = ".";
        final String separator = "-";
        if (appName.contains(dot)) {
            appName = appName.replace(dot, separator);
        }
        String name = appName + separator + METRIC_FILE;
        if (usePid) {
            name += ".pid" + pid;
        }
        return name;
    }

    /**
     * Form index file name of the {@code metricFileName}
     *
     * @param metricFileName
     * @return the index file name of the metricFileName
     */
    public static String formIndexFileName(String metricFileName) {
        return metricFileName + METRIC_FILE_INDEX_SUFFIX;
    }
}
