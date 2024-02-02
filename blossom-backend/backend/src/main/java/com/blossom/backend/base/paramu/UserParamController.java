package com.blossom.backend.base.paramu;

import cn.hutool.core.util.ObjUtil;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.auth.annotation.AuthUserType;
import com.blossom.backend.base.paramu.pojo.UserParamUpdReq;
import com.blossom.backend.base.user.UserTypeEnum;
import com.blossom.common.base.exception.XzException400;
import com.blossom.common.base.pojo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户参数配置
 *
 * @since 1.12.0
 */
@Slf4j
@RestController
@RequestMapping("/user/param")
public class UserParamController {

    @Autowired
    private UserParamService baseService;

    /**
     * 用户参数列表
     *
     * @apiNote 敏感参数会进行脱敏
     */
    @GetMapping("/list")
    public R<Map<String, String>> list() {
        Map<String, String> param = baseService.selectMap(AuthContext.getUserId(), true, UserParamEnum.values());
        return R.ok(param);
    }

    /**
     * 修改用户参数
     */
    @PostMapping("/upd")
    public R<Map<String, String>> upd(@Validated @RequestBody UserParamUpdReq req) {
        req.setUserId(AuthContext.getUserId());
        baseService.update(req);
        baseService.refresh();
        return R.ok(baseService.selectMap(AuthContext.getUserId(), true, UserParamEnum.values()));
    }

    /**
     * 修改用户参数
     *
     * @apiNote 需要管理员权限
     */
    @AuthUserType(UserTypeEnum.ADMIN)
    @PostMapping("/upd/admin")
    public R<Map<String, String>> updByAdmin(@Validated @RequestBody UserParamUpdReq req) {
        XzException400.throwBy(ObjUtil.isNull(req.getUserId()), "用户ID为必填项");
        baseService.update(req);
        baseService.refresh();
        return R.ok(baseService.selectMap(req.getUserId(), true, UserParamEnum.values()));
    }

    /**
     * 刷新用户配置
     */
    @PostMapping("/refresh")
    public R<?> paramRefresh() {
        baseService.refresh();
        return R.ok();
    }
}
