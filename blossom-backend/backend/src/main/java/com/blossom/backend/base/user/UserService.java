package com.blossom.backend.base.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blossom.backend.base.auth.security.PasswordEncoder;
import com.blossom.backend.base.user.pojo.UserAddReq;
import com.blossom.backend.base.user.pojo.UserEntity;
import com.blossom.backend.base.user.pojo.UserUpdPwdReq;
import com.blossom.common.base.exception.XzException400;
import com.blossom.common.base.util.security.SaltUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户
 *
 * @author xzzz
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserService extends ServiceImpl<UserMapper, UserEntity> {

    private static final Map<String, UserEntity> userCache = new HashMap<>();

    private final PasswordEncoder passwordEncoder;

    /**
     * 查询全部用户
     */
    public List<UserEntity> listAll() {
        return baseMapper.selectList(new QueryWrapper<>());
    }

    /**
     * 根据ID查询
     */
    public UserEntity selectById(Long id) {
        return baseMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getId, id));
    }

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    public UserEntity selectByUsername(String username) {
        return baseMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getUsername, username));
    }

    /**
     * 新增用户
     */
    @Transactional(rollbackFor = Exception.class)
    public void insert(UserAddReq req) {
        UserEntity user = req.to(UserEntity.class);
        user.setNickName(req.getUsername());
        user.setRealName(req.getUsername());
        user.setType(req.getType());
        user.setSalt(SaltUtil.randomSalt());
        user.setPassword(passwordEncoder.encode(req.getPassword() + user.getSalt()));
        baseMapper.insert(user);
    }

    /**
     * 修改用户信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updById(UserEntity user) {
        baseMapper.updById(user);
    }

    /**
     * 修改密码
     */
    @Transactional(rollbackFor = Exception.class)
    public void updPassword(UserUpdPwdReq req) {
        XzException400.throwBy(!req.getNewPassword().equals(req.getConfirmPassword()), "两次输入密码不匹配");
        UserEntity user = selectById(req.getUserId());
        if (!passwordEncoder.matches(req.getPassword() + user.getSalt(), user.getPassword())) {
            throw new XzException400("密码错误");
        }

        String newPwd = passwordEncoder.encode(req.getNewPassword() + user.getSalt());
        baseMapper.updPwd(req.getUserId(), newPwd);
    }


}
