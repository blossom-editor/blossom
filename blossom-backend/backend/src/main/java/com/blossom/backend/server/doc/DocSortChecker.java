package com.blossom.backend.server.doc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.blossom.backend.server.article.draft.ArticleMapper;
import com.blossom.backend.server.article.draft.pojo.ArticleEntity;
import com.blossom.backend.server.doc.pojo.DocTreeRes;
import com.blossom.backend.server.folder.FolderMapper;
import com.blossom.backend.server.folder.FolderTypeEnum;
import com.blossom.backend.server.folder.pojo.FolderEntity;
import com.blossom.backend.server.utils.DocUtil;
import com.blossom.common.base.exception.XzException400;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DocSortChecker {

    @Autowired
    private FolderMapper folderMapper;

    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 检查 pid 下是否有重复的文档排序
     *
     * @param pids  pid
     * @param newFs 新文件夹
     * @param newAs 新文章
     * @return
     */
    public boolean checkUnique(List<Long> pids,
                               List<FolderEntity> newFs,
                               List<ArticleEntity> newAs,
                               FolderTypeEnum folderType,
                               Long userId) {

        Map<Long, FolderEntity> newFMap = CollUtil.isEmpty(newFs) ? new HashMap<>() : newFs.stream().collect(Collectors.toMap(FolderEntity::getId, doc -> doc));
        Map<Long, ArticleEntity> newAMap = CollUtil.isEmpty(newAs) ? new HashMap<>() : newAs.stream().collect(Collectors.toMap(ArticleEntity::getId, doc -> doc));

        List<DocTreeRes> allDoc = new ArrayList<>();

        // 获取文件夹排序
        FolderEntity fByPid = new FolderEntity();
        fByPid.setPids(pids);
        fByPid.setType(folderType.getType());
        fByPid.setUserId(userId);
        List<FolderEntity> folders = folderMapper.listAll(fByPid);

        if (CollUtil.isNotEmpty(folders)) {
            for (FolderEntity f : folders) {
                FolderEntity newF = newFMap.get(f.getId());
                if (newF != null) {
                    f.setPid(newF.getPid());
                    f.setSort(newF.getSort());
                    newFMap.remove(f.getId());
                }
                allDoc.add(DocUtil.toDocTree(f));
            }
        }

        if (MapUtil.isNotEmpty(newFMap)) {
            for (FolderEntity f : newFMap.values()) {
                allDoc.add(DocUtil.toDocTree(f));
            }
        }

        // 只有处理文章排序时, 才需要获取文章排序
        if(FolderTypeEnum.ARTICLE.equals(folderType)) {
            ArticleEntity aByPid = new ArticleEntity();
            aByPid.setPids(pids);
            aByPid.setUserId(userId);
            List<ArticleEntity> articles = articleMapper.listAll(aByPid);
            if (CollUtil.isNotEmpty(articles)) {
                for (ArticleEntity a : articles) {
                    ArticleEntity newA = newAMap.get(a.getId());
                    if (newA != null) {
                        a.setPid(newA.getPid());
                        a.setSort(newA.getSort());
                        newAMap.remove(a.getId());
                    }
                    allDoc.add(DocUtil.toDocTree(a));
                }
            }

            if (MapUtil.isNotEmpty(newAMap)) {
                for (ArticleEntity a : newAMap.values()) {
                    allDoc.add(DocUtil.toDocTree(a));
                }
            }
        }


        // 按 pid 分组, 依次校验各组下数据
        Map<Long, List<DocTreeRes>> map = allDoc.stream().collect(Collectors.groupingBy(DocTreeRes::getP));

        map.forEach((pid, list) -> {
            // 获取不重复的排序
            long distinct = list.stream().map(DocTreeRes::getS).distinct().count();
            // 不重复的个数小于总数则有重复
            if (distinct != list.size()) {
                throw new XzException400("排序重复");
            }
        });
        return true;
    }
}
