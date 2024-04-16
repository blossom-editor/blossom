package com.blossom.backend.server.article.draft;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.auth.annotation.AuthIgnore;
import com.blossom.backend.base.paramu.UserParamEnum;
import com.blossom.backend.base.paramu.UserParamService;
import com.blossom.backend.base.user.UserService;
import com.blossom.backend.server.article.draft.pojo.*;
import com.blossom.backend.server.article.open.ArticleOpenService;
import com.blossom.backend.server.article.open.pojo.ArticleOpenEntity;
import com.blossom.backend.server.doc.DocService;
import com.blossom.backend.server.doc.DocSortChecker;
import com.blossom.backend.server.doc.DocTypeEnum;
import com.blossom.backend.server.folder.FolderService;
import com.blossom.backend.server.folder.FolderTypeEnum;
import com.blossom.backend.server.folder.pojo.FolderEntity;
import com.blossom.backend.server.utils.ArticleUtil;
import com.blossom.backend.server.utils.DocUtil;
import com.blossom.backend.server.utils.DownloadUtil;
import com.blossom.common.base.enums.YesNo;
import com.blossom.common.base.exception.XzException400;
import com.blossom.common.base.exception.XzException404;
import com.blossom.common.base.pojo.DelReq;
import com.blossom.common.base.pojo.R;
import com.blossom.common.base.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;


/**
 * 文章 [Article]
 *
 * @author xzzz
 * @order 3
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/article")
public class ArticleController {
    private final ArticleService baseService;
    private final ArticleOpenService openService;
    private final FolderService folderService;
    private final UserService userService;
    private final ArticleTempVisitService tempVisitService;
    private final DocService docService;
    private final DocSortChecker docSortChecker;
    private final ImportManager importManager;
    private final UserParamService userParamService;

    /**
     * 查询列表
     *
     * @apiNote 返回简单字段
     */
    @GetMapping("/list")
    public R<List<ArticleInfoSimpleRes>> listAll(ArticleQueryReq req) {
        req.setUserId(AuthContext.getUserId());
        return R.ok(baseService.listAll(req), ArticleInfoSimpleRes.class);
    }

    /**
     * 文章详情 [ID]
     *
     * @param id           文章ID
     * @param showToc      是否返回目录
     * @param showMarkdown 是否返回 markdown 内容
     * @param showHtml     是否返回 html 内容
     * @return 文章信息
     */
    @GetMapping("/info")
    public R<ArticleInfoRes> info(@RequestParam("id") Long id,
                                  @RequestParam("showToc") Boolean showToc,
                                  @RequestParam("showMarkdown") Boolean showMarkdown,
                                  @RequestParam("showHtml") Boolean showHtml) {
        if (showToc == null) {
            showToc = false;
        }
        if (showMarkdown == null) {
            showMarkdown = false;
        }
        if (showHtml == null) {
            showHtml = false;
        }
        ArticleEntity article = baseService.selectById(id, showToc, showMarkdown, showHtml, AuthContext.getUserId());
        XzException400.throwBy(ObjUtil.isNull(article), "文章不存在");
        ArticleInfoRes res = article.to(ArticleInfoRes.class);
        res.setTags(DocUtil.toTagList(article.getTags()));
        res.setType(DocTypeEnum.A.getType());

        if (YesNo.YES.getValue().equals(article.getOpenStatus())) {
            ArticleOpenEntity open = openService.selectById(id, false, false, false);
            if (open != null) {
                res.setOpenTime(open.getOpenTime());
                res.setOpenVersion(open.getOpenVersion());
                res.setSyncTime(open.getSyncTime());
            }
        }

        return R.ok(res);
    }


    /**
     * 新增文章
     *
     * @param req 文章对象
     * @return 保存结果
     */
    @PostMapping("/add")
    public R<ArticleEntity> insert(@Validated @RequestBody ArticleAddReq req) {
        ArticleEntity article = req.to(ArticleEntity.class);
        article.setTags(DocUtil.toTagStr(req.getTags()));
        article.setUserId(AuthContext.getUserId());
        // 如果新增到顶部, 获取最小的排序
        if (BooleanUtil.isTrue(req.getAddToLast())) {
            article.setSort(docService.selectMaxSortByPid(req.getPid(), AuthContext.getUserId(), FolderTypeEnum.ARTICLE) + 1);
        }
        return R.ok(baseService.insert(article));
    }

    /**
     * 修改文章基础信息
     *
     * @param req 文章对象
     * @apiNote 该接口只能修改文章的基本信息, 正文及版本修改请使用 "/upd/content" 接口，或者 {@link ArticleService#updateContentById(ArticleEntity)}
     */
    @PostMapping("/upd")
    public R<Long> update(@Validated @RequestBody ArticleUpdReq req) {
        ArticleEntity article = req.to(ArticleEntity.class);
        article.setTags(DocUtil.toTagStr(req.getTags()));
        article.setUserId(AuthContext.getUserId());
        // 检查排序是否重复
//        if (req.getSort() != null && req.getPid() != null) {
//            final long newPid = req.getPid();
//            docSortChecker.checkUnique(CollUtil.newArrayList(newPid),
//                    null,
//                    CollUtil.newArrayList(article),
//                    FolderTypeEnum.ARTICLE,
//                    AuthContext.getUserId());
//        }
        return R.ok(baseService.update(article));
    }

    /**
     * 保存正文内容
     */
    @PostMapping("/upd/content")
    public R<ArticleUpdContentRes> updateContent(@Validated @RequestBody ArticleUpdContentReq content) {
        ArticleEntity upd = content.to(ArticleEntity.class);
        upd.setReferences(content.getReferences());
        upd.setUserId(AuthContext.getUserId());
        int words = baseService.updateContentById(upd);
        ArticleUpdContentRes res = new ArticleUpdContentRes();
        res.setWords(words);
        res.setUpdTime(DateUtils.date());
        return R.ok(res);
    }

    /**
     * 修改文章名称
     *
     * @param name 文章名称
     * @since 1.10.0
     */
    @PostMapping("/upd/name")
    public R<?> updName(@Validated @RequestBody ArticleUpdNameReq name) {
        ArticleEntity article = name.to(ArticleEntity.class);
        article.setUserId(AuthContext.getUserId());
        baseService.update(article);
        return R.ok();
    }

    /**
     * 快速增加/删除标签
     *
     * @since 1.10.0
     */
    @PostMapping("/upd/tag")
    public R<List<String>> updTag(@Validated @RequestBody ArticleUpdTagReq req) {
        ArticleEntity info = baseService.selectById(req.getId(), false, false, false, AuthContext.getUserId());
        List<String> tags = DocUtil.toTagList(info.getTags());
        if (tags.contains(req.getTag().toLowerCase()) || tags.contains(req.getTag().toUpperCase())) {
            tags.remove(req.getTag().toLowerCase());
            tags.remove(req.getTag().toUpperCase());
        } else {
            tags.add(req.getTag());
        }
        ArticleEntity article = req.to(ArticleEntity.class);
        article.setTags(DocUtil.toTagStr(tags));
        article.setUserId(AuthContext.getUserId());
        baseService.update(article);
        return R.ok(tags);
    }

    /**
     * 删除文章
     */
    @PostMapping("/del")
    public R<?> delete(@Validated @RequestBody DelReq req) {
        baseService.delete(req.getId(), AuthContext.getUserId());
        return R.ok();
    }

    /**
     * 星标文章
     *
     * @param req 文章对象
     */
    @PostMapping("/star")
    public R<Long> star(@Validated @RequestBody ArticleStarReq req) {
        ArticleEntity article = req.to(ArticleEntity.class);
        article.setUserId(AuthContext.getUserId());
        return R.ok(baseService.update(article));
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
        ArticleEntity article = baseService.selectById(id, false, true, false, AuthContext.getUserId());
        if (StrUtil.isBlank(article.getMarkdown())) {
            article.setMarkdown("文章无内容");
        }
        try (InputStream is = new ByteArrayInputStream(article.getMarkdown().getBytes(StandardCharsets.UTF_8));
             BufferedInputStream bis = new BufferedInputStream(is)) {
            String filename = URLEncodeUtil.encode(article.getName() + ".md");
            DownloadUtil.forceDownload(response, bis, filename);
        }
    }

    /**
     * 下载文章 Html
     *
     * @param id       文章ID
     * @param response 文章流
     * @apiNote 返回流
     */
    @GetMapping("/download/html")
    public void downloadHtml(@RequestParam("id") Long id, HttpServletResponse response) throws IOException {
        ArticleEntity article = baseService.selectById(id, false, false, true, AuthContext.getUserId());
        if (StrUtil.isBlank(article.getHtml())) {
            article.setHtml("<span>文章无内容</span>");
        }
        String reportHtml = ArticleUtil.toHtml(article,
                userService.selectById(AuthContext.getUserId()),
                userParamService.getValue(AuthContext.getUserId(), UserParamEnum.WEB_BLOG_COLOR).getParamValue(),
                userParamService.getValue(AuthContext.getUserId(), UserParamEnum.WEB_BLOG_WATERMARK_ENABLED).getParamValue(),
                userParamService.getValue(AuthContext.getUserId(), UserParamEnum.WEB_BLOG_WATERMARK_CONTENT).getParamValue(),
                userParamService.getValue(AuthContext.getUserId(), UserParamEnum.WEB_BLOG_WATERMARK_FONTSIZE).getParamValue(),
                userParamService.getValue(AuthContext.getUserId(), UserParamEnum.WEB_BLOG_WATERMARK_COLOR).getParamValue(),
                userParamService.getValue(AuthContext.getUserId(), UserParamEnum.WEB_BLOG_WATERMARK_GAP).getParamValue()
        );
        try (InputStream is = new ByteArrayInputStream(reportHtml.getBytes(StandardCharsets.UTF_8));
             BufferedInputStream bis = new BufferedInputStream(is)) {
            String filename = URLEncodeUtil.encode(article.getName() + ".html");
            DownloadUtil.forceDownload(response, bis, filename);
        }
    }

    /**
     * 文章导入
     *
     * @param file 文件
     * @param pid  上级菜单
     */
    @PostMapping("import")
    public R<?> upload(@RequestParam("file") MultipartFile file, @RequestParam(value = "pid") Long pid, @RequestParam(value = "batchId") String batchId) {
        try {
            String suffix = FileUtil.getSuffix(file.getOriginalFilename());
            if (!"txt".equals(suffix) && !"md".equals(suffix)) {
                throw new XzException400("不支持的文件类型: [" + suffix + "]");
            }
            FolderEntity folder = folderService.selectById(pid);
            XzException404.throwBy(ObjUtil.isNull(folder), "上级文件夹不存在");
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            ArticleEntity article = new ArticleEntity();
            article.setMarkdown(content);
            article.setVersion(1);
            article.setPid(pid);
            article.setUserId(AuthContext.getUserId());
            article.setName(FileUtil.getPrefix(file.getOriginalFilename()));
//            article.setWords(ArticleUtil.statWords(content));
            article.setSort(importManager.getSort(batchId, pid, AuthContext.getUserId()));
            baseService.insert(article);
        } catch (Exception e) {
            e.printStackTrace();
            throw new XzException400("上传失败");
        }
        return R.ok();
    }

    /**
     * 创建文章的临时访问缓存
     *
     * @param id       文章ID
     * @param duration 临时访问的过期时间
     * @return 临时访问Key
     * @since 1.9.0
     */
    @GetMapping("/temp/key")
    public R<String> createTempVisitKey(@RequestParam("id") Long id,
                                        @RequestParam(value = "duration", required = false) Long duration) {
        if (duration == null) {
            duration = 3 * 60L;
        }
        log.info("创建文章临时访问权限 [{}:{}m]", id, duration);
        return R.ok(tempVisitService.create(id, AuthContext.getUserId(), duration));
    }

    /**
     * 临时查看文章
     *
     * @param s 临时访问 Key
     * @since 1.9.0
     */
    @AuthIgnore
    @GetMapping("/temp/h")
    public String content(@RequestParam("k") String s, HttpServletResponse resp) {
        ArticleTempVisitService.TempVisit visit = tempVisitService.get(s);
        XzException404.throwBy(ObjUtil.isNull(visit), "文章不存在或您无权限查看");
        ArticleEntity article = baseService.selectById(visit.getArticleId(), false, false, true, visit.getUserId());
        resp.setContentType("text/html");
        return ArticleUtil.toHtml(
                article,
                userService.selectById(visit.getUserId()),
                userParamService.getValue(visit.getUserId(), UserParamEnum.WEB_BLOG_COLOR).getParamValue(),
                userParamService.getValue(visit.getUserId(), UserParamEnum.WEB_BLOG_WATERMARK_ENABLED).getParamValue(),
                userParamService.getValue(visit.getUserId(), UserParamEnum.WEB_BLOG_WATERMARK_CONTENT).getParamValue(),
                userParamService.getValue(visit.getUserId(), UserParamEnum.WEB_BLOG_WATERMARK_FONTSIZE).getParamValue(),
                userParamService.getValue(visit.getUserId(), UserParamEnum.WEB_BLOG_WATERMARK_COLOR).getParamValue(),
                userParamService.getValue(visit.getUserId(), UserParamEnum.WEB_BLOG_WATERMARK_GAP).getParamValue()
        );
    }
}
