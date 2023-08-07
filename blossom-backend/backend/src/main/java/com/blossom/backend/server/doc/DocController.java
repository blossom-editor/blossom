package com.blossom.backend.server.doc;

import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.auth.annotation.AuthIgnore;
import com.blossom.backend.config.BlConstants;
import com.blossom.backend.server.doc.pojo.DocTreeReq;
import com.blossom.backend.server.doc.pojo.DocTreeRes;
import com.blossom.common.base.pojo.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 文档 [Doc]
 *
 * @author xzzz
 * @apiNote 包含文件夹和文章
 */
@RestController
@AllArgsConstructor
@RequestMapping("/doc")
public class DocController {

    private final DocService docService;

    /**
     * 全部列表, 编辑器使用
     *
     * @return 文件夹列表
     */
    @GetMapping("/trees")
    public R<List<DocTreeRes>> trees(@ModelAttribute DocTreeReq req) {
        req.setUserId(AuthContext.getUserId());
        return R.ok(docService.listTree(req));
    }

    /**
     * 公开文章和列表, web 使用, 只查询公开的文章
     *
     * @return 文件夹列表
     */
    @AuthIgnore
    @GetMapping("/trees/open")
    public R<List<DocTreeRes>> trees(@RequestHeader(BlConstants.REQ_HEADER_USERID) Long userId) {
        if (userId == null) {
            return R.ok(new ArrayList<>());
        }
        DocTreeReq open = new DocTreeReq();
        open.setOnlyOpen(true);
        open.setUserId(userId);
        return R.ok(docService.listTree(open));
    }
}
