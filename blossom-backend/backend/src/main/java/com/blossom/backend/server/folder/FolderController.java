package com.blossom.backend.server.folder;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjUtil;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.auth.annotation.AuthIgnore;
import com.blossom.backend.config.BlConstants;
import com.blossom.backend.server.article.draft.pojo.ArticleUpdTagReq;
import com.blossom.backend.server.doc.DocService;
import com.blossom.backend.server.folder.pojo.*;
import com.blossom.backend.server.utils.DocUtil;
import com.blossom.common.base.exception.XzException404;
import com.blossom.common.base.pojo.DelReq;
import com.blossom.common.base.pojo.R;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * 文件夹 [Folder]
 *
 * @author xzzz
 * @order 2
 */
@RestController
@AllArgsConstructor
@RequestMapping("/folder")
public class FolderController {
    private final FolderService baseService;
    private final DocService docService;

    /**
     * 查询专题列表 [OP]
     *
     * @param userId 博客配置的用户ID
     */
    @AuthIgnore
    @GetMapping("/subjects/open")
    public R<List<FolderSubjectRes>> listSubjectOpen(@RequestHeader(BlConstants.REQ_HEADER_USERID) Long userId) {
        if (userId == null) {
            return R.ok(new ArrayList<>());
        }
        return R.ok(baseService.subjects(userId, true, false));
    }

    /**
     * 查询专题列表
     *
     * @param starStatus 公开状态
     */
    @GetMapping("/subjects")
    public R<List<FolderSubjectRes>> listSubject() {
        return R.ok(baseService.subjects(AuthContext.getUserId(), false, true));
    }

    /**
     * 星标文件夹
     *
     * @param req 目录文件夹
     * @since 1.14.0
     */
    @PostMapping("/star")
    public R<Long> star(@Validated @RequestBody FolderStarReq req) {
        FolderEntity folder = req.to(FolderEntity.class);
        folder.setUserId(AuthContext.getUserId());
        return R.ok(baseService.update(folder));
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
        res.setStarStatus(entity.getStarStatus());
        return R.ok(res);
    }

    /**
     * 新增或修改文件夹
     *
     * @param req 文件夹对象
     */
    @PostMapping("/add")
    public R<FolderEntity> insert(@Validated @RequestBody FolderAddReq req) {
        FolderEntity folder = req.to(FolderEntity.class);
        folder.setTags(DocUtil.toTagStr(req.getTags()));
        folder.setUserId(AuthContext.getUserId());
        // 如果新增到底部, 获取最大的排序
        if (BooleanUtil.isTrue(req.getAddToLast())) {
            folder.setSort(docService.selectMaxSortByPid(
                    req.getPid(),
                    AuthContext.getUserId(),
                    Objects.requireNonNull(FolderTypeEnum.getType(req.getType()))) + 1);
        }
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
        // 检查排序是否重复
//        if (folder.getSort() != null && folder.getPid() != null) {
//            final long newPid = folder.getPid();
//            docSortChecker.checkUnique(CollUtil.newArrayList(newPid),
//                    CollUtil.newArrayList(folder),
//                    null,
//                    FolderTypeEnum.ARTICLE,
//                    AuthContext.getUserId());
//        }
        return R.ok(baseService.update(folder));
    }

    /**
     * 修改文件夹
     *
     * @param req 文件夹对象
     */
    @PostMapping("/upd/name")
    public R<?> updateName(@Validated @RequestBody FolderUpdNameReq req) {
        FolderEntity folder = req.to(FolderEntity.class);
        baseService.update(folder);
        return R.ok();
    }

    /**
     * 快速增加/删除标签
     *
     * @since 1.10.0
     */
    @PostMapping("/upd/tag")
    public R<List<String>> updTag(@Validated @RequestBody ArticleUpdTagReq req) {
        FolderEntity info = baseService.selectById(req.getId());
        List<String> tags = DocUtil.toTagList(info.getTags());
        if (tags.contains(req.getTag().toLowerCase()) || tags.contains(req.getTag().toUpperCase())) {
            tags.remove(req.getTag().toLowerCase());
            tags.remove(req.getTag().toUpperCase());
        } else {
            tags.add(req.getTag());
        }
        FolderEntity folder = req.to(FolderEntity.class);
        folder.setTags(DocUtil.toTagStr(tags));
        baseService.update(folder);
        return R.ok(tags);
    }

    /**
     * 公开文件夹
     */
    @PostMapping("/open")
    public R<Long> open(@Validated @RequestBody FolderOpenCloseReq req) {
        FolderEntity folder = req.to(FolderEntity.class);
        return R.ok(baseService.update(folder));
    }

    /**
     * 删除文件夹
     *
     * @apiNote 文件夹下无文章时才可删除
     */
    @PostMapping("/del")
    public R<?> del(@Validated @RequestBody DelReq req) {
        baseService.delete(req.getId());
        return R.ok();
    }
}
