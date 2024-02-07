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
     * 根据路径名称查询
     *
     * @param pathName 路径名称
     */
    PictureEntity selectByPathName(@Param("pathName") String pathName);

    /**
     * 修改
     */
    void updById(PictureEntity entity);

    /**
     * 文件专题
     *
     * @param ids    文件ID
     * @param pid    文件夹ID
     * @param userId 用户ID
     * @since 1.10.0
     */
    void transfer(@Param("ids") List<Long> ids, @Param("pid") Long pid, @Param("userId") Long userId);

    /**
     * 统计文件夹下的图片
     *
     * @param pid 文件夹ID
     */
    PictureStatRes stat(@Param("userId") Long userId, @Param("pid") Long pid);

    /**
     * 删除图片
     */
    void delByUserId(@Param("userId") Long userId);
}