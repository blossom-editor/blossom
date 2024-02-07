package com.blossom.backend.base.paramu;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blossom.backend.base.paramu.pojo.UserParamEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户参数
 *
 * @since 1.12.0
 */
@Mapper
public interface UserParamMapper extends BaseMapper<UserParamEntity> {


    /**
     * 根据用户ID获取参数列表
     *
     * @param userId 用户ID
     */
    List<UserParamEntity> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和参数名获取具体参数
     *
     * @param userId    用户ID
     * @param paramName 参数值
     */
    UserParamEntity selectByUserIdAndName(@Param("userId") Long userId, @Param("paramName") String paramName);

    /**
     * 修改用户参数
     *
     * @param userId     用户ID
     * @param paramName  参数名称
     * @param paramValue 参数值
     */
    void updByParamName(@Param("userId") Long userId, @Param("paramName") String paramName, @Param("paramValue") String paramValue);

    /**
     * 新增参数
     */
    int insertByUserId(UserParamEntity param);

    /**
     * 删除参数
     * @param userId 用户ID
     */
    void delByUserId(@Param("userId") Long userId);
}
