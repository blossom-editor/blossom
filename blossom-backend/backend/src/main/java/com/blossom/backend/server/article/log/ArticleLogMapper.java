package com.blossom.backend.server.article.log;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blossom.backend.server.article.log.pojo.ArticleLogEntity;
import org.apache.ibatis.annotations.Mapper;


/**
 * 文章记录
 */
@Mapper
public interface ArticleLogMapper extends BaseMapper<ArticleLogEntity> {

}