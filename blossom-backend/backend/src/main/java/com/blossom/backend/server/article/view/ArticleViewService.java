package com.blossom.backend.server.article.view;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blossom.backend.server.article.view.pojo.ArticleViewEntity;
import com.blossom.common.base.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 文章访问记录
 *
 * @author xzzz
 */
@Slf4j
@Service
@AllArgsConstructor
public class ArticleViewService extends ServiceImpl<ArticleViewMapper, ArticleViewEntity> {

    /**
     * 检查并新增文章今日UV记录, 注意:非全系统UV
     *
     * @return true: 今日第一次访问; false:今日已访问
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean uv(String ip, String userAgent, Long articleId) {
        Date now = DateUtil.date();
        if (baseMapper.checkUv(DateUtils.toYMD(now), ip, articleId) > 0) {
            return false;
        }
        ArticleViewEntity uv = new ArticleViewEntity();
        uv.setType(ArticleViewTypeEnum.UV.getType());
        uv.setArticleId(articleId);
        uv.setIp(ip);
        uv.setUserAgent(userAgent);
        uv.setCreDay(now);
        uv.setCreTime(now);
        baseMapper.insert(uv);
        return true;
    }

    /**
     * 新增
     */
    @Transactional(rollbackFor = Exception.class)
    public void like(ArticleViewEntity req) {
        baseMapper.insert(req);
    }

    /**
     * 清空文章的访问记录
     *
     * @param articleId 文章ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long articleId) {
        if (articleId == null) {
            return;
        }
        LambdaQueryWrapper<ArticleViewEntity> where = new LambdaQueryWrapper<ArticleViewEntity>()
                .eq(ArticleViewEntity::getArticleId, articleId);
        baseMapper.delete(where);
    }
}