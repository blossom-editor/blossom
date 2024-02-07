package com.blossom.backend.server.web;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blossom.backend.server.web.pojo.WebEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 网站收藏
 *
 * @author xzzz
 */
@Mapper
public interface WebMapper extends BaseMapper<WebEntity> {

    /**
     * 删除文章
     */
    void delByUserId(@Param("userId") Long userId);
}