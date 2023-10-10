package com.blossom.backend.server.web;

import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.server.web.pojo.WebEntity;
import com.blossom.backend.server.web.pojo.WebSaveReq;
import com.blossom.common.base.pojo.DelReq;
import com.blossom.common.base.pojo.R;
import com.blossom.backend.base.auth.annotation.AuthIgnore;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 网站收藏 [Web]
 *
 * @author xzzz
 * @order 60
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/web")
public class WebController {

    private final WebService baseService;

    /**
     * 网站列表 [OP]
     */
    @AuthIgnore
    @GetMapping("/list")
    public R<Map<String, List<WebEntity>>> listAll() {
        return R.ok(baseService.listAll(AuthContext.getUserId()));
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R<?> save(@Validated @RequestBody WebSaveReq req) {
        if (req.getId() != null && baseService.getById(req.getId()) != null) {
            return R.ok(baseService.updateById(req.to(WebEntity.class)));
        } else {
            req.setUserId(AuthContext.getUserId());
            return R.ok(baseService.save(req.to(WebEntity.class)));
        }
    }

    /**
     * 删除
     */
    @PostMapping("/del")
    public R<?> save(@Validated @RequestBody DelReq req) {
        baseService.removeById(req.getId());
        return R.ok();
    }
}
