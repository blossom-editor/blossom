package com.blossom.backend.server.article.reference;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blossom.backend.server.article.reference.pojo.ArticleReferenceEntity;
import com.blossom.backend.server.article.reference.pojo.ArticleReferenceReq;
import com.blossom.common.base.util.BeanUtil;
import com.blossom.common.base.util.security.Base64Util;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文章相关引用
 *
 * @author xzzz
 */
@Slf4j
@Service
@AllArgsConstructor
public class ArticleReferenceService extends ServiceImpl<ArticleReferenceMapper, ArticleReferenceEntity> {

    /**
     * 文章引用记录
     *
     * @param userId     用户ID
     * @param sourceId   引用源
     * @param sourceName 引用源名称
     * @param references 目标
     */
    @Transactional(rollbackFor = Exception.class)
    public void bind(Long userId, Long sourceId, String sourceName, List<ArticleReferenceReq> references) {
        delete(sourceId);
        // 没有图片, 则不保存
        if (CollUtil.isEmpty(references)) {
            return;
        }
        List<ArticleReferenceEntity> refs = BeanUtil.toList(references, ArticleReferenceEntity.class);
        for (ArticleReferenceEntity ref : refs) {
            ref.setUserId(userId);
            ref.setSourceId(sourceId);
            ref.setSourceName(sourceName);
            if (Base64Util.isBase64Img(ref.getTargetUrl())) {
                ref.setTargetUrl("");
            }
        }
        baseMapper.insertList(refs);
    }

    /**
     * 将文章修改为内部未知文章
     *
     * @param userId   用户ID
     * @param targetId 目标文章ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateToUnknown(Long userId, Long targetId) {
        baseMapper.updateToUnknown(userId, targetId, "未知文章-" + targetId);
    }

    /**
     * 将文章引用修改为内部具名文章
     *
     * @param userId   用户ID
     * @param targetId 目标文章ID
     * @param name     文章名称
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateToKnown(Long userId, Long targetId, String name) {
        baseMapper.updateToKnown(userId, targetId, name);
    }

    /**
     * 内部链接修改名称时, 同时修改双链中的名称
     *
     * @param articleId ID
     * @param name      名称
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateInnerName(Long userId, Long articleId, String name) {
        if (articleId <= 0 || StrUtil.isBlank(name)) {
            return;
        }
        baseMapper.updateSourceName(userId, articleId, name);
        baseMapper.updateTargetName(userId, articleId, name);
    }


    /**
     * 删除引用
     *
     * @param articleId 文章ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long articleId) {
        LambdaQueryWrapper<ArticleReferenceEntity> where = new LambdaQueryWrapper<>();
        where.eq(ArticleReferenceEntity::getSourceId, articleId);
        baseMapper.delete(where);
    }

    /**
     * 插件图片是否被引用
     *
     * @param url 文章url
     */
    public boolean check(String url) {
        LambdaQueryWrapper<ArticleReferenceEntity> where = new LambdaQueryWrapper<>();
        where.eq(ArticleReferenceEntity::getTargetUrl, url);
        return baseMapper.exists(where);
    }

    /**
     * 查看文章的图片引用列表
     *
     * @param articleId 文章ID
     */
    public List<ArticleReferenceEntity> listPics(Long articleId) {
        return baseMapper.listPic(articleId);
    }

    /**
     * 查询文章引用关系
     *
     * @param onlyInner 是否只查询内部文章之间的引用
     * @param userId    用户ID
     * @param articleId 文章ID, 只查询和某个文章相关的引用
     */
    public Map<String, Object> listGraph(boolean onlyInner, Long userId, Long articleId) {
        Map<String, Object> result = new HashMap<>();
        List<ArticleReferenceEntity> all = baseMapper.listGraph(onlyInner, userId, articleId);
        if (CollUtil.isEmpty(all)) {
            result.put("nodes",new String[0]);
            result.put("links",new String[0]);
            return result;
        }

        // ======================================== node ========================================
        // sourceName
        Map<Long, List<ArticleReferenceEntity>> source = all.stream().collect(Collectors.groupingBy(ArticleReferenceEntity::getSourceId));
        // targetName, 被引用的同一篇文章可能会有不同名称, 例如 github.com 分别被不同的引用时分别叫 A1,A2
        Map<String, List<ArticleReferenceEntity>> target = all.stream().collect(Collectors.groupingBy(ArticleReferenceEntity::getTargetUrl));

        Set<Node> nodes = new HashSet<>();
        source.forEach((id, list) -> {
            Node node = new Node(list.get(0).getSourceName(), ArticleReferenceEnum.INNER.getType());
            node.setInner(true);
            node.setArtId(id);
            nodes.add(node);
        });
        target.forEach((name, list) -> {
            final Integer type = list.get(0).getType();
            Node node = new Node(list.get(0).getTargetName(), type);
            if (type.equals(ArticleReferenceEnum.INNER.getType()) || type.equals(ArticleReferenceEnum.INNER_UNKNOWN.getType())) {
                node.setInner(true);
                node.setArtId(list.get(0).getTargetId());
            } else {
                node.setInner(false);
                node.setArtUrl(list.get(0).getTargetUrl());
            }
            nodes.add(node);
        });

        result.put("nodes", nodes);

        // ======================================== link ========================================
        Set<Link> links = new HashSet<>();
        for (ArticleReferenceEntity ref : all) {
            String sourceName = source.get(ref.getSourceId()).get(0).getSourceName();
            String targetName = target.get(ref.getTargetUrl()).get(0).getTargetName();
            links.add(new Link(sourceName, targetName));
        }

        result.put("links", links);

        return result;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class Node {
        private String name;
        private Integer artType;
        private Boolean inner;
        private Long artId; // inner = true
        private String artUrl;// inner = false

        public Node(String name, Integer artType) {
            this.name = name;
            this.artType = artType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if ((null == o) || (this.getClass() != o.getClass())) {
                return false;
            }

            Node t = (Node) o;

            return this.name.equals(t.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

    @Data
    @EqualsAndHashCode
    private static class Link {
        private String source;
        private String target;

        public Link(String source, String target) {
            this.source = source;
            this.target = target;
        }
    }

}
