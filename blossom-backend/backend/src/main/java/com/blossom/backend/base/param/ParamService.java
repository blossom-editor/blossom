package com.blossom.backend.base.param;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blossom.backend.base.param.pojo.ParamEntity;
import com.blossom.backend.base.param.pojo.ParamReq;
import com.blossom.common.base.exception.XzException404;
import com.blossom.common.base.util.BeanUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 系统参数信息业务接口
 *
 * @author xzzz
 */
@Slf4j
@Service
@AllArgsConstructor
public class ParamService extends ServiceImpl<ParamMapper, ParamEntity> {

    private static final Map<String, ParamEntity> CACHE = new HashMap<>(20);

    @PostConstruct
    public void refresh() {
        log.info("[    BASE] 初始化系统参数缓存");
        CACHE.clear();
        List<ParamEntity> params = baseMapper.selectList(new QueryWrapper<>());
        if (CollUtil.isEmpty(params)) {
            return;
        }
        for (ParamEntity param : params) {
            CACHE.put(param.getParamName(), param);
        }
    }

    public ParamEntity getValue(ParamEnum name) {
        return CACHE.get(name.name());
    }

    /**
     * 根据多个名称查询
     *
     * @param masking 返回数据是否脱敏
     * @param names   参数名称
     * @return 返回参数 map
     */
    public Map<String, String> selectMap(boolean masking, ParamEnum... names) {
        if (ArrayUtil.isEmpty(names)) {
            return new HashMap<>(0);
        }
        Map<String, String> result = new HashMap<>(names.length);
        for (ParamEnum name : names) {
            ParamEntity param = BeanUtil.toObj(CACHE.get(name.name()), ParamEntity.class);
            if (masking && name.getMasking()) {
                param.setParamValue(StrUtil.hide(param.getParamValue(), 0, Math.min(param.getParamValue().length(), 20)));
            }
            result.put(name.name(), param.getParamValue());
        }
        return result;
    }

    /**
     * 修改
     */
    @Transactional(rollbackFor = Exception.class)
    public Long update(ParamReq req) {
        XzException404.throwBy(req.getId() == null, "ID不得为空");
        return req.getId();
    }
}