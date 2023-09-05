package com.blossom.backend.server.article.draft;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.user.UserService;
import com.blossom.backend.server.article.draft.pojo.*;
import com.blossom.backend.server.article.open.ArticleOpenService;
import com.blossom.backend.server.article.open.pojo.ArticleOpenEntity;
import com.blossom.backend.server.doc.DocTypeEnum;
import com.blossom.backend.server.folder.FolderService;
import com.blossom.backend.server.folder.pojo.FolderEntity;
import com.blossom.backend.server.utils.ArticleUtil;
import com.blossom.backend.server.utils.DocUtil;
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
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;


/**
 * 文章 [Article]
 *
 * @author xzzz
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
     * 通过ID查询文章
     *
     * @param id           文章ID
     * @param showToc      返回目录
     * @param showMarkdown 返回 markdown 内容
     * @param showHtml     返回 html 内容
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
        ArticleEntity article = baseService.selectById(id, showToc, showMarkdown, showHtml);
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
    public R<Long> insert(@Validated @RequestBody ArticleAddReq req) {
        ArticleEntity article = req.to(ArticleEntity.class);
        article.setTags(DocUtil.toTagStr(req.getTags()));
        article.setUserId(AuthContext.getUserId());
        return R.ok(baseService.insert(article));
    }

    /**
     * 修改文章
     *
     * @param req 文章对象
     * @apiNote 该接口只能修改文章的基本信息, 正文及版本修改请使用 {@link ArticleService#updateContentById(ArticleEntity)}
     */
    @PostMapping("/upd")
    public R<Long> insert(@Validated @RequestBody ArticleUpdReq req) {
        ArticleEntity article = req.to(ArticleEntity.class);
        article.setTags(DocUtil.toTagStr(req.getTags()));
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
     * 删除文章
     */
    @PostMapping("/del")
    public R<?> delete(@Validated @RequestBody DelReq req) {
        baseService.delete(req.getId());
        return R.ok();
    }

    /**
     * 星标文章
     *
     * @param req 文章对象
     */
    @PostMapping("/star")
    public R<Long> star(@Validated @RequestBody ArticleStarReq req) {
        return R.ok(baseService.update(req.to(ArticleEntity.class)));
    }

    /**
     * 下载文章
     *
     * @param id       文章ID
     * @param response 文章流
     */
    @GetMapping("/download")
    public void download(@RequestParam("id") Long id, HttpServletResponse response) throws IOException {
        ArticleEntity article = baseService.selectById(id, false, true, false);
        if (StrUtil.isBlank(article.getMarkdown())) {
            throw new IllegalArgumentException("文章内容为空,无法导出");
        }
        try (InputStream is = new ByteArrayInputStream(article.getMarkdown().getBytes(StandardCharsets.UTF_8)); BufferedInputStream bis = new BufferedInputStream(is)) {
            OutputStream os = response.getOutputStream();
            String filename = URLEncodeUtil.encode(article.getName() + ".md");
            // 设置强制下载不打开
            response.setContentType("application/force-download");
            // 将请求头暴露给前端
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.addHeader("Content-Disposition", "attachment;filename=" + filename);
            byte[] buffer = new byte[1024];
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        }
    }


    /**
     * 下载文章
     *
     * @param id       文章ID
     * @param response 文章流
     */
    @GetMapping("/download/html")
    public void downloadHtml(@RequestParam("id") Long id, HttpServletResponse response) throws IOException {
        ArticleEntity article = baseService.selectById(id, false, false, true);
        if (StrUtil.isBlank(article.getHtml())) {
            throw new IllegalArgumentException("文章内容为空,无法导出");
        }
        String reportHtml = ArticleUtil.exportHtml(article, userService.selectById(AuthContext.getUserId()));
        try (InputStream is = new ByteArrayInputStream(reportHtml.getBytes(StandardCharsets.UTF_8));
             BufferedInputStream bis = new BufferedInputStream(is)) {
            OutputStream os = response.getOutputStream();
            String filename = URLEncodeUtil.encode(article.getName() + ".html");
            // 设置强制下载不打开
            response.setContentType("application/force-download");
            // 将请求头暴露给前端
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.addHeader("Content-Disposition", "attachment;filename=" + filename);
            byte[] buffer = new byte[1024];
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        }
    }

    /**
     * 文章导入
     *
     * @param file 文件
     * @param pid  上级菜单
     */
    @PostMapping("import")
    public R<?> upload(@RequestParam("file") MultipartFile file, @RequestParam(value = "pid") Long pid) {
        try {
            String suffix = FileUtil.getSuffix(file.getOriginalFilename());
            if (!"txt".equals(suffix) && !"md".equals(suffix)) {
                throw new XzException404("不支持的文件类型: [" + suffix + "]");
            }
            FolderEntity folder = folderService.selectById(pid);
            XzException404.throwBy(ObjUtil.isNull(folder), "上级文件夹不存在");
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            ArticleEntity article = new ArticleEntity();
            article.setMarkdown(content);
            article.setPid(pid);
            article.setName(FileUtil.getPrefix(file.getOriginalFilename()));
            baseService.insert(article);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.ok();
    }
}
