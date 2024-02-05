package com.blossom.backend.base.user;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blossom.backend.base.auth.security.PasswordEncoder;
import com.blossom.backend.base.paramu.UserParamMapper;
import com.blossom.backend.base.paramu.UserParamService;
import com.blossom.backend.base.user.pojo.UserAddReq;
import com.blossom.backend.base.user.pojo.UserEntity;
import com.blossom.backend.base.user.pojo.UserUpdPwdReq;
import com.blossom.backend.server.article.draft.ArticleMapper;
import com.blossom.backend.server.article.log.ArticleLogMapper;
import com.blossom.backend.server.article.open.ArticleOpenMapper;
import com.blossom.backend.server.article.recycle.ArticleRecycleMapper;
import com.blossom.backend.server.article.reference.ArticleReferenceMapper;
import com.blossom.backend.server.article.stat.ArticleStatMapper;
import com.blossom.backend.server.article.view.ArticleViewMapper;
import com.blossom.backend.server.folder.FolderMapper;
import com.blossom.backend.server.note.NoteMapper;
import com.blossom.backend.server.picture.PictureMapper;
import com.blossom.backend.server.plan.PlanMapper;
import com.blossom.backend.server.todo.TodoMapper;
import com.blossom.backend.server.web.WebMapper;
import com.blossom.common.base.exception.XzException400;
import com.blossom.common.base.util.security.SaltUtil;
import com.blossom.common.iaas.OSManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户
 *
 * @author xzzz
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserService extends ServiceImpl<UserMapper, UserEntity> {

    private final PasswordEncoder passwordEncoder;
    private final UserParamService userParamService;
    private final UserParamMapper userParamMapper;
    private final ArticleMapper articleMapper;
    private final ArticleLogMapper articleLogMapper;
    private final ArticleViewMapper articleViewMapper;
    private final ArticleOpenMapper articleOpenMapper;
    private final ArticleReferenceMapper articleReferenceMapper;
    private final ArticleRecycleMapper articleRecycleMapper;
    private final FolderMapper folderMapper;
    private final NoteMapper noteMapper;
    private final PlanMapper planMapper;
    private final PictureMapper pictureMapper;
    private final TodoMapper todoMapper;
    private final WebMapper webMapper;
    private final ArticleStatMapper statMapper;
    private final OSManager osManager;

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
        UserEntity userByUsername = selectByUsername(req.getUsername());
        XzException400.throwBy(userByUsername != null, "用户名[" + req.getUsername() + "]已存在, 无法重复添加");
        UserEntity user = req.to(UserEntity.class);
        user.setNickName(req.getUsername());
        user.setRealName(req.getUsername());
        user.setType(req.getType());
        user.setSalt(SaltUtil.randomSalt());
        user.setPassword(passwordEncoder.encode(req.getPassword() + user.getSalt()));
        baseMapper.insertUser(user);
        userParamService.initUserParams(user.getId());
    }

    /**
     * 修改用户信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updById(UserEntity user) {
        baseMapper.updById(user);
    }

    /**
     * 修改用户信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(final Long userId) {
        List<Long> articleIds = articleMapper.listIdByUserId(userId);
        System.out.println(articleIds);
        // base_user
        baseMapper.delById(userId);
        // base_user_param
        userParamMapper.delByUserId(userId);
        // blossom_article
        articleMapper.delByUserId(userId);
        // blossom_article_open
        articleOpenMapper.delByUserId(userId);
        // blossom_article_recycle
        articleRecycleMapper.delByUserId(userId);
        // blossom_article_reference
        articleReferenceMapper.delByUserId(userId);
        if (CollUtil.isNotEmpty(articleIds)) {
            // blossom_article_log
            articleLogMapper.delByIds(articleIds);
            // blossom_article_view
            articleViewMapper.delByIds(articleIds);
        }
        // blossom_folder
        folderMapper.delByUserId(userId);
        // blossom_note
        noteMapper.delByUserId(userId);
        // blossom_picture
        pictureMapper.delByUserId(userId);
        // blossom_plan
        planMapper.delByUserId(userId);
        // blossom_stat
        statMapper.delByUserId(userId);
        // blossom_todo
        todoMapper.delByUserId(userId);
        // blossom_web
        webMapper.delByUserId(userId);

        // 删除物理文件
        final String rootPath = osManager.getDefaultPath();
        final String uid = "/U" + userId;
        osManager.deletePath(rootPath + uid);
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

    /**
     * 重置用户密码
     *
     * @param userId   用户ID
     * @param password 密码
     * @param salt     加盐
     * @since 1.11.0
     */
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long userId, String password, String salt) {
        String newPwd = passwordEncoder.encode(password + salt);
        baseMapper.updPwd(userId, newPwd);
    }

}
