package com.blossom.backend.server.article.open;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blossom.backend.server.article.TagEnum;
import com.blossom.backend.server.article.draft.ArticleService;
import com.blossom.backend.server.article.draft.pojo.ArticleEntity;
import com.blossom.backend.server.article.draft.pojo.ArticleQueryReq;
import com.blossom.backend.server.article.open.pojo.ArticleBatchOpenReq;
import com.blossom.backend.server.article.open.pojo.ArticleOpenEntity;
import com.blossom.backend.server.article.open.pojo.ArticleOpenReq;
import com.blossom.common.base.enums.YesNo;
import com.blossom.common.base.exception.XzException400;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 公开文章
 *
 * @author xzzz
 */
@Slf4j
@Service
public class ArticleOpenService extends ServiceImpl<ArticleOpenMapper, ArticleOpenEntity> {

    private ArticleService articleService;

    @Autowired
    public void setArticleService(ArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * 根据ID查询
     */
    public ArticleOpenEntity selectById(Long id, boolean showToc, boolean showMarkdown, boolean showHtml) {
        QueryWrapper<ArticleOpenEntity> where = new QueryWrapper<>();
        List<String> column = CollUtil.newArrayList("id", "pid", "words", "open_version", "open_time", "sync_time");
        if (showToc) {
            column.add(TagEnum.toc.name());
        }
        if (showMarkdown) {
            column.add("markdown");
        }
        if (showHtml) {
            column.add("html");
        }
        where.select(column);
        where.eq("id", id).last("limit 1");
        return baseMapper.selectOne(where);
    }

    /**
     * 公开或关闭公开访问
     */
    @Transactional(rollbackFor = Exception.class)
    public Long openSingle(ArticleOpenReq req) {
        ArticleEntity article = articleService.getById(req.getId());
        open(req, article);
//        ArticleEntity upd = new ArticleEntity();
//        upd.setId(req.getId());
//        upd.setUserId(req.getUserId());
//        upd.setOpenStatus(req.getOpenStatus());
//        /*
//         * 公开文章 将 article 表插入到 article_open 表
//         */
//        if (YesNo.YES.getValue().equals(req.getOpenStatus())) {
//            XzException400.throwBy(article.getOpenStatus().equals(YesNo.YES.getValue()), "文章已[" + req.getId() + "]已允许公开访问, 若要同步最新文章内容, 请使用同步");
//            upd.setOpenVersion(article.getVersion());
//            baseMapper.open(req.getId());
//        }
//        /*
//         * 取消公开 删除 article_open 表数据
//         */
//        else if (YesNo.NO.getValue().equals(req.getOpenStatus())) {
//            upd.setOpenVersion(0);
//            XzException400.throwBy(article.getOpenStatus().equals(YesNo.NO.getValue()), "文章[" + req.getId() + "]未公开, 无法取消公开访问");
//            baseMapper.delById(req.getId());
//        }
//
//        // 修改文章的公开状态
//        articleService.update(upd);
        return req.getId();
    }

    /**
     * 批量公开
     *
     * @param req
     * @since 1.14.0
     */
    @Transactional(rollbackFor = Exception.class)
    public void openBatch(ArticleBatchOpenReq req) {
        ArticleQueryReq where = new ArticleQueryReq();
        where.setPids(CollUtil.newArrayList(req.getPid()));
        List<ArticleEntity> articles = articleService.listAll(where);
        for (ArticleEntity article : articles) {
            ArticleOpenReq open = new ArticleOpenReq();
            open.setId(article.getId());
            open.setOpenStatus(req.getOpenStatus());
            open.setUserId(req.getUserId());
            open(open, article);
        }
    }

    /**
     * 公开状态
     *
     * @param req     本次公开请求
     * @param article 文章
     */
    @Transactional(rollbackFor = Exception.class)
    public void open(ArticleOpenReq req, ArticleEntity article) {
        ArticleEntity upd = new ArticleEntity();
        upd.setId(req.getId());
        upd.setUserId(req.getUserId());
        upd.setOpenStatus(req.getOpenStatus());
        /*
         * 公开文章 将 article 表插入到 article_open 表
         */
        if (YesNo.YES.getValue().equals(req.getOpenStatus())) {
            if (YesNo.YES.getValue().equals(article.getOpenStatus())) {
                return;
            }
            // XzException400.throwBy(article.getOpenStatus().equals(YesNo.YES.getValue()), "文章已[" + req.getId() + "]已允许公开访问, 若要同步最新文章内容, 请使用同步");
            upd.setOpenVersion(article.getVersion());
            baseMapper.open(req.getId());
        }
        /*
         * 取消公开 删除 article_open 表数据
         */
        else if (YesNo.NO.getValue().equals(req.getOpenStatus())) {
            if (YesNo.NO.getValue().equals(article.getOpenStatus())) {
                return;
            }
            // XzException400.throwBy(article.getOpenStatus().equals(YesNo.NO.getValue()), "文章[" + req.getId() + "]未公开, 无法取消公开访问");
            upd.setOpenVersion(0);
            baseMapper.delById(req.getId());
        }

        // 修改文章的公开状态
        articleService.update(upd);
    }

    /**
     * 同步公开访问的文章正文等信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void sync(Long id) {
        ArticleOpenEntity article = baseMapper.selectById(id);
        XzException400.throwBy(ObjUtil.isNull(article), "文章[" + id + "]未公开, 无法同步");
        baseMapper.sync(id);
        articleService.sync(id);
    }
}
