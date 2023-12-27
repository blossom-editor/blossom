package com.blossom.backend.base.paramu;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blossom.backend.base.paramu.pojo.UserParamEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户参数
 *
 * @since 1.12.0
 */
@Mapper
public interface UserParamMapper extends BaseMapper<UserParamEntity> {

    /**
     * 参数是否存在
     *
     * @param userId    用户ID
     * @param paramName 参数值
     */
    UserParamEntity selectByUserId(@Param("userId") Long userId, @Param("paramName") String paramName);

    /**
     * 修改用户参数
     *
     * @param userId     用户ID
     * @param paramName  参数名称
     * @param paramValue 参数值
     */
    void updByParamName(@Param("userId") Long userId, @Param("paramName") String paramName, @Param("paramValue") String paramValue);
}
