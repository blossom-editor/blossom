package com.blossom.backend.base.param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blossom.backend.base.param.pojo.ParamEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统参数信息的持久化层
 *
 * @author xzzz
 */
@Mapper
public interface ParamMapper extends BaseMapper<ParamEntity> {

    /**
     * 修改系统参数
     *
     * @param paramName  参数名称
     * @param paramValue 参数值
     */
    void updByParamName(@Param("paramName") String paramName, @Param("paramValue") String paramValue);
}