package com.blossom.backend.server.doc;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DocMapper {

    /**
     * 查询 PID 下的最小排序
     *
     * @param pid PID
     * @return 最小排序
     */
    Integer selectMaxSortByPid(@Param("pid") Long pid, @Param("userId") Long userId, @Param("type") Integer type);

}
