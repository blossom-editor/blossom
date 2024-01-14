package com.blossom.backend.server.article.recycle;


import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.server.article.recycle.pojo.ArticleRecycleListRes;
import com.blossom.backend.server.article.recycle.pojo.ArticleRecycleRestoreReq;
import com.blossom.common.base.pojo.R;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章回收站 [A#Recycle]
 *
 * @author xzzz
 * @order 7
 * @since 1.10.0
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/article/recycle")
public class ArticleRecycleController {

    private final ArticleRecycleService baseService;

    /**
     * 查询列表
     *
     * @apiNote 返回简单字段
     */
    @GetMapping("/list")
    public R<List<ArticleRecycleListRes>> listAll() {
        return R.ok(baseService.listAll(AuthContext.getUserId()), ArticleRecycleListRes.class);
    }

    /**
     * 还原文章
     *
     * @apiNote 还原不包含收藏、公开状态，文章版本将清零。文章的 Html 解析结果，目录也将清空。
     */
    @PostMapping("/restore")
    public R<?> restore(@Validated @RequestBody ArticleRecycleRestoreReq req) {
        baseService.restore(AuthContext.getUserId(), req.getId());
        return R.ok();
    }
}
