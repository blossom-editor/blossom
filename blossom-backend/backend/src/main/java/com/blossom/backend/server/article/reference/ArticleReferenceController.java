package com.blossom.backend.server.article.reference;

import com.blossom.backend.base.auth.AuthContext;
import com.blossom.common.base.pojo.R;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 文章引用 [A#Reference]
 *
 * @author xzzz
 * @order 5
 */
@RestController
@AllArgsConstructor
@RequestMapping("/article/ref")
public class ArticleReferenceController {

    private final ArticleReferenceService baseService;

    /**
     * 文章引用关系
     *
     * @param onlyInner 是否只查询内部文章之间的引用
     * @param articleId 查询指定文章的引用关系
     */
    @GetMapping("/list")
    public R<Map<String, Object>> listAll(
            @RequestParam("onlyInner") Boolean onlyInner,
            @RequestParam(value = "articleId", required = false) Long articleId) {
        if (onlyInner == null) {
            onlyInner = true;
        }
        return R.ok(baseService.listGraph(onlyInner, AuthContext.getUserId(), articleId));
    }

}
