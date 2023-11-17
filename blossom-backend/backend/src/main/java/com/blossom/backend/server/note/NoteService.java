package com.blossom.backend.server.note;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blossom.backend.server.note.pojo.NoteEntity;
import com.blossom.backend.server.note.pojo.NoteTopReq;
import com.blossom.backend.server.note.pojo.NoteUpdReq;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 便签
 *
 * @author xzzz
 */
@Slf4j
@Service
@AllArgsConstructor
public class NoteService extends ServiceImpl<NoteMapper, NoteEntity> {

    public List<NoteEntity> listAll(Long userId) {
        LambdaQueryWrapper<NoteEntity> where = new LambdaQueryWrapper<>();
        where.eq(NoteEntity::getUserId, userId)
                .orderByDesc(NoteEntity::getTop)
                .orderByDesc(NoteEntity::getTopTime)
                .orderByDesc(NoteEntity::getId);
        return baseMapper.selectList(where);
    }

    /**
     * 便签置顶
     *
     * @param note 便签
     * @return 是否置顶
     */
    public Integer top(NoteTopReq note) {
        baseMapper.top(note.getId(), note.getTop());
        return note.getTop();
    }

    /**
     * 修改便签
     *
     * @since 1.9.0
     */
    public void updById(NoteUpdReq req) {
        baseMapper.updById(req.getId(), req.getUserId(), req.getContent());
    }
}
