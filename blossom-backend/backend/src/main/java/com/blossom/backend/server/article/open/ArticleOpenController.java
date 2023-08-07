package com.blossom.backend.server.article.open;


import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.server.article.draft.ArticleService;
import com.blossom.backend.server.article.draft.pojo.ArticleEntity;
import com.blossom.backend.server.article.draft.pojo.ArticleInfoRes;
import com.blossom.backend.server.article.open.pojo.ArticleOpenEntity;
import com.blossom.backend.server.article.open.pojo.ArticleOpenReq;
import com.blossom.backend.server.article.open.pojo.ArticleOpenRes;
import com.blossom.backend.server.article.open.pojo.ArticleOpenSyncReq;
import com.blossom.backend.base.auth.annotation.AuthIgnore;
import com.blossom.backend.server.article.view.ArticleViewService;
import com.blossom.backend.server.article.view.pojo.ArticleViewEntity;
import com.blossom.backend.server.doc.DocTypeEnum;
import com.blossom.backend.server.utils.DocUtil;
import com.blossom.common.base.exception.XzException404;
import com.blossom.common.base.pojo.R;
import com.blossom.common.base.util.ServletUtil;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * 公开文章 [Article]
 *
 * @author xzzz
 */
@RestController
@AllArgsConstructor
@RequestMapping("/article/open")
public class ArticleOpenController {

    private final ArticleService articleService;
    private final ArticleOpenService openService;


    /**
     * 查询公开文章, 只返回 html 内容
     *
     * @param id 文章ID
     * @return
     */
    @AuthIgnore
    @GetMapping("/info")
    public R<ArticleInfoRes> infoOpen(@RequestParam("id") Long id, HttpServletRequest request) {
        ArticleOpenEntity open = openService.selectById(id, true, false, true);
        XzException404.throwBy(ObjUtil.isNull(open), "文章不存在");

        ArticleInfoRes res = open.to(ArticleInfoRes.class);
        res.setOpenTime(open.getOpenTime());
        res.setOpenVersion(open.getOpenVersion());
        res.setSyncTime(open.getSyncTime());
        res.setType(DocTypeEnum.A.getType());

        ArticleEntity article = articleService.selectById(id, false, false, false);
        if (article != null) {
            res.setTags(DocUtil.toTagList(article.getTags()));
            res.setName(article.getName());
            res.setUv(article.getUv());
            res.setLikes(article.getLikes());
        }

        articleService.uvAndPv(ServletUtil.getIP(request), ServletUtil.getUserAgent(request), id);

        return R.ok(res);
    }

    /**
     * 公开文章
     *
     * @param req 文章对象
     */
    @PostMapping
    public R<Long> open(@Validated @RequestBody ArticleOpenReq req) {
        req.setUserId(AuthContext.getUserId());
        return R.ok(openService.open(req));
    }

    /**
     * 同步公开文章
     *
     * @param req 文章对象
     */
    @PostMapping("/sync")
    public R<ArticleOpenRes> sync(@Validated @RequestBody ArticleOpenSyncReq req) {
        openService.sync(req.getId());
        ArticleOpenEntity openEntity = openService.selectById(req.getId(), false, false, false);
        return R.ok(openEntity.to(ArticleOpenRes.class));
    }
}