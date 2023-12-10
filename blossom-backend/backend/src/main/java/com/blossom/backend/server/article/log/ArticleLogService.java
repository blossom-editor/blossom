package com.blossom.backend.server.article.log;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blossom.backend.base.param.ParamEnum;
import com.blossom.backend.base.param.ParamService;
import com.blossom.backend.base.param.pojo.ParamEntity;
import com.blossom.backend.server.article.log.pojo.ArticleLogEntity;
import com.blossom.common.base.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 文章记录
 */
@Slf4j
@Service
@AllArgsConstructor
public class ArticleLogService extends ServiceImpl<ArticleLogMapper, ArticleLogEntity> {

    private final ParamService paramService;

    public List<ArticleLogEntity> listAll(Long articleId) {
        return baseMapper.listAll(articleId);
    }

    public String content(Long id) {
        return baseMapper.selectById(id).getMarkdown();
    }

    /**
     * 新增记录
     */
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void insert(Long articleId, Integer version, String markdown) {
        ArticleLogEntity log = new ArticleLogEntity();
        log.setArticleId(articleId);
        log.setVersion(version);
        log.setMarkdown(StrUtil.isBlank(markdown) ? "无内容" : markdown);
        log.setCreTime(DateUtils.date());
        baseMapper.insert(log);
    }

    /**
     * 每天凌晨5点执行
     */
    @Scheduled(cron = "0 0 05 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void delExpireLog() {
        ParamEntity param = paramService.getValue(ParamEnum.ARTICLE_LOG_EXP_DAYS);
        int expireDay = -60;
        if (param != null) {
            expireDay = Integer.parseInt(param.getParamValue());
        }
        if (expireDay > 0) {
            expireDay = expireDay * -1;
        }

        log.info("[BLOSSOM] 删除{}日前的编辑记录", Math.abs(expireDay));
        Date expireDate = DateUtils.offsetDay(DateUtils.date(), expireDay);
        LambdaQueryWrapper<ArticleLogEntity> where = new LambdaQueryWrapper<>();
        where.lt(ArticleLogEntity::getCreTime, DateUtils.toYMD(expireDate));
        baseMapper.delete(where);
    }

}