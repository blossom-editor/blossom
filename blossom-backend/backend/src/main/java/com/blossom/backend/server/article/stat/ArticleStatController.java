package com.blossom.backend.server.article.stat;

import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.config.BlConstants;
import com.blossom.backend.server.article.draft.pojo.ArticleStatRes;
import com.blossom.backend.server.article.stat.pojo.ArticleHeatmapRes;
import com.blossom.backend.server.article.stat.pojo.ArticleLineRes;
import com.blossom.common.base.pojo.R;
import com.blossom.backend.base.auth.annotation.AuthIgnore;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * 文章统计 [A#Stat]
 *
 * @author xzzz
 * @order 9
 */
@RestController
@AllArgsConstructor
@RequestMapping("/article/stat")
public class ArticleStatController {

    private final ArticleStatService statService;

    /**
     * 每日编辑热力图 [OP]
     *
     * @param userId      博客配置的用户ID
     * @param offsetMonth 向前查询的月数, 填写负数, 默认为 -2
     */
    @AuthIgnore
    @GetMapping("/heatmap/open")
    public R<ArticleHeatmapRes> heatmapOpen(
            @RequestHeader(BlConstants.REQ_HEADER_USERID) Long userId,
            @RequestParam(value = "offsetMonth", required = false) Integer offsetMonth) {
        if (userId == null) {
            return R.ok(new ArticleHeatmapRes());
        }
        return R.ok(statService.statArticleCountByDay(offsetMonth, userId));
    }

    /**
     * 每日编辑热力图
     *
     * @param offsetMonth 向前查询的月数, 填写负数, 默认为 -2
     */
    @GetMapping("/heatmap")
    public R<ArticleHeatmapRes> heatmap(@RequestParam(value = "offsetMonth", required = false) Integer offsetMonth) {
        return R.ok(statService.statArticleCountByDay(offsetMonth, AuthContext.getUserId()));
    }

    /**
     * 文章数和文章字数 [OP]
     *
     * @param userId 博客配置的用户ID
     */
    @AuthIgnore
    @GetMapping("/words/open")
    public R<ArticleStatRes> wordOpen(@RequestHeader(BlConstants.REQ_HEADER_USERID) Long userId) {
        if (userId == null) {
            return R.ok(new ArticleStatRes());
        }
        return R.ok(statService.statCount(null, null, userId));
    }

    /**
     * 文章数和文章字数
     */
    @GetMapping("/words")
    public R<ArticleStatRes> word() {
        return R.ok(statService.statCount(null, null, AuthContext.getUserId()));
    }

    /**
     * 文章字数折线图 [OP]
     *
     * @param userId 博客配置的用户ID
     */
    @AuthIgnore
    @GetMapping("/line/open")
    public R<ArticleLineRes> lineOpen(@RequestHeader(BlConstants.REQ_HEADER_USERID) Long userId) {
        if (userId == null) {
            return R.ok(new ArticleLineRes());
        }
        return R.ok(statService.statArticleWordsByMonth(userId));
    }

    /**
     * 字数折线图
     */
    @GetMapping("/line")
    public R<ArticleLineRes> line() {
        return R.ok(statService.statArticleWordsByMonth(AuthContext.getUserId()));
    }
}
