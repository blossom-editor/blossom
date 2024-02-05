package com.blossom.backend.base.user;

import cn.hutool.core.util.ObjUtil;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.auth.AuthService;
import com.blossom.backend.base.auth.annotation.AuthIgnore;
import com.blossom.backend.base.auth.annotation.AuthUserType;
import com.blossom.backend.base.param.ParamEnum;
import com.blossom.backend.base.param.ParamService;
import com.blossom.backend.base.paramu.UserParamEnum;
import com.blossom.backend.base.paramu.UserParamService;
import com.blossom.backend.base.sys.SysService;
import com.blossom.backend.base.user.pojo.*;
import com.blossom.backend.config.BlConstants;
import com.blossom.backend.server.article.draft.pojo.ArticleStatRes;
import com.blossom.backend.server.article.stat.ArticleStatService;
import com.blossom.common.base.exception.XzException404;
import com.blossom.common.base.exception.XzException500;
import com.blossom.common.base.pojo.R;
import com.blossom.common.base.util.spring.SpringUtil;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    private final AuthService authService;
    private final ArticleStatService articleService;
    private final SysService sysService;
    private final ParamService paramService;
    private final UserParamService userParamService;

    /**
     * 获取用户列表
     *
     * @apiNote 需要管理员权限
     * @since 1.13.0
     */
    @AuthUserType(UserTypeEnum.ADMIN)
    @GetMapping("/list")
    public R<List<UserListRes>> list() {
        return R.ok(userService.listAll(), UserListRes.class);
    }

    /**
     * 用户信息
     *
     * @param id 用户ID
     * @apiNote 根据用户ID获取用户信息, 需要管理员权限
     */
    @AuthUserType(UserTypeEnum.ADMIN)
    @GetMapping("/info/admin")
    public R<BlossomUserRes> user(@RequestParam("id") Long id) {
        return getUserById(id);
    }

    /**
     * 新增用户
     *
     * @apiNote 需要管理员权限
     */
    @AuthUserType(UserTypeEnum.ADMIN)
    @PostMapping("/add")
    public R<?> add(@Validated @RequestBody UserAddReq req) {
        userService.insert(req);
        return R.ok();
    }

    /**
     * 修改用户
     *
     * @apiNote 需要管理员权限
     */
    @AuthUserType(UserTypeEnum.ADMIN)
    @PostMapping("/upd/admin")
    public R<?> updateImportant(@Validated @RequestBody UserUpdAdminReq req) {
        UserEntity user = req.to(UserEntity.class);
        userService.updById(user);
        return R.ok();
    }

    /**
     * 禁用启用
     */
    @AuthUserType(UserTypeEnum.ADMIN)
    @PostMapping("/disabled")
    public R<?> disabled(@Validated @RequestBody UserDisabledReq req) {
        if (req.getId().equals(AuthContext.getUserId())) {
            throw new XzException500("不能禁用自己");
        }
        UserEntity user = req.to(UserEntity.class);
        userService.updById(user);
        authService.kickout(req.getId());
        return R.ok();
    }

    /**
     * 用户信息
     *
     * @apiNote 当前登录用户的用户信息
     */
    @GetMapping("/info")
    public R<BlossomUserRes> user() {
        return getUserById(AuthContext.getUserId());
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
        Map<String, String> userParamMap = userParamService.selectMap(userId, true, UserParamEnum.values());
        user.setUserParams(userParamMap);
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
     * 删除用户
     */
    @AuthUserType(UserTypeEnum.ADMIN)
    @PostMapping("/del")
    public R<?> delete(@Validated @RequestBody UserDeleteReq req) {
        if (req.getId().equals(AuthContext.getUserId())) {
            throw new XzException500("不能删除自己");
        }
        userService.delete(req.getId());
        authService.kickout(req.getId());
        return R.ok();
    }

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     */
    private R<BlossomUserRes> getUserById(Long userId) {
        BlossomUserRes user = userService.selectById(userId).to(BlossomUserRes.class);
        user.setOsRes(sysService.getOsConfig());
        Map<String, String> paramMap = paramService.selectMap(true, ParamEnum.values());
        user.setParams(paramMap);
        paramMap.put("SERVER_VERSION", SpringUtil.get("project.base.version"));
        Map<String, String> userParamMap = userParamService.selectMap(userId, true, UserParamEnum.values());
        user.setUserParams(userParamMap);
        return R.ok(user);
    }

}
