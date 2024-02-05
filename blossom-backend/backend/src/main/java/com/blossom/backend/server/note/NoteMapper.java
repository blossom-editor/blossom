package com.blossom.backend.server.note;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blossom.backend.server.note.pojo.NoteEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 便签
 *
 * @author xzzz
 */
@Mapper
public interface NoteMapper extends BaseMapper<NoteEntity> {

    /**
     * 置顶/取消置顶
     *
     * @param id  便签ID
     * @param top 是否置顶
     */
    void top(@Param("id") Long id, @Param("top") Integer top);

    /**
     * 根据ID修改
     *
     * @param id ID
     * @param userId 用户ID
     */
    void updById(@Param("id") Long id, @Param("userId") Long userId, @Param("content") String content);

    /**
     * 删除便签
     */
    void delByUserId(@Param("userId") Long userId);
}
