package com.blossom.backend.server.folder;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blossom.backend.server.folder.pojo.FolderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文件夹
 *
 * @author xzzz
 */
@Mapper
public interface FolderMapper extends BaseMapper<FolderEntity> {

    /**
     * 查询全部
     */
    List<FolderEntity> listAll(FolderEntity entity);

    /**
     * 递归获取传入ID的所有的父文件夹
     */
    List<FolderEntity> recursiveToParent(@Param("ids") List<Long> ids);

    /**
     * 递归获取传入ID的所有的子文件夹
     */
    List<FolderEntity> recursiveToChildren(@Param("ids") List<Long> ids);

    /**
     * 根据ID修改
     */
    void updById(FolderEntity entity);

    /**
     * 根据ID集合修改
     */
    void updByIds(FolderEntity entity);

    /**
     * 删除文件夹
     */
    void delByUserId(@Param("userId") Long userId);
}