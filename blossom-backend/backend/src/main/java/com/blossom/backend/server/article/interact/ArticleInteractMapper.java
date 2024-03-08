package com.blossom.backend.server.article.interact;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blossom.backend.server.article.draft.pojo.ArticleEntity;
import com.blossom.backend.server.article.interact.pojo.LikeActionType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 互动相关
 *
 * @author xingxing
 */
@Mapper
public interface ArticleInteractMapper extends BaseMapper<ArticleEntity> {

    /**
     * 点赞相关操作
     * @param actionType
     * @param articleId
     * Return void
     * Author xingxing
     * Date 2024/2/17
     */
    int likeAction(@Param("actionType") LikeActionType actionType, @Param("articleId") Long articleId);

}
