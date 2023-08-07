package com.blossom.backend.server.article.view;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blossom.backend.server.article.view.pojo.ArticleViewEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * 文章访问记录
 *
 * @author xzzz
 */
@Mapper
public interface ArticleViewMapper extends BaseMapper<ArticleViewEntity> {

    /**
     * 检查文章的UV
     *
     * @param today     日期
     * @param ip        ip
     * @param articleId 文章ID
     */
    Long checkUv(@Param("today") String today, @Param("ip") String ip, @Param("articleId") Long articleId);
}