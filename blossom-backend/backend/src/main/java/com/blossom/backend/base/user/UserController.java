package com.blossom.backend.base.user;

import cn.hutool.core.util.ObjUtil;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.auth.annotation.AuthIgnore;
import com.blossom.backend.base.param.ParamEnum;
import com.blossom.backend.base.param.ParamService;
import com.blossom.backend.base.paramu.UserParamEnum;
import com.blossom.backend.base.paramu.UserParamService;
import com.blossom.backend.base.sys.SysService;
import com.blossom.backend.base.user.pojo.*;
import com.blossom.backend.config.BlConstants;
import com.blossom.backend.server.article.draft.pojo.ArticleStatRes;
import com.blossom.backend.server.article.stat.ArticleStatService;
import com.blossom.common.base.exception.XzException400;
import com.blossom.common.base.exception.XzException404;
import com.blossom.common.base.pojo.R;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户 [User]
 *
 * @author xzzz
 */
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final ArticleStatService articleService;
    private final SysService sysService;
    private final ParamService paramService;
    private final UserParamService userParamService;

    /**
     * 用户信息
     */
    @GetMapping("/info")
    public R<BlossomUserRes> user() {
        BlossomUserRes user = userService.selectById(AuthContext.getUserId()).to(BlossomUserRes.class);
        user.setOsRes(sysService.getOsConfig());
        Map<String, String> paramMap = paramService.selectMap(true, ParamEnum.values());
        user.setParams(paramMap);
        Map<String, String> userParamMap = userParamService.selectMap(AuthContext.getUserId(), true, UserParamEnum.values());
        user.setUserParams(userParamMap);
        return R.ok(user);
    }

    /**
     * 用户信息 [OP]
     *
     * @param userId 博客配置的用户ID
     */
    @AuthIgnore
    @GetMapping("/info/open")
    public R<BlossomUserRes> userOpen(@RequestHeader(BlConstants.REQ_HEADER_USERID) Long userId) {
        if (userId == null) {
            return R.ok(new BlossomUserRes());
        }
        UserEntity u = userService.selectById(userId);
        XzException404.throwBy(ObjUtil.isNull(u), "用户不存在");
        BlossomUserRes user = u.to(BlossomUserRes.class);
        ArticleStatRes stat = articleService.statCount(null, null, userId);
        user.setArticleWords(stat.getArticleWords());
        user.setArticleCount(stat.getArticleCount());
        return R.ok(user);
    }

    /**
     * 修改用户
     */
    @PostMapping("/upd")
    public R<?> update(@Validated @RequestBody UserUpdReq req) {
        UserEntity user = req.to(UserEntity.class);
        user.setId(AuthContext.getUserId());
        userService.updById(user);
        return R.ok();
    }

    /**
     * 修改密码
     */
    @PostMapping("/upd/pwd")
    public R<?> updatePassword(@Validated @RequestBody UserUpdPwdReq req) {
        req.setUserId(AuthContext.getUserId());
        userService.updPassword(req);
        return R.ok();
    }

    /**
     * 新增用户
     */
    @PostMapping("/add")
    public R<?> add(@Validated @RequestBody UserAddReq req) {
        UserEntity curUser = userService.getById(AuthContext.getUserId());
        if (curUser == null || !UserTypeEnum.ADMIN.getType().equals(curUser.getType())) {
            throw new XzException400("您没有权限添加用户");
        }
        userService.insert(req);
        return R.ok();
    }
}
