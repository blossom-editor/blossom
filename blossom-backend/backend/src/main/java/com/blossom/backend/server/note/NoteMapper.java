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
}
