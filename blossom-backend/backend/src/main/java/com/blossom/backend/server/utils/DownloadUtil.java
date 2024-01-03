package com.blossom.backend.server.utils;

import cn.hutool.core.net.URLEncodeUtil;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DownloadUtil {


    /**
     * 指定使用下载的方式处理响应
     *
     * @param response 响应
     * @param bis      流
     * @param filename 文件名称, 需要包含文件后缀
     */
    public static void forceDownload(HttpServletResponse response,
                                     BufferedInputStream bis,
                                     String filename) throws IOException {
        // 设置强制下载不打开
        response.setContentType("application/force-download");
        // 将请求头暴露给前端
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncodeUtil.encode(filename));
        OutputStream os = response.getOutputStream();
        byte[] buffer = new byte[1024];
        int i = bis.read(buffer);
        while (i != -1) {
            os.write(buffer, 0, i);
            i = bis.read(buffer);
        }
    }
}
