package com.blossom.backend.server.article.reference;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blossom.backend.server.article.reference.pojo.ArticleReferenceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章
 *
 * @author xzzz
 */
@Mapper
public interface ArticleReferenceMapper extends BaseMapper<ArticleReferenceEntity> {

    /**
     * 新增图片和文章引用关系
     *
     * @param articleId   文章ID
     * @param articleName 文章名称
     * @param pictureUrls 图片列表
     */
    void insertList(@Param("references") List<ArticleReferenceEntity> references);

    /**
     * 查看引用的所有图片
     *
     * @param userId    用户ID
     * @param articleId 文章ID
     */
    List<ArticleReferenceEntity> listPic(@Param("articleId") Long articleId);

    /**
     * 查询引用关系
     *
     * @param inner     是否内部
     * @param userId    用户ID
     * @param articleId 文章ID
     */
    List<ArticleReferenceEntity> listGraph(@Param("inner") Boolean inner, @Param("userId") Long userId, @Param("articleId") Long articleId);

    /**
     * 修改 sourceName
     */
    void updateSourceName(@Param("userId") Long userId, @Param("sourceId") Long sourceId, @Param("sourceName") String sourceName);

    /**
     * 修改 targetName
     */
    void updateTargetName(@Param("userId") Long userId, @Param("targetId") Long targetId, @Param("targetName") String targetName);

    /**
     * 被引用的文章修改为未知
     */
    void updateToUnknown(@Param("userId") Long userId, @Param("targetId") Long targetId, @Param("targetName") String targetName);

    /**
     * 被引用的文章修改为未知
     */
    void updateToKnown(@Param("userId") Long userId, @Param("targetId") Long targetId, @Param("targetName") String targetName);

    /**
     * 删除文章引用
     */
    void delByUserId(@Param("userId") Long userId);
}