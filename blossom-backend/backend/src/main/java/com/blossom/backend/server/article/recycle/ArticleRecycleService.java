package com.blossom.backend.server.article.recycle;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.param.ParamEnum;
import com.blossom.backend.base.param.ParamService;
import com.blossom.backend.base.param.pojo.ParamEntity;
import com.blossom.backend.base.search.message.ArticleIndexMsg;
import com.blossom.backend.base.search.message.IndexMsgTypeEnum;
import com.blossom.backend.base.search.queue.IndexMsgQueue;
import com.blossom.backend.server.article.recycle.pojo.ArticleRecycleEntity;
import com.blossom.backend.server.folder.FolderService;
import com.blossom.backend.server.folder.pojo.FolderEntity;
import com.blossom.common.base.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 文章回收站
 *
 * @author xzzz
 * @since 1.10.0
 */
@Slf4j
@Service
@AllArgsConstructor
public class ArticleRecycleService extends ServiceImpl<ArticleRecycleMapper, ArticleRecycleEntity> {

    private final FolderService folderService;
    private final ParamService paramService;


    /**
     * 查询回收站所有内容
     *
     * @param userId 用户ID
     */
    public List<ArticleRecycleEntity> listAll(Long userId) {
        return baseMapper.listAll(userId);
    }

    /**
     * 还原数据
     *
     * @param id 文章ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void restore(Long id) {
        ArticleRecycleEntity article = baseMapper.selectById(id);
        FolderEntity folder = folderService.selectById(article.getPid());
        if (ObjUtil.isNull(folder)) {
            baseMapper.restore(id, 0L);
        } else {
            baseMapper.restore(id, folder.getId());
        }
        baseMapper.deleteById(id);
        ArticleIndexMsg articleIndexMsg = new ArticleIndexMsg(IndexMsgTypeEnum.ADD, article.getId(), AuthContext.getUserId());
        try {
            IndexMsgQueue.add(articleIndexMsg);
        } catch (InterruptedException e) {
            // 不抛出, 暂时先记录
            log.error("索引更新失败" + e.getMessage());
        }
    }

    /**
     * 每天凌晨4点执行
     * @Scheduled(cron = "0 0/1 * * * ?")
     */
    @Scheduled(cron = "0 0 04 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void delExpireRecycle() {
        ParamEntity param = paramService.getValue(ParamEnum.ARTICLE_RECYCLE_EXP_DAYS);
        int expireDay = -60;
        if (param != null) {
            expireDay = Integer.parseInt(param.getParamValue());
        }
        if (expireDay > 0) {
            expireDay = expireDay * -1;
        }

        log.info("[BLOSSOM] 删除{}日前的文章回收站记录", Math.abs(expireDay));
        Date expireDate = DateUtils.offsetDay(DateUtils.date(), expireDay);
        LambdaQueryWrapper<ArticleRecycleEntity> where = new LambdaQueryWrapper<>();
        where.lt(ArticleRecycleEntity::getDelTime, DateUtils.toYMD(expireDate));
        baseMapper.delete(where);
    }
}
