package com.blossom.backend.server.article.stat;

import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.auth.annotation.AuthIgnore;
import com.blossom.backend.base.auth.annotation.AuthUserType;
import com.blossom.backend.base.auth.exception.AuthException;
import com.blossom.backend.base.auth.exception.AuthRCode;
import com.blossom.backend.base.user.UserTypeEnum;
import com.blossom.backend.config.BlConstants;
import com.blossom.backend.server.article.draft.pojo.ArticleStatRes;
import com.blossom.backend.server.article.stat.pojo.ArticleHeatmapRes;
import com.blossom.backend.server.article.stat.pojo.ArticleLineRes;
import com.blossom.backend.server.article.stat.pojo.ArticleWordsRes;
import com.blossom.backend.server.article.stat.pojo.ArticleWordsSaveReq;
import com.blossom.common.base.pojo.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
     * 文章数和文章字数
     *
     * @apiNote 只有管理员可以查看
     * @since 1.13.0
     */
    @AuthUserType(UserTypeEnum.ADMIN)
    @GetMapping("/words/user")
    public R<ArticleStatRes> word(@RequestParam("id") Long id) {
        if (!AuthContext.getType().equals(UserTypeEnum.ADMIN.getType())) {
            throw new AuthException(AuthRCode.PERMISSION_DENIED);
        }
        return R.ok(statService.statCount(null, null, id));
    }

    /**
     * 字数统计列表
     *
     * @since 1.8.0
     */
    @GetMapping("/words/list")
    public R<List<ArticleWordsRes>> wordsList() {
        return R.ok(statService.wordsList(AuthContext.getUserId()), ArticleWordsRes.class);
    }

    /**
     * 保存字数统计信息
     *
     * @since 1.8.0
     */
    @PostMapping("/words/save")
    public R<?> wordsSave(@RequestBody ArticleWordsSaveReq req) {
        statService.updateWords(req, AuthContext.getUserId());
        return R.ok();
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
     * 近36月字数折线图
     *
     * @apiNote 只查询最近36个月统计内容
     */
    @GetMapping("/line")
    public R<ArticleLineRes> line() {
        return R.ok(statService.statArticleWordsByMonth(AuthContext.getUserId()));
    }
}
