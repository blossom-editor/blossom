package com.blossom.backend.server.article.stat;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blossom.backend.server.article.stat.pojo.ArticleStatEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章统计
 *
 * @author xzzz
 */
@Mapper
public interface ArticleStatMapper extends BaseMapper<ArticleStatEntity> {
}
