package com.blossom.backend.server.note;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.server.note.pojo.NoteEntity;
import com.blossom.backend.server.note.pojo.NoteSaveReq;
import com.blossom.backend.server.note.pojo.NoteTopReq;
import com.blossom.backend.server.note.pojo.NoteUpdReq;
import com.blossom.common.base.pojo.DelReq;
import com.blossom.common.base.pojo.R;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 便签 [Note]
 *
 * @order 50
 * @author xzzz
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/note")
public class NoteController extends ServiceImpl<NoteMapper, NoteEntity> {

    private final NoteService baseService;

    /**
     * 全部列表
     */
    @GetMapping("/list")
    public R<List<NoteEntity>> listAll() {
        return R.ok(baseService.listAll(AuthContext.getUserId()));
    }

    /**
     * 新增
     */
    @PostMapping("/add")
    public R<?> add(@Validated @RequestBody NoteSaveReq note) {
        NoteEntity entity = note.to(NoteEntity.class);
        entity.setUserId(AuthContext.getUserId());
        baseService.save(entity);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/upd")
    public R<?> upd(@Validated @RequestBody NoteUpdReq note) {
        note.setUserId(AuthContext.getUserId());
        baseService.updById(note);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/del")
    public R<?> add(@Validated @RequestBody DelReq note) {
        baseService.removeById(note.getId());
        return R.ok();
    }

    /**
     * 置顶/取消置顶
     */
    @PostMapping("/top")
    public R<?> top(@Validated @RequestBody NoteTopReq note) {
        baseService.top(note);
        return R.ok();
    }
}

