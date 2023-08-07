package com.blossom.backend.server.doc;

import cn.hutool.core.collection.CollUtil;
import com.blossom.backend.server.FolderTypeEnum;
import com.blossom.backend.server.TagEnum;
import com.blossom.backend.server.article.draft.ArticleService;
import com.blossom.backend.server.article.draft.pojo.ArticleQueryReq;
import com.blossom.backend.server.doc.pojo.DocTreeReq;
import com.blossom.backend.server.doc.pojo.DocTreeRes;
import com.blossom.backend.server.folder.FolderService;
import com.blossom.backend.server.folder.pojo.FolderQueryReq;
import com.blossom.backend.server.picture.PictureService;
import com.blossom.backend.server.utils.DocUtil;
import com.blossom.backend.server.utils.PictureUtil;
import com.blossom.common.base.enums.YesNo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文档
 *
 * @author xzzz
 */
@Service
@AllArgsConstructor
public class DocService {
    private final FolderService folderService;
    private final ArticleService articleService;
    private final PictureService pictureService;

    /**
     * 查询文档树状列表
     *
     * @return 树状列表
     */
    public List<DocTreeRes> listTree(DocTreeReq req) {
        List<DocTreeRes> all = new ArrayList<>();

        /* ===============================================================================================
         * 只查询文件夹
         * =============================================================================================== */
        if (req.getOnlyFolder()) {
            FolderQueryReq where = req.to(FolderQueryReq.class);
            List<DocTreeRes> folder = folderService.listTree(where);
            all.addAll(CollUtil.newArrayList(PictureUtil.getDefaultFolder(req.getUserId())));
            all.addAll(folder);
        }
        /* ===============================================================================================
         * 只查询有图片的文件夹, 包含有图片的文章文件夹
         * =============================================================================================== */
        else if (req.getOnlyPicture()) {
            // 有图片的所有文件夹, 包含文章文件夹
            List<Long> pids = pictureService.listDistinctPid(req.getUserId());
            if (CollUtil.isNotEmpty(pids)) {
                List<DocTreeRes> articleTopFolder = folderService.recursiveToParentTree(pids);
                all.addAll(articleTopFolder);
            }
            // 图片文件夹
            FolderQueryReq folder = req.to(FolderQueryReq.class);
            folder.setType(FolderTypeEnum.PICTURE.getType());
            List<DocTreeRes> picFolder = folderService.listTree(folder);

            all.addAll(CollUtil.newArrayList(PictureUtil.getDefaultFolder(req.getUserId())));
            all.addAll(picFolder);
        }
        /* ===============================================================================================
         * 只查询公开的的文章和文章文件夹
         * =============================================================================================== */
        else if (req.getOnlyOpen()) {
            ArticleQueryReq articleWhere = req.to(ArticleQueryReq.class);
            articleWhere.setOpenStatus(YesNo.YES.getValue());
            List<DocTreeRes> articles = articleService.listTree(articleWhere);
            all.addAll(articles);
            if (CollUtil.isNotEmpty(articles)) {
                List<Long> pidList = articles.stream().map(DocTreeRes::getP).collect(Collectors.toList());
                List<DocTreeRes> folders = folderService.recursiveToParentTree(pidList);
                all.addAll(folders);
            }
        }
        /* ===============================================================================================
         * 只查询专题的文章和文件夹
         * =============================================================================================== */
        else if (req.getOnlySubject()) {
            FolderQueryReq folderWhere = req.to(FolderQueryReq.class);
            folderWhere.setTags(TagEnum.subject.name());
            folderWhere.setType(FolderTypeEnum.ARTICLE.getType());
            List<DocTreeRes> subjects = folderService.listTree(folderWhere);
            if (CollUtil.isNotEmpty(subjects)) {
                List<Long> subjectIds = subjects.stream().map(DocTreeRes::getI).collect(Collectors.toList());
                List<DocTreeRes> foldersTop = folderService.recursiveToParentTree(subjectIds);
                List<DocTreeRes> foldersBottom = folderService.recursiveToChildrenTree(subjectIds);
                all.addAll(foldersTop);
                all.addAll(foldersBottom);
            }
            List<DocTreeRes> articles = articleService.listTree(req.to(ArticleQueryReq.class));
            all.addAll(articles);
        }
        /* ===============================================================================================
         * 只查询关注的
         * =============================================================================================== */
        else if (req.getOnlyStar()) {
            ArticleQueryReq articleWhere = req.to(ArticleQueryReq.class);
            articleWhere.setStarStatus(YesNo.YES.getValue());
            List<DocTreeRes> articles = articleService.listTree(articleWhere);
            all.addAll(articles);
            if (CollUtil.isNotEmpty(articles)) {
                List<Long> pidList = articles.stream().map(DocTreeRes::getP).collect(Collectors.toList());
                List<DocTreeRes> folders = folderService.recursiveToParentTree(pidList);
                all.addAll(folders);
            }
        }
        /* ===============================================================================================
         * 默认查询文章文件夹
         * =============================================================================================== */
        else {
            FolderQueryReq folder = req.to(FolderQueryReq.class);
            folder.setType(FolderTypeEnum.ARTICLE.getType());
            List<DocTreeRes> folders = folderService.listTree(folder);
            List<DocTreeRes> articles = articleService.listTree(req.to(ArticleQueryReq.class));
            all.addAll(folders);
            all.addAll(articles);
        }

        return DocUtil.treeWrap(all.stream().distinct().collect(Collectors.toList()));
    }

}
