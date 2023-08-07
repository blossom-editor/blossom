package com.blossom.backend.server.picture;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blossom.backend.server.picture.pojo.PictureEntity;
import com.blossom.backend.server.picture.pojo.PictureStatRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 图片
 *
 * @author xzzz
 */
@Mapper
public interface PictureMapper extends BaseMapper<PictureEntity> {

    /**
     * 查询全部
     */
    List<PictureEntity> listAll(PictureEntity entity);

    /**
     * 查询所有的 pid, 并去重
     *
     * @param userId 用户ID
     */
    List<Long> listDistinctPid(@Param("userId") Long userId);

    /**
     * 修改
     */
    void updById(PictureEntity entity);

    /**
     * 统计文件夹下的图片
     *
     * @param pid 文件夹ID
     */
    PictureStatRes stat(@Param("userId") Long userId, @Param("pid") Long pid);
}