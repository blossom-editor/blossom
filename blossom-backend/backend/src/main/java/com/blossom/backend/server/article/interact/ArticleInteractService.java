package com.blossom.backend.server.article.interact;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blossom.backend.server.article.draft.pojo.ArticleEntity;
import com.blossom.backend.server.article.interact.pojo.LikeActionType;
import com.blossom.common.base.exception.XzException500;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 互动相关
 *
 * @author xingxing
 */
@Slf4j
@Service
@AllArgsConstructor
public class ArticleInteractService extends ServiceImpl<ArticleInteractMapper, ArticleEntity> {

    private final ArticleInteractMapper baseMapper;

    public Long likeAction(LikeActionType actionType, Long articleId) {
        int count = baseMapper.likeAction(actionType, articleId);
        XzException500.throwBy(count != 1, "sql执行结果错误");
        return articleId;
    }
}
