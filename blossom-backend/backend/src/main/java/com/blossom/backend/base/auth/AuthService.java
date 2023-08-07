package com.blossom.backend.base.auth;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.blossom.backend.base.auth.exception.AuthException;
import com.blossom.backend.base.auth.exception.AuthRCode;
import com.blossom.backend.base.auth.pojo.LoginDTO;
import com.blossom.backend.base.auth.security.PasswordEncoder;
import com.blossom.backend.base.auth.pojo.AccessToken;
import com.blossom.backend.base.auth.repo.TokenRepository;
import com.blossom.backend.base.auth.token.TokenEncoder;
import com.blossom.backend.base.user.UserService;
import com.blossom.backend.base.user.pojo.UserEntity;
import com.blossom.common.base.enums.YesNo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * 授权服务
 *
 * @author xzzz
 */
@Slf4j
@Service
public class AuthService extends AbstractAuthService {

    @Autowired
    private UserService userService;

    public AuthService(AuthProperties properties,
                       TokenRepository tokenRepository,
                       TokenEncoder tokenEncoder,
                       PasswordEncoder passwordEncoder,
                       ApplicationContext applicationContext) {
        super(properties, tokenRepository, tokenEncoder, passwordEncoder, applicationContext);
    }

    /**
     * 根据用户名密码登录
     */
    @Override
    protected void loginByPassword(AccessToken accessToken, LoginDTO login) {
        AuthException.throwBy(StrUtil.isBlank(login.getPassword()), AuthRCode.USERNAME_OR_PWD_FAULT);
        UserEntity user = userService.selectByUsername(login.getUsername());
        AuthException.throwBy(ObjUtil.isNull(user), AuthRCode.USERNAME_OR_PWD_FAULT);
        AuthException.throwBy(!passwordEncoder.matches(login.getPassword() + user.getSalt(), user.getPassword()), AuthRCode.USERNAME_OR_PWD_FAULT);
        fillUserDetail(accessToken, user);
    }

    /**
     * 退出
     *
     * @param token token 令牌
     */
    public void logout(String token) {
        tokenRepository.remove(token);
    }

    /**
     * 用户注册
     */
    public void register() {

    }

    /**
     * 检查 AccessToken 信息
     *
     * @return
     */
    public AccessToken check() {
        return AuthContext.getContext();
    }
}
