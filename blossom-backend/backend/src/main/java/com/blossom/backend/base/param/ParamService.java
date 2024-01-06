package com.blossom.backend.base.param;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blossom.backend.base.param.pojo.ParamEntity;
import com.blossom.backend.base.param.pojo.ParamUpdReq;
import com.blossom.common.base.exception.XzException500;
import com.blossom.common.base.util.BeanUtil;
import com.blossom.common.iaas.IaasEnum;
import com.blossom.common.iaas.OSManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final OSManager osManager;

    /**
     * 初始化系统参数
     */
    @EventListener(ApplicationStartedEvent.class)
    public void refresh() {
        log.info("[   PARAM] 初始化系统参数缓存");
        CACHE.clear();
        List<ParamEntity> params = baseMapper.selectList(new QueryWrapper<>());
        if (CollUtil.isEmpty(params)) {
            return;
        }
        for (ParamEntity param : params) {
            CACHE.put(param.getParamName(), param);
        }
    }

    /**
     * 根据参数名称获取参数
     *
     * @param name 参数名称
     */
    public ParamEntity getValue(ParamEnum name) {
        ParamEntity param = CACHE.get(name.name());
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
    public Map<String, String> selectMap(boolean masking, ParamEnum... names) {
        if (ArrayUtil.isEmpty(names)) {
            return new HashMap<>(0);
        }
        Map<String, String> result = new HashMap<>(names.length);
        for (ParamEnum name : names) {
            ParamEntity param = BeanUtil.toObj(CACHE.get(name.name()), ParamEntity.class);
            XzException500.throwBy(ObjUtil.isNull(param), "缺失所有系统参数, 请检查系统参数配置[BASE_SYS_PARAM]中是否包含数据");
            if (masking && name.getMasking()) {
                param.setParamValue(StrUtil.hide(param.getParamValue(), 0, Math.min(param.getParamValue().length(), name.getMaskingLength())));
            }
            result.put(name.name(), param.getParamValue());

            // domain 涉及多个地方配置, 需要特别处理
            if (name.equals(ParamEnum.BLOSSOM_OBJECT_STORAGE_DOMAIN)) {
                result.put(name.name(), getDomain());
            }
        }
        return result;
    }

    /**
     * 获取对象存储的地址前缀, 优先从数据库中获取, 如果数据库中配置的是默认值,
     *
     * @return 对象存储的地址
     * @since 1.12.0
     */
    public String getDomain() {
        if (osManager.getProp().getOsType().equals(IaasEnum.BLOSSOM.getType())) {
            String domain = getValue(ParamEnum.BLOSSOM_OBJECT_STORAGE_DOMAIN).getParamValue();
            // 如果后台配置的图片地址为默认值
            if (ParamEnum.BLOSSOM_OBJECT_STORAGE_DOMAIN.getDefaultValue().equals(domain)) {
                // 如果配置文件中未配置地址
                if (StrUtil.isNotBlank(osManager.getDomain())) {
                    domain = osManager.getDomain();
                }
            }
            if (StrUtil.isBlank(domain)) {
                throw new XzException500("文件地址 [" + ParamEnum.BLOSSOM_OBJECT_STORAGE_DOMAIN.name() + "] 配置错误");
            }
            return domain;
        }
        return osManager.getDomain();
    }

    /**
     * 修改参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(ParamUpdReq req) {
        baseMapper.updByParamName(req.getParamName(), req.getParamValue());
    }
}