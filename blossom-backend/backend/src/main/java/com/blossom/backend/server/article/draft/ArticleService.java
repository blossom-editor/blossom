package com.blossom.backend.server.article.draft;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blossom.backend.server.TagEnum;
import com.blossom.backend.server.article.draft.pojo.ArticleEntity;
import com.blossom.backend.server.article.draft.pojo.ArticleQueryReq;
import com.blossom.backend.server.article.log.ArticleLogService;
import com.blossom.backend.server.article.reference.ArticleReferenceService;
import com.blossom.backend.server.article.view.ArticleViewService;
import com.blossom.backend.server.doc.pojo.DocTreeRes;
import com.blossom.backend.server.utils.ArticleUtil;
import com.blossom.backend.server.utils.DocUtil;
import com.blossom.common.base.exception.XzException404;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 文章
 *
 * @author xzzz
 */
@Slf4j
@Service
@AllArgsConstructor
public class ArticleService extends ServiceImpl<ArticleMapper, ArticleEntity> {

    private final ArticleReferenceService referenceService;
    private final ArticleViewService viewService;
    private final ArticleLogService logService;

    public List<ArticleEntity> listAllContent(List<Long> ids) {
        List<ArticleEntity> articles =  baseMapper.listAllContent(ids);
        if (CollUtil.isEmpty(articles)) {
            return new ArrayList<>();
        }
        return articles;
    }

    /**
     * 查询列表
     */
    public List<ArticleEntity> listAll(ArticleQueryReq req) {
        List<ArticleEntity> articles = baseMapper.listAll(req.to(ArticleEntity.class));
        if (CollUtil.isEmpty(articles)) {
            return new ArrayList<>();
        }
        return articles;
    }

    /**
     * 树状列表
     *
     * @return DocTreeRes
     */
    public List<DocTreeRes> listTree(ArticleQueryReq req) {
        List<ArticleEntity> articles = baseMapper.listAll(req.to(ArticleEntity.class));
        List<DocTreeRes> articleTrees = new ArrayList<>(articles.size());
        for (ArticleEntity article : articles) {
            articleTrees.add(DocUtil.toDocTree(article));
        }
        return articleTrees;
    }

    /**
     * 根据ID查询
     */
    public ArticleEntity selectById(Long id, boolean showToc, boolean showMarkdown, boolean showHtml) {
        QueryWrapper<ArticleEntity> where = new QueryWrapper<>();
        List<String> column = CollUtil.newArrayList("id", "pid", "name", "icon", "tags", "sort", "cover", "describes", "star_status",
                "open_status", "pv", "uv", "likes", "words", "version", "cre_time", "upd_time");
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
     * 新增
     */
    @Transactional(rollbackFor = Exception.class)
    public Long insert(ArticleEntity req) {
        baseMapper.insert(req);
        return req.getId();
    }

    /**
     * 修改, 该接口只能修改文章的基本信息, 正文及版本修改请使用 {@link ArticleService#updateContentById(ArticleEntity)}
     */
    @Transactional(rollbackFor = Exception.class)
    public Long update(ArticleEntity req) {
        XzException404.throwBy(req.getId() == null, "ID不得为空");
        baseMapper.updById(req);
        return req.getId();
    }

    /**
     * 修改文章内容, 并统计字数
     *
     * @return 返回文章字数
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer updateContentById(ArticleEntity req) {
        XzException404.throwBy(req.getId() == null, "ID不得为空");
        if (req.getMarkdown() != null) {
            req.setWords(ArticleUtil.statWords(req.getMarkdown()));
        }
        if (req.getHtml() != null) {
            req.setHtml(req.getHtml().replaceAll("<p><br></p>", ""));
        }
        baseMapper.updContentById(req);
        referenceService.bind(req.getUserId(), req.getId(), req.getName(), req.getReferences());
        logService.insert(req.getId(), 0, req.getMarkdown());
        return req.getWords();
    }

    /**
     * 同步版本号, 将文章的 version 同步到 openVersion, 只有 open_status 为 1 才会修改成功
     */
    public void sync(Long id) {
        baseMapper.sync(id);
    }

    /**
     * 递增UV和PV数据
     * PV 接口每调用一次都会递增, UV数据每个IP每天只会递增一次
     *
     * @param ip        ip
     * @param userAgent userAgent
     * @param id        文章ID
     */
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void uvAndPv(String ip, String userAgent, Long id) {
        int uv = 0;
        if (viewService.uv(ip, userAgent, id)) {
            uv = 1;
        }
        baseMapper.uvAndPv(id, 1, uv);
    }
}