package com.blossom.backend.server.article.recycle;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blossom.backend.server.article.recycle.pojo.ArticleRecycleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章回收站
 *
 * @author xzzz
 * @since 1.10.0
 */
@Mapper
public interface ArticleRecycleMapper extends BaseMapper<ArticleRecycleEntity> {

    /**
     * 查询全部
     *
     * @param userId 用户ID
     */
    List<ArticleRecycleEntity> listAll(@Param("userId") Long userId);

    /**
     * 保存进回收站
     *
     * @param id 文章ID
     */
    void save(@Param("id") Long id);

    /**
     * 还原
     *
     * @param id  文章ID
     * @param pid 文章的父ID
     */
    void restore(@Param("id") Long id, @Param("pid") Long pid);

    /**
     * 删除文章回收站
     */
    void delByUserId(@Param("userId") Long userId);
}
