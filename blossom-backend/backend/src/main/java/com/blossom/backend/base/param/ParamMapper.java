package com.blossom.backend.base.param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blossom.backend.base.param.pojo.ParamEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 系统参数信息的持久化层
 *
 * @author xzzz
 */
@Mapper
public interface ParamMapper extends BaseMapper<ParamEntity> {

}