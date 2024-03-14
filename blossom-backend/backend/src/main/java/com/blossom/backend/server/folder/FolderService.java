package com.blossom.backend.server.folder;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blossom.backend.server.article.TagEnum;
import com.blossom.backend.server.article.draft.ArticleService;
import com.blossom.backend.server.article.draft.pojo.ArticleEntity;
import com.blossom.backend.server.article.draft.pojo.ArticleQueryReq;
import com.blossom.backend.server.doc.pojo.DocTreeRes;
import com.blossom.backend.server.folder.pojo.FolderEntity;
import com.blossom.backend.server.folder.pojo.FolderQueryReq;
import com.blossom.backend.server.folder.pojo.FolderSubjectRes;
import com.blossom.backend.server.picture.PictureService;
import com.blossom.backend.server.picture.pojo.PictureEntity;
import com.blossom.backend.server.utils.DocUtil;
import com.blossom.common.base.enums.YesNo;
import com.blossom.common.base.exception.XzException400;
import com.blossom.common.base.exception.XzException404;
import com.blossom.common.base.exception.XzException500;
import com.blossom.common.base.util.DateUtils;
import com.blossom.common.base.util.PrimaryKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件夹
 *
 * @author xzzz
 */
@Slf4j
@Service
public class FolderService extends ServiceImpl<FolderMapper, FolderEntity> {
    private ArticleService articleService;
    private PictureService pictureService;

    @Autowired
    public void setArticleService(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Autowired
    public void setPictureService(PictureService pictureService) {
        this.pictureService = pictureService;
    }

    /**
     * 专题列表
     * <p>1. 查询全部公开专题
     * <p>2. 由于专题可能会有子文件夹, 所以要查询所有子文件夹.
     * <p>3. 查询出这些文件夹下的所有文章
     * <p>4. 相同专题的所有文件夹ID归为一组.
     * <p>5. 通过文件夹ID获取到专题下的所有文章, 从而统计文章的总字数, 修改时间, 创建时间等.
     * <p>6. 如果文章包含 TOC 标签, 则该文章为专题的目录, 专题的默认跳转会跳转至该目录
     */
    public List<FolderSubjectRes> subjects(Long userId) {
        // 1. 查询所有公开的专题
        FolderEntity where = new FolderEntity();
        where.setTags(TagEnum.subject.name());
        where.setOpenStatus(YesNo.YES.getValue());
        where.setUserId(userId);
        List<FolderEntity> allOpenSubject = baseMapper.listAll(where);
        if (CollUtil.isEmpty(allOpenSubject)) {
            return new ArrayList<>();
        }

        // 专题的ID
        List<Long> allOpenSubjectIds = allOpenSubject.stream().map(FolderEntity::getId).collect(Collectors.toList());

        // 2. 查询全部专题的子文件夹
        List<FolderEntity> allOpenSubjectChildFolders = baseMapper.recursiveToChildren(CollUtil.newArrayList(allOpenSubjectIds));
        allOpenSubjectIds.addAll(allOpenSubjectChildFolders.stream().map(FolderEntity::getId).collect(Collectors.toList()));

        // 3. 查询这些文件夹下的所有文章
        ArticleQueryReq articleWhere = new ArticleQueryReq();
        articleWhere.setPids(allOpenSubjectIds);
        articleWhere.setUserId(userId);
        // 统计专题信息时, 会包含非公开文章
        // articleWhere.setOpenStatus(YesNo.YES.getValue());
        List<ArticleEntity> articles = articleService.listAll(articleWhere);

        List<FolderSubjectRes> results = new ArrayList<>();

        for (FolderEntity subject : allOpenSubject) {
            // 专题对象, 包含字数, 更新日期等信息
            FolderSubjectRes result = subject.to(FolderSubjectRes.class);
            // 默认专题字数
            result.setSubjectWords(0);
            // 默认专题修改时间
            result.setSubjectUpdTime(subject.getCreTime());
            // 4. 这个专题下的所有文件夹ID
            List<Long> subjectAllId = DocUtil.getChildrenIds(subject.getId(), allOpenSubjectChildFolders);
            // 5. 遍历文章, 将文章归属到某个专题下, 并统计相关字数, 日期等信息
            for (ArticleEntity article : articles) {
                if (subjectAllId.contains(article.getPid())) {
                    result.setSubjectWords(result.getSubjectWords() + article.getWords());
                    if (DateUtils.compare(result.getSubjectUpdTime(), article.getUpdTime()) < 0) {
                        result.setSubjectUpdTime(article.getUpdTime());
                    }
                    // 6. 如果是TOC,则设置 TOC_ID
                    if (TagEnum.isToc(article.getTags())) {
                        result.setTocId(article.getId());
                    }
                }
            }

            results.add(result);
        }

        return results;
    }

    /**
     * 查询全部文件夹, 并转换成 {@link DocTreeRes}
     */
    public List<DocTreeRes> listTree(FolderQueryReq req) {
        List<FolderEntity> folders = baseMapper.listAll(req.to(FolderEntity.class));
        return DocUtil.toTreeRes(folders);
    }

    /**
     * 递归获取传入ID的所有的父文件夹, 并转换成 {@link DocTreeRes}, 结果会包含自己
     *
     * @param ids ID 集合
     */
    public List<DocTreeRes> recursiveToParentTree(List<Long> ids) {
        List<FolderEntity> folders = baseMapper.recursiveToParent(ids);
        return DocUtil.toTreeRes(folders);
    }

    /**
     * 递归获取传入ID的所有的子文件夹, 并转换成 {@link DocTreeRes}, 结果会包含自己
     *
     * @param ids ID 集合
     */
    public List<DocTreeRes> recursiveToChildrenTree(List<Long> ids) {
        List<FolderEntity> folders = baseMapper.recursiveToChildren(ids);
        return DocUtil.toTreeRes(folders);
    }

    /**
     * 根据ID查询
     *
     * @param id 文件夹ID
     */
    public FolderEntity selectById(Long id) {
        return baseMapper.selectOne(new LambdaQueryWrapper<FolderEntity>().eq(FolderEntity::getId, id));
    }

    /**
     * 新增文件夹
     * <p>======================================================
     * <h2>StorePath 存储路径</h2>
     * <p>1. 文件夹的 storePath 不允许为 "null". 默认值为 "/"
     * <p>2. 新增文件夹时, 如果传入的 storePath 等于 "null" 或 "/" 时, 会使用父级文件夹的 storePath,
     * 如果父级没有显式配置存储路径, 则本文件夹使用 "/".
     * <p>3. 由于文件夹在常规操作下是逐层创建的, 所以只会寻找父级, 而不会递归向上直到顶级.
     * <p>======================================================
     * <h2>Type 文件夹类型</h2>
     * <p>1. 文件夹类型: 1:文章文件夹; 2:图片文件夹 {@link FolderTypeEnum}
     * <p>2. 文件夹类型一经创建, 无法修改.
     */
    @Transactional(rollbackFor = Exception.class)
    public FolderEntity insert(FolderEntity folder) {
        // 如果是
        if (StrUtil.isBlank(folder.getStorePath()) || "/".equals(folder.getStorePath())) {
            FolderEntity parentFolder = this.selectById(folder.getPid());
            // 无上级菜单, 使用默认值, 否则使用上级菜单的路径
            if (parentFolder == null) {
                folder.setStorePath(formatStorePath("/"));
            } else {
                folder.setStorePath(parentFolder.getStorePath());
            }
        }
        folder.setId(PrimaryKeyUtil.nextId());
        baseMapper.insert(folder);
        return folder;
    }

    /**
     * 修改文件夹
     * <p>======================================================
     * <h2>StorePath 存储路径</h2>
     * <p>1. 文件夹的 storePath 不允许为 "null". 默认值为 "/"
     * <p>2. 修改文件夹时, 如果传入的 storePath 不为空, 会同时修改所有子文件夹的路径, 但如果子文件夹路径指定了其他路径, 则跳过该子文件夹. 如下:
     * <pre>{@code children.stream().filter(child -> child.getStorePath().equals(oldFolder.getStorePath())) }</pre>
     */
    @Transactional(rollbackFor = Exception.class)
    public Long update(FolderEntity folder) {
        XzException404.throwBy(folder.getId() == null, "ID不得为空");
        XzException400.throwBy(folder.getId().equals(folder.getPid()), "上级文件夹不能是自己");
        // 如果
        if (StrUtil.isNotBlank(folder.getStorePath())) {
            final FolderEntity oldFolder = selectById(folder.getId());
            // 获取所有子文件夹
            List<FolderEntity> children = baseMapper.recursiveToChildren(CollUtil.newArrayList(folder.getId()));
            // 子文件夹中排除自己, 排除存储路径不等于自己的
            children = children.stream()
                    .filter(child -> !child.getId().equals(folder.getId()))
                    .filter(child -> child.getStorePath().equals(oldFolder.getStorePath()))
                    .collect(Collectors.toList());
            if (CollUtil.isNotEmpty(children)) {
                FolderEntity upd = new FolderEntity();
                upd.setStorePath(folder.getStorePath());
                upd.setIds(children.stream().map(FolderEntity::getId).collect(Collectors.toList()));
                baseMapper.updByIds(upd);
            }
        }
        folder.setStorePath(formatStorePath(folder.getStorePath()));
        baseMapper.updById(folder);
        return folder.getId();
    }

    /**
     * 格式化 StorePath
     * <p>1. 在前后增加 /
     * <p>2. 在前后增加 /
     *
     * @param storePath storePath
     */
    private String formatStorePath(String storePath) {
        if (storePath == null) {
            return null;
        }
        if ("".equals(storePath)) {
            storePath = "/";
        }
        if (!StrUtil.endWith(storePath, "/")) {
            storePath = storePath + "/";
        }
        if (!StrUtil.startWith(storePath, "/")) {
            storePath = "/" + storePath;
        }
        while (storePath.contains("//")) {
            storePath = storePath.replace("//", "/");
        }
        return storePath;
    }

    /**
     * 删除文件夹
     * <p>1. 文件夹下有子文件夹时, 无法删除</p>
     * <p>2. 文件夹下有文章时, 无法删除</p>
     *
     * @param folderId 文件夹ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long folderId) {
        // 文件夹下有文件夹, 无法删除
        if (recursiveToChildrenTree(CollUtil.newArrayList(folderId)).stream().anyMatch(d -> !d.getI().equals(folderId))) {
            throw new XzException500("文件夹下有子文件夹, 无法删除, 请先删除子文件夹");
        }

        // 文件夹下有文章, 无法删除
        ArticleQueryReq articleReq = new ArticleQueryReq();
        articleReq.setPids(CollUtil.newArrayList(folderId));
        if (CollUtil.isNotEmpty(articleService.listTree(articleReq))) {
            throw new XzException500("文件夹下有文章, 无法删除, 请先删除下属文章");
        }

        // 文件夹下有图片, 无法删除
        PictureEntity picReq = new PictureEntity();
        picReq.setPid(folderId);
        if (CollUtil.isNotEmpty(pictureService.listAll(picReq))) {
            throw new XzException500("文件夹下有图片, 无法删除, 请先删除下属图片");
        }

        baseMapper.deleteById(folderId);
    }

}