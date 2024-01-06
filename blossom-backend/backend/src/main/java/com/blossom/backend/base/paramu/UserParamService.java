package com.blossom.backend.base.paramu;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blossom.backend.base.param.ParamEnum;
import com.blossom.backend.base.param.ParamMapper;
import com.blossom.backend.base.param.pojo.ParamEntity;
import com.blossom.backend.base.paramu.pojo.UserParamEntity;
import com.blossom.backend.base.paramu.pojo.UserParamUpdReq;
import com.blossom.backend.base.user.UserMapper;
import com.blossom.backend.base.user.pojo.UserEntity;
import com.blossom.common.base.exception.XzException500;
import com.blossom.common.base.util.BeanUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户参数
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserParamService extends ServiceImpl<UserParamMapper, UserParamEntity> {

    private final ParamMapper paramMapper;
    private final UserMapper userMapper;

    private static final Map<Long, Map<String, UserParamEntity>> CACHE = new HashMap<>(20);

    /**
     * 启动时刷新数据
     */
    @EventListener(ApplicationStartedEvent.class)
    public void refresh() {
        log.info("[ U_PARAM] 初始化用户参数缓存");
        CACHE.clear();
        List<UserEntity> users = userMapper.selectList(new QueryWrapper<>());
        // 初始化所有用户的配置参数
        for (UserEntity user : users) {
            initUserParams(user.getId());
        }
    }

    /**
     * 获取博客地址
     * <p>在 1.12.0 版本前, 博客的地址配置在 SysParam 中
     * <p>如果 SysParam 配置中有值, 则使用 SysParam 中的值, 否则使用默认值
     */
    public String getWebArticleUrl() {
        List<ParamEntity> sysParams = paramMapper.selectList(new QueryWrapper<>());
        String WEB_ARTICLE_URL = "";

        // 获取数据库中的配置的博客地址
        for (ParamEntity param : sysParams) {
            if (param.getParamName().equals(ParamEnum.WEB_ARTICLE_URL.name())) {
                WEB_ARTICLE_URL = param.getParamValue();
                break;
            }
        }
        // 如果数据库中没有配置, 则使用默认值
        if (StrUtil.isBlank(WEB_ARTICLE_URL)) {
            WEB_ARTICLE_URL = UserParamEnum.WEB_ARTICLE_URL.getDefaultValue();
        }
        return WEB_ARTICLE_URL;
    }

    /**
     * 初始化用户参数
     *
     * @param userId 用户ID
     */
    public void initUserParams(Long userId) {
        Map<String, UserParamEntity> params = new HashMap<>(UserParamEnum.values().length);
        List<UserParamEntity> userParams = baseMapper.selectByUserId(userId);
        Map<String, UserParamEntity> userParamMap = userParams.stream().collect(Collectors.toMap(UserParamEntity::getParamName, p -> p));
        for (UserParamEnum param : UserParamEnum.values()) {
            UserParamEntity storeParam = userParamMap.get(param.name());
            // 该用户不存在该参数, 则新增参数
            if (storeParam == null) {
                UserParamEntity istParam = new UserParamEntity();
                istParam.setUserId(userId);
                istParam.setParamName(param.name());
                istParam.setParamValue(param.getDefaultValue());
                // 博客的地址需要特别兼容, 用于适配 1.12.0 之前的版本
                if (param.name().equals(UserParamEnum.WEB_ARTICLE_URL.name())) {
                    istParam.setParamValue(getWebArticleUrl());
                }
                baseMapper.insertByUserId(istParam);
                params.put(param.name(), istParam);
            } else {
                params.put(param.name(), storeParam);
            }
        }
        CACHE.put(userId, params);
    }


    /**
     * 根据参数名称获取参数
     *
     * @param name 参数名称
     */
    public UserParamEntity getValue(Long userId, UserParamEnum name) {
        UserParamEntity param = CACHE.get(userId).get(name.name());
        XzException500.throwBy(ObjUtil.isNull(param), String.format("缺失系统参数[%s], 请检查系统参数配置", name.name()));
        return param;
    }

    /**
     * 根据多个名称查询
     *
     * @param masking 返回数据是否脱敏
     * @param names   参数名称
     * @return 返回参数 map
     */
    public Map<String, String> selectMap(Long userId, boolean masking, UserParamEnum... names) {
        if (ArrayUtil.isEmpty(names)) {
            return new HashMap<>(0);
        }
        Map<String, String> result = new HashMap<>(names.length);
        for (UserParamEnum name : names) {
            ParamEntity param = BeanUtil.toObj(CACHE.get(userId).get(name.name()), ParamEntity.class);
            XzException500.throwBy(ObjUtil.isNull(param), "缺失所有系统参数, 请检查用户参数配置[BASE_USER_PARAM]中是否包含数据");
            if (masking && name.getMasking()) {
                param.setParamValue(StrUtil.hide(param.getParamValue(), 0, Math.min(param.getParamValue().length(), name.getMaskingLength())));
            }
            result.put(name.name(), param.getParamValue());
        }
        return result;
    }

    /**
     * 修改参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(UserParamUpdReq req) {
        baseMapper.updByParamName(req.getUserId(), req.getParamName(), req.getParamValue());
    }
}
