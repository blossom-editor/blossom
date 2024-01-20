package com.blossom.backend.server.article.recycle;


import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.util.StrUtil;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.server.article.recycle.pojo.ArticleRecycleEntity;
import com.blossom.backend.server.article.recycle.pojo.ArticleRecycleListRes;
import com.blossom.backend.server.article.recycle.pojo.ArticleRecycleRestoreReq;
import com.blossom.backend.server.utils.DownloadUtil;
import com.blossom.common.base.pojo.R;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 文章回收站 [A#Recycle]
 *
 * @author xzzz
 * @order 7
 * @since 1.10.0
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/article/recycle")
public class ArticleRecycleController {

    private final ArticleRecycleService baseService;

    /**
     * 查询列表
     *
     * @apiNote 返回简单字段
     */
    @GetMapping("/list")
    public R<List<ArticleRecycleListRes>> listAll() {
        return R.ok(baseService.listAll(AuthContext.getUserId()), ArticleRecycleListRes.class);
    }

    /**
     * 还原文章
     *
     * @apiNote 还原不包含收藏、公开状态，文章版本将清零。文章的 Html 解析结果，目录也将清空。
     */
    @PostMapping("/restore")
    public R<?> restore(@Validated @RequestBody ArticleRecycleRestoreReq req) {
        baseService.restore(AuthContext.getUserId(), req.getId());
        return R.ok();
    }

    /**
     * 下载文章
     *
     * @param id       文章ID
     * @param response 文章流
     * @apiNote 返回流
     */
    @GetMapping("/download")
    public void download(@RequestParam("id") Long id, HttpServletResponse response) throws IOException {
        ArticleRecycleEntity article = baseService.selectById(id);
        if (StrUtil.isBlank(article.getMarkdown())) {
            article.setMarkdown("文章无内容");
        }
        try (InputStream is = new ByteArrayInputStream(article.getMarkdown().getBytes(StandardCharsets.UTF_8));
             BufferedInputStream bis = new BufferedInputStream(is)) {
            String filename = URLEncodeUtil.encode(article.getName() + ".md");
            DownloadUtil.forceDownload(response, bis, filename);
        }
    }
}
