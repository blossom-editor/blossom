package com.blossom.backend.server.article.open;


import cn.hutool.core.util.ObjUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.auth.annotation.AuthIgnore;
import com.blossom.backend.base.param.ParamEnum;
import com.blossom.backend.base.param.ParamService;
import com.blossom.backend.base.param.pojo.ParamEntity;
import com.blossom.backend.server.article.draft.ArticleService;
import com.blossom.backend.server.article.draft.pojo.ArticleEntity;
import com.blossom.backend.server.article.draft.pojo.ArticleInfoRes;
import com.blossom.backend.server.article.open.pojo.ArticleOpenEntity;
import com.blossom.backend.server.article.open.pojo.ArticleOpenReq;
import com.blossom.backend.server.article.open.pojo.ArticleOpenRes;
import com.blossom.backend.server.article.open.pojo.ArticleOpenSyncReq;
import com.blossom.backend.server.doc.DocTypeEnum;
import com.blossom.backend.server.utils.DocUtil;
import com.blossom.common.base.exception.XzException404;
import com.blossom.common.base.pojo.R;
import com.blossom.common.base.util.ServletUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 公开文章 [Article]
 *
 * @author xzzz
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/article/open")
public class ArticleOpenController {

    private final ArticleService articleService;
    private final ArticleOpenService openService;
    private final ParamService paramService;


    /**
     * 查询公开文章, 只返回 html 内容
     *
     * @param id 文章ID
     * @return 文章信息
     */
    @AuthIgnore
    @GetMapping("/info")
    public R<ArticleInfoRes> infoOpen(@RequestParam("id") Long id, HttpServletRequest request) {
        log.info("公开文章被查看:{}", id);
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

    /**
     * 生成公开文章二维码
     *
     * @param id 文章ID
     */
    @GetMapping("/qrcode")
    public void qrcode(@RequestParam("id") Long id, HttpServletResponse response) {
        ParamEntity param = paramService.getValue(ParamEnum.WEB_ARTICLE_URL);
        XzException404.throwBy(ObjUtil.isNull(param), "未配置文章公网访问链接，无法生成二维码，请在服务端配置参数 [BaseParam.WEB_ARTICLE_URL]");
        final String url = param.getParamValue() + id;
        BufferedImage bfi = QrCodeUtil.generate(url, new QrConfig(200, 200));
        response.setContentType("image/png");
        response.setHeader("Access-Control-Expose-Headers", "Article-Url");
        response.addHeader("Article-Url", url);
        try {
            ImageIO.write(bfi, "png", response.getOutputStream());
            response.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}