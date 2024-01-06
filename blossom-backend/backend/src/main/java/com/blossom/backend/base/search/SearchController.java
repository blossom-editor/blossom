package com.blossom.backend.base.search;

import com.blossom.backend.base.auth.AuthContext;
import com.blossom.common.base.pojo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 搜索接口
 */
@Slf4j
@RestController
public class SearchController {

    @Autowired
    private Searcher searcher;

    /**
     * 搜索
     *
     * @param keyword  搜索关键字
     * @param hlColor  高亮颜色
     * @param operator 是否全部匹配
     * @param debug    是否DEBUG, 为 true 时高亮前后缀为【】
     * @since 1.12.0
     */
    @GetMapping("/search")
    public R<SearchRes> search(@RequestParam("keyword") String keyword,
                                     @RequestParam("hlColor") String hlColor,
                                     @RequestParam("operator") boolean operator,
                                     @RequestParam("debug") boolean debug) {
        return R.ok(searcher.search(keyword, AuthContext.getUserId(), hlColor, operator, debug));
    }
}
