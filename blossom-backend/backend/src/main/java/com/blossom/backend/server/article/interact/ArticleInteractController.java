package com.blossom.backend.server.article.interact;

import com.blossom.backend.base.auth.annotation.AuthIgnore;
import com.blossom.backend.server.article.interact.pojo.LikeActionType;
import com.blossom.common.base.pojo.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * 互动相关 [A#Interact]
 *
 * @author xingxing
 * @order 9
 */
@RestController
@AllArgsConstructor
@RequestMapping("/article/interact")
public class ArticleInteractController {

    private final ArticleInteractService baseService;


    /**
     * 点赞文章
     * @param articleId
     * Return com.blossom.common.base.pojo.R<java.lang.Long>
     * Author xingxing
     * Date 2024/2/17
     */
    @AuthIgnore
    @PostMapping("/like/{articleId}")
    public R<Long> likeAction(@RequestParam LikeActionType action, @PathVariable Long articleId) {
        return R.ok(baseService.likeAction(action,articleId));
    }
}
