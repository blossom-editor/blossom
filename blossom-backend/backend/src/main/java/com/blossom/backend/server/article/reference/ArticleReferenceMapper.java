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
}