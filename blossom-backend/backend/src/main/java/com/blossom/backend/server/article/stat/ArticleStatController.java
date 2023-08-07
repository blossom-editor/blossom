package com.blossom.backend.server.article.stat;

import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.user.pojo.BlossomUserRes;
import com.blossom.backend.config.BlConstants;
import com.blossom.backend.server.article.draft.pojo.ArticleStatRes;
import com.blossom.backend.server.article.stat.pojo.ArticleHeatmapRes;
import com.blossom.backend.server.article.stat.pojo.ArticleLineRes;
import com.blossom.common.base.pojo.R;
import com.blossom.backend.base.auth.annotation.AuthIgnore;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * 文章统计
 *
 * @author xzzz
 */
@RestController
@AllArgsConstructor
@RequestMapping("/article/stat")
public class ArticleStatController {

    private final ArticleStatService statService;

    /**
     * 文章每日编辑热力图
     *
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

    @GetMapping("/heatmap")
    public R<ArticleHeatmapRes> heatmap(@RequestParam(value = "offsetMonth", required = false) Integer offsetMonth) {
        return R.ok(statService.statArticleCountByDay(offsetMonth, AuthContext.getUserId()));
    }

    /**
     * 当前文章数和文章字数
     */
    @AuthIgnore
    @GetMapping("/words/open")
    public R<ArticleStatRes> wordOpen(@RequestHeader(BlConstants.REQ_HEADER_USERID) Long userId) {
        if (userId == null) {
            return R.ok(new ArticleStatRes());
        }
        return R.ok(statService.statCount(null, null, userId));
    }

    @GetMapping("/words")
    public R<ArticleStatRes> word() {
        return R.ok(statService.statCount(null, null, AuthContext.getUserId()));
    }

    /**
     * 文章字数折线图
     */
    @AuthIgnore
    @GetMapping("/line/open")
    public R<ArticleLineRes> lineOpen(@RequestHeader(BlConstants.REQ_HEADER_USERID) Long userId) {
        if (userId == null) {
            return R.ok(new ArticleLineRes());
        }
        return R.ok(statService.statArticleWordsByMonth(userId));
    }

    @GetMapping("/line")
    public R<ArticleLineRes> line() {
        return R.ok(statService.statArticleWordsByMonth(AuthContext.getUserId()));
    }
}
