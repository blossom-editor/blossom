package com.blossom.backend.base.auth;

import cn.hutool.core.util.StrUtil;
import com.blossom.backend.base.auth.annotation.AuthIgnore;
import com.blossom.backend.base.auth.annotation.AuthUserType;
import com.blossom.backend.base.auth.pojo.AccessToken;
import com.blossom.backend.base.auth.pojo.KickOutReq;
import com.blossom.backend.base.auth.pojo.LoginReq;
import com.blossom.backend.base.user.UserTypeEnum;
import com.blossom.common.base.exception.XzException400;
import com.blossom.common.base.pojo.R;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录授权 [Auth]
 *
 * @author xzzz
 */
@RestController
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 登录[OP]
     *
     * @param req 请求对象
     * @return token信息
     */
    @AuthIgnore
    @PostMapping("login")
    public R<AccessToken> login(HttpServletRequest request, @Validated @RequestBody LoginReq req) {
        XzException400.throwBy(StrUtil.isBlank(req.getClientId()), "客户端ID[ClientId]为必填项");
        XzException400.throwBy(StrUtil.isBlank(req.getGrantType()), "授权方式[GrantType]为必填项");
        return R.ok(authService.login(request, req));
    }

    /**
     * 用户退出
     *
     * @apiNote 如果为 JWT 授权方式, 则退出功能无效, 调用该接口后 Token 仍然可以正常使用。
     * 客户端可以正常调用该接口以校验 Token 是否有效, 然后自行从 Storage 或 Cookie 中删除 Token 即可。
     */
    @PostMapping("logout")
    public R<?> logout() {
        authService.logout(AuthContext.getToken());
        return R.ok();
    }

    /**
     * 踢出用户
     *
     * @since 1.13.0
     */
    @AuthUserType(UserTypeEnum.ADMIN)
    @PostMapping("kickout")
    public R<?> kickout(@RequestBody KickOutReq req) {
        authService.kickout(req.getUserId());
        return R.ok();
    }

    /**
     * 检查 Token 状态
     */
    @GetMapping("check")
    public R<AccessToken> check() {
        return R.ok(authService.check());
    }
}
