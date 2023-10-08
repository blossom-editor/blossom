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
 * @order 1
 * @apiNote 包含文件夹和文章
 */
@RestController
@AllArgsConstructor
@RequestMapping("/doc")
public class DocController {

    private final DocService docService;

    /**
     * 文档列表
     *
     * @return 文件夹列表
     * @apiNote 文档包含文章和文件夹, 文件夹分为图片文件夹和文章文件夹 {@link DocTypeEnum}
     */
    @GetMapping("/trees")
    public R<List<DocTreeRes>> trees(@ModelAttribute DocTreeReq req) {
        req.setUserId(AuthContext.getUserId());
        return R.ok(docService.listTree(req));
    }

    /**
     * 文档列表 [OP]
     *
     * @param userId 博客配置的用户ID
     * @return 文件夹列表
     * @apiNote 文档列表的公开接口, 接收
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
