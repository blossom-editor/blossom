package com.blossom.backend.server.article.log;

import cn.hutool.core.collection.CollUtil;
import com.blossom.backend.server.article.log.pojo.ArticleLogEntity;
import com.blossom.backend.server.article.log.pojo.ArticleLogRes;
import com.blossom.common.base.pojo.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 文章记录 [A#Log]
 *
 * @author xzzz
 * @order 6
 */
@RestController
@RequestMapping("/article/log")
@AllArgsConstructor
public class ArticleLogController {

    private final ArticleLogService baseService;

    /**
     * 文章编辑记录
     *
     * @param articleId 文章ID
     * @apiNote 文章记录不会分页, 可能会一次返回大量数据
     */
    @GetMapping
    public R<List<ArticleLogRes>> open(@RequestParam("articleId") Long articleId) {
        List<ArticleLogRes> result = new ArrayList<>();
        List<ArticleLogEntity> all = baseService.listAll(articleId);
        if (CollUtil.isNotEmpty(all)) {
            for (ArticleLogEntity log : all) {
                ArticleLogRes res = new ArticleLogRes();
                res.setI(log.getId());
                res.setDt(log.getCreTime());
                result.add(res);
            }
        }
        return R.ok(result);
    }

    /**
     * 查文章记录内容
     *
     * @param id 记录ID
     * @apiNote 返回文章的 Markdown 正文内容
     */
    @GetMapping("content")
    public R<String> content(@RequestParam("id") Long id) {
        return R.ok(baseService.content(id));
    }
}
