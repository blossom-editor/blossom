package com.blossom.backend.server.web;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blossom.backend.server.web.pojo.WebEntity;
import com.blossom.common.base.util.SortUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 网站收藏
 *
 * @author xzzz
 */
@Service
public class WebService extends ServiceImpl<WebMapper, WebEntity> {

    /**
     * 全部列表
     *
     * @return 网站的分类:分类下的网站集合
     */
    public Map<String, List<WebEntity>> listAll(Long userId) {
        LambdaQueryWrapper<WebEntity> where = new LambdaQueryWrapper<>();
        where.eq(WebEntity::getUserId, userId);
        List<WebEntity> list = baseMapper.selectList(where);
        if (CollUtil.isEmpty(list)) {
            return new HashMap<>(0);
        }
        return list.stream()
                .sorted((w1, w2) -> SortUtil.intSort.compare(w1.getSort(), w2.getSort()))
                .collect(Collectors.groupingBy(WebEntity::getType));
    }

}
