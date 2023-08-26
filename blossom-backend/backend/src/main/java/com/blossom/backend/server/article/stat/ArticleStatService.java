package com.blossom.backend.server.article.stat;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blossom.backend.server.article.draft.ArticleMapper;
import com.blossom.backend.server.article.draft.pojo.ArticleStatRes;
import com.blossom.backend.server.article.stat.pojo.ArticleHeatmapRes;
import com.blossom.backend.server.article.stat.pojo.ArticleLineRes;
import com.blossom.backend.server.article.stat.pojo.ArticleStatEntity;
import com.blossom.common.base.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 文章统计
 *
 * @author xzzz
 */
@Slf4j
@Service
@AllArgsConstructor
public class ArticleStatService extends ServiceImpl<ArticleStatMapper, ArticleStatEntity> {

    private final ArticleMapper articleMapper;

    /**
     * 文章编辑热力图, type = 1
     *
     * @param offsetMonth 统计最近的月数, 传入负数
     */
    public ArticleHeatmapRes statArticleCountByDay(Integer offsetMonth, Long userId) {
        // 默认1个季度
        if (offsetMonth == null) {
            offsetMonth = -2;
        }

        Date today = DateUtils.date();
        Date thisMonthEnd = DateUtils.endOfMonth(today);
        Date before2Month = DateUtils.offsetMonth(thisMonthEnd, offsetMonth);

        String begin = DateUtils.format(before2Month, DateUtils.PATTERN_YYYYMMDD).substring(0, 7) + "-01";
        String end = DateUtils.format(thisMonthEnd, DateUtils.PATTERN_YYYYMMDD);

        LambdaQueryWrapper<ArticleStatEntity> where = new LambdaQueryWrapper<>();
        where.eq(ArticleStatEntity::getUserId, userId)
                .eq(ArticleStatEntity::getType, ArticleStatTypeEnum.ARTICLE_HEATMAP.getType())
                .ge(ArticleStatEntity::getStatDate, begin)
                .le(ArticleStatEntity::getStatDate, end);

        // 最近 @{offsetMonth} 个月, 每个月按最大31天计算
        List<ArticleStatEntity> stats = baseMapper.selectList(where);

        ArticleHeatmapRes res = new ArticleHeatmapRes();
        res.setData(new ArrayList<>());
        res.setMaxStatValues(0);
        res.setDateBegin(begin);
        res.setDateEnd(end);

        if (CollUtil.isNotEmpty(stats)) {
            res.setMaxStatValues(stats.stream().mapToInt(ArticleStatEntity::getStatValue).max().orElse(0));
            for (ArticleStatEntity stat : stats) {
                Object[] data = new Object[]{
                        DateUtils.format(stat.getStatDate(), DateUtils.PATTERN_YYYYMMDD),
                        stat.getStatValue()
                };
                res.getData().add(data);
            }
        }

        return res;

    }

    /**
     * 文章字数统计
     */
    public ArticleLineRes statArticleWordsByMonth(Long userId) {
        Date today = DateUtils.date();
        Date thisMonthEnd = DateUtils.endOfMonth(today);
        Date before36Month = DateUtils.offsetMonth(thisMonthEnd, -36);

        String begin = DateUtils.format(before36Month, DateUtils.PATTERN_YYYYMMDD).substring(0, 7) + "-01";
        String end = DateUtils.format(thisMonthEnd, DateUtils.PATTERN_YYYYMMDD);

        LambdaQueryWrapper<ArticleStatEntity> where = new LambdaQueryWrapper<>();
        where.eq(ArticleStatEntity::getUserId, userId).eq(ArticleStatEntity::getType, ArticleStatTypeEnum.ARTICLE_WORDS.getType())
                .ge(ArticleStatEntity::getStatDate, begin)
                .le(ArticleStatEntity::getStatDate, end);

        List<ArticleStatEntity> stats = baseMapper.selectList(where);

        ArticleLineRes res = new ArticleLineRes();
        res.setStatDates(new ArrayList<>());
        res.setStatValues(new ArrayList<>());
        if (CollUtil.isNotEmpty(stats)) {
            for (ArticleStatEntity stat : stats) {
                res.getStatDates().add(DateUtils.format(stat.getStatDate(), DateUtils.PATTERN_YYYYMMDD).substring(0, 7));
                res.getStatValues().add(stat.getStatValue());
            }
        }
        return res;
    }

    /**
     * 文章统计, 文章数, 总字数
     *
     * @param beginUpdTime 修改日期的开始范围
     * @param endUpdTime   修改日期的结束范围
     */
    public ArticleStatRes statCount(String beginUpdTime, String endUpdTime, Long userId) {
        return articleMapper.statCount(beginUpdTime, endUpdTime, userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updByDate(Integer type, String date, Integer value, Long userId) {
        if (value == null) {
            value = 0;
        }
        LambdaQueryWrapper<ArticleStatEntity> existWhere = new LambdaQueryWrapper<>();
        existWhere
                .eq(ArticleStatEntity::getUserId, userId)
                .eq(ArticleStatEntity::getType, type)
                .eq(ArticleStatEntity::getStatDate, date);
        if (baseMapper.exists(existWhere)) {
            LambdaQueryWrapper<ArticleStatEntity> updWhere = new LambdaQueryWrapper<>();
            updWhere.eq(ArticleStatEntity::getUserId, userId)
                    .eq(ArticleStatEntity::getType, type)
                    .eq(ArticleStatEntity::getStatDate, date);
            ArticleStatEntity upd = new ArticleStatEntity();
            upd.setStatValue(value);
            baseMapper.update(upd, updWhere);
        } else {
            ArticleStatEntity ist = new ArticleStatEntity();
            ist.setType(type);
            ist.setStatDate(DateUtils.parse(date, DateUtils.PATTERN_YYYYMMDD));
            ist.setStatValue(value);
            ist.setUserId(userId);
            baseMapper.insert(ist);
        }

    }

    public static void main(String[] args) {
        Date today = DateUtils.date();
        Date thisMonthEnd = DateUtils.endOfMonth(today);
        System.out.println(DateUtils.format(thisMonthEnd, DateUtils.PATTERN_YYYYMMDD));
    }
}
