package com.blossom.backend.server.doc;

import cn.hutool.core.collection.CollUtil;
import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.server.article.TagEnum;
import com.blossom.backend.server.article.draft.ArticleMapper;
import com.blossom.backend.server.article.draft.ArticleService;
import com.blossom.backend.server.article.draft.pojo.ArticleEntity;
import com.blossom.backend.server.doc.pojo.DocTreeReq;
import com.blossom.backend.server.doc.pojo.DocTreeRes;
import com.blossom.backend.server.doc.pojo.DocTreeUpdSortReq;
import com.blossom.backend.server.folder.FolderMapper;
import com.blossom.backend.server.folder.FolderService;
import com.blossom.backend.server.folder.FolderTypeEnum;
import com.blossom.backend.server.folder.pojo.FolderEntity;
import com.blossom.backend.server.picture.PictureService;
import com.blossom.backend.server.utils.DocUtil;
import com.blossom.backend.server.utils.PictureUtil;
import com.blossom.common.base.enums.YesNo;
import com.blossom.common.base.util.SortUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 文档
 *
 * @author xzzz
 */
@Service
public class DocService {
    private ArticleService articleService;
    private PictureService pictureService;
    private FolderService folderService;
    @Autowired
    private DocMapper baseMapper;
    @Autowired
    private FolderMapper folderMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private DocSortChecker docSortChecker;

    @Autowired
    public void setArticleService(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Autowired
    public void setFolderService(FolderService folderService) {
        this.folderService = folderService;
    }

    @Autowired
    public void setPictureService(PictureService pictureService) {
        this.pictureService = pictureService;
    }

    /**
     * 查询文档树状列表
     *
     * @return 树状列表
     */
    public List<DocTreeRes> listTree(DocTreeReq req) {
        List<DocTreeRes> all = new ArrayList<>();
        boolean priorityType = false;

        /* ===============================================================================================
         * 只查询文件夹
         * =============================================================================================== */
        if (req.getOnlyFolder()) {
            FolderEntity where = req.to(FolderEntity.class);
            List<FolderEntity> folder = folderMapper.listAll(where);
            all.addAll(CollUtil.newArrayList(PictureUtil.getDefaultFolder(req.getUserId())));
            all.addAll(DocUtil.toDocTreesByFolders(folder));
            priorityType = true;
        }
        /* ===============================================================================================
         * 只查询有图片的文件夹, 包含有图片的文章文件夹
         * =============================================================================================== */
        else if (req.getOnlyPicture()) {
            // 1. 所有图片文件夹
            FolderEntity where = req.to(FolderEntity.class);
            where.setType(FolderTypeEnum.PICTURE.getType());
            List<FolderEntity> picFolder = folderMapper.listAll(where);
            all.addAll(DocUtil.toDocTreesByFolders(picFolder));

            // 2. 有图片的图片或文章文件夹
            List<Long> pids = pictureService.listDistinctPid(req.getUserId());
            if (CollUtil.isNotEmpty(pids)) {
                List<Long> picFolderIds = picFolder.stream().map(FolderEntity::getId).collect(Collectors.toList());
                // 剔除掉图片文件夹
                List<Long> articleFolderIds = pids.stream().filter(i -> !picFolderIds.contains(i)).collect(Collectors.toList());
                if(CollUtil.isNotEmpty(articleFolderIds)) {
                    List<FolderEntity> articleFolder = folderMapper.recursiveToParent(articleFolderIds);
                    all.addAll(DocUtil.toDocTreesByFolders(articleFolder));
                }
            }

            Optional<DocTreeRes> min = all.stream().min((f1, f2) -> SortUtil.intSort.compare(f1.getS(), f2.getS()));
            DocTreeRes defaultFolder = PictureUtil.getDefaultFolder(req.getUserId());
            min.ifPresent(docTreeRes -> defaultFolder.setS(docTreeRes.getS() - 1));

            // 3. 默认的图片文件夹
            all.addAll(CollUtil.newArrayList(defaultFolder));
            priorityType = true;
        }
        /* ===============================================================================================
         * 只查询公开的的文章和文章文件夹
         * =============================================================================================== */
        else if (req.getOnlyOpen()) {

            ArticleEntity where = req.to(ArticleEntity.class);
            where.setOpenStatus(YesNo.YES.getValue());
            List<ArticleEntity> articles = articleMapper.listAll(where);
            all.addAll(DocUtil.toDocTreesByArticles(articles));

            if (CollUtil.isNotEmpty(articles)) {
                List<Long> pidList = articles.stream().map(ArticleEntity::getPid).collect(Collectors.toList());
                List<FolderEntity> folders = folderMapper.recursiveToParent(pidList);
                all.addAll(DocUtil.toDocTreesByFolders(folders));
            }
        }
        /* ===============================================================================================
         * 只查询专题的文章和文件夹
         * =============================================================================================== */
        else if (req.getOnlySubject()) {

            FolderEntity where = req.to(FolderEntity.class);
            where.setTags(TagEnum.subject.name());
            where.setType(FolderTypeEnum.ARTICLE.getType());
            List<FolderEntity> subjects = folderMapper.listAll(where);

            if (CollUtil.isNotEmpty(subjects)) {
                List<Long> subjectIds = subjects.stream().map(FolderEntity::getId).collect(Collectors.toList());
                List<FolderEntity> foldersTop = folderMapper.recursiveToParent(subjectIds);
                List<FolderEntity> foldersBottom = folderMapper.recursiveToChildren(subjectIds);
                all.addAll(DocUtil.toDocTreesByFolders(foldersTop));
                all.addAll(DocUtil.toDocTreesByFolders(foldersBottom));
            }

            List<ArticleEntity> articles = articleMapper.listAll(req.to(ArticleEntity.class));
            all.addAll(DocUtil.toDocTreesByArticles(articles));
        }
        /* ===============================================================================================
         * 只查询关注的
         * =============================================================================================== */
        else if (req.getOnlyStar()) {

            ArticleEntity where = req.to(ArticleEntity.class);
            where.setStarStatus(YesNo.YES.getValue());
            List<ArticleEntity> articles = articleMapper.listAll(where);
            all.addAll(DocUtil.toDocTreesByArticles(articles));

            if (CollUtil.isNotEmpty(articles)) {
                List<Long> pidList = articles.stream().map(ArticleEntity::getPid).collect(Collectors.toList());
                List<FolderEntity> folders = folderMapper.recursiveToParent(pidList);
                all.addAll(DocUtil.toDocTreesByFolders(folders));
            }
        }
        /* ===============================================================================================
         * 只查询指定文章
         * =============================================================================================== */
        else if (req.getArticleId() != null && req.getArticleId() > 0) {

            ArticleEntity where = req.to(ArticleEntity.class);
            where.setId(req.getArticleId());
            List<ArticleEntity> articles = articleMapper.listAll(where);
            all.addAll(DocUtil.toDocTreesByArticles(articles));

            if (CollUtil.isNotEmpty(articles)) {
                List<Long> pidList = articles.stream().map(ArticleEntity::getPid).collect(Collectors.toList());
                List<FolderEntity> folders = folderMapper.recursiveToParent(pidList);
                all.addAll(DocUtil.toDocTreesByFolders(folders));
            }
        }
        /* ===============================================================================================
         * 默认查询文章文件夹
         * =============================================================================================== */
        else {
            FolderEntity folder = req.to(FolderEntity.class);
            folder.setType(FolderTypeEnum.ARTICLE.getType());

            List<FolderEntity> folders = folderMapper.listAll(folder);
            all.addAll(DocUtil.toDocTreesByFolders(folders));

            List<ArticleEntity> articles = articleMapper.listAll(req.to(ArticleEntity.class));
            all.addAll(DocUtil.toDocTreesByArticles(articles));
        }

        return DocUtil.treeWrap(all.stream().distinct().collect(Collectors.toList()), priorityType);
    }

    /**
     * 根据PID获取最大排序
     *
     * @param pid pid
     * @return 最大排序
     * @since 1.10.0
     */
    public int selectMaxSortByPid(Long pid, Long userId, FolderTypeEnum type) {
        return baseMapper.selectMaxSortByPid(pid, userId, type.getType());
    }

    /**
     * 修改排序
     *
     * @param docs 需要修改的文档
     * @since 1.14.0
     */
    @Transactional(rollbackFor = Exception.class)
    public void updSort(List<DocTreeUpdSortReq.Doc> docs, Long userId, FolderTypeEnum folderType) {
        // 提取所有需要修改的文档的父文档
        List<Long> pids = docs.stream().map(DocTreeUpdSortReq.Doc::getP).collect(Collectors.toList());

        List<ArticleEntity> articles = new ArrayList<>();
        List<FolderEntity> folders = new ArrayList<>();

        // 一次拖拽所修改的文档可能包含文章和文件夹, 需要归类和转为 Entity
        for (DocTreeUpdSortReq.Doc doc : docs) {
            if (DocTypeEnum.A.getType().equals(doc.getTy())) {
                ArticleEntity a = new ArticleEntity();
                a.setId(doc.getI());
                a.setPid(doc.getP());
                a.setName(doc.getN());
                a.setSort(doc.getS());
                articles.add(a);
            }
            if (DocTypeEnum.FA.getType().equals(doc.getTy())) {
                FolderEntity f = new FolderEntity();
                f.setId(doc.getI());
                f.setPid(doc.getP());
                f.setName(doc.getN());
                f.setSort(doc.getS());
                folders.add(f);
            }
        }

        docSortChecker.checkUnique(pids, folders, articles, folderType, userId);

        for (DocTreeUpdSortReq.Doc tree : docs) {
            if (DocTypeEnum.FA.getType().equals(tree.getTy()) || DocTypeEnum.FP.getType().equals(tree.getTy())) {
                FolderEntity f = new FolderEntity();
                f.setId(tree.getI());
                f.setPid(tree.getP());
                f.setSort(tree.getS());
                f.setUserId(AuthContext.getUserId());
                f.setStorePath(tree.getSp());
                folderService.update(f);
            } else if (DocTypeEnum.A.getType().equals(tree.getTy())) {
                ArticleEntity a = new ArticleEntity();
                a.setId(tree.getI());
                a.setPid(tree.getP());
                a.setSort(tree.getS());
                a.setUserId(AuthContext.getUserId());
                articleService.update(a);
            }
        }
    }

}
