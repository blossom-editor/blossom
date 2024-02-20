package com.blossom.backend.server.article.thumbsUp;


import com.blossom.backend.base.auth.annotation.AuthIgnore;
import com.blossom.backend.config.BlConstants;
import com.blossom.backend.server.article.stat.ArticleStatService;
import com.blossom.backend.server.article.stat.pojo.ArticleHeatmapRes;
import com.blossom.common.base.pojo.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/article/thumbsUp")
public class ThumbsUpController {

    private final ArticleStatService statService;


//    private final  ServletServerHttpRequest request;



    /**
     * 每日编辑热力图 [OP]
     *
     * @param userId      博客配置的用户ID
     * @param offsetMonth 向前查询的月数, 填写负数, 默认为 -2
     */
    @AuthIgnore
    @GetMapping("/heatmap/open")
    public R<ArticleHeatmapRes> heatmapOpen(@RequestHeader(BlConstants.REQ_HEADER_USERID) Long userId, @RequestParam(value = "offsetMonth", required = false) Integer offsetMonth) {
        if (userId == null) {
            return R.ok(new ArticleHeatmapRes());
        }
        return R.ok(statService.statArticleCountByDay(offsetMonth, userId));
    }
}
