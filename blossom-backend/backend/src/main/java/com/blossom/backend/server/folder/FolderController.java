package com.blossom.backend.server.folder;

import cn.hutool.core.util.ObjUtil;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.config.BlConstants;
import com.blossom.backend.server.article.stat.pojo.ArticleLineRes;
import com.blossom.backend.server.folder.pojo.*;
import com.blossom.backend.server.utils.DocUtil;
import com.blossom.common.base.exception.XzException404;
import com.blossom.common.base.pojo.R;
import com.blossom.backend.base.auth.annotation.AuthIgnore;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * 文件夹 [Folder]
 *
 * @author xzzz
 */
@RestController
@AllArgsConstructor
@RequestMapping("/folder")
public class FolderController {
    private final FolderService baseService;

    /**
     * 查询专题列表 [OP]
     */
    @AuthIgnore
    @GetMapping("/subjects/open")
    public R<List<FolderSubjectRes>> listSubjectOpen(@RequestHeader(BlConstants.REQ_HEADER_USERID) Long userId) {
        if (userId == null) {
            return R.ok(new ArrayList<>());
        }
        return R.ok(baseService.subjects(userId));
    }

    /**
     * 查询专题列表
     */
    @GetMapping("/subjects")
    public R<List<FolderSubjectRes>> listSubject() {
        return R.ok(baseService.subjects(AuthContext.getUserId()));
    }

    /**
     * 通过ID查询文件夹
     *
     * @param id 文件夹ID
     * @return 文件夹信息
     */
    @GetMapping("/info")
    public R<FolderRes> info(@RequestParam("id") Long id) {
        FolderEntity entity = baseService.selectById(id);
        XzException404.throwBy(ObjUtil.isNull(entity), "文件夹[" + id + "]信息不存在");
        FolderRes res = entity.to(FolderRes.class);
        res.setTags(DocUtil.toTagList(entity.getTags()));
        res.setType(entity.getType());
        return R.ok(res);
    }

    /**
     * 新增或修改文件夹
     *
     * @param req 文件夹对象
     */
    @PostMapping("/add")
    public R<Long> insert(@Validated @RequestBody FolderAddReq req) {
        FolderEntity folder = req.to(FolderEntity.class);
        folder.setTags(DocUtil.toTagStr(req.getTags()));
        folder.setUserId(AuthContext.getUserId());
        return R.ok(baseService.insert(folder));
    }

    /**
     * 修改文件夹
     *
     * @param req 文件夹对象
     */
    @PostMapping("/upd")
    public R<Long> update(@Validated @RequestBody FolderUpdReq req) {
        FolderEntity folder = req.to(FolderEntity.class);
        folder.setTags(DocUtil.toTagStr(req.getTags()));
        return R.ok(baseService.update(folder));
    }

    /**
     * 公开
     */
    @PostMapping("/open")
    public R<Long> open(@Validated @RequestBody FolderOpenCloseReq req) {
        FolderEntity folder = req.to(FolderEntity.class);
        return R.ok(baseService.update(folder));
    }
}
