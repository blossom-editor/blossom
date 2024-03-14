package com.blossom.backend.server.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.blossom.backend.server.article.draft.pojo.ArticleEntity;
import com.blossom.backend.server.doc.DocTypeEnum;
import com.blossom.backend.server.doc.pojo.DocTreeRes;
import com.blossom.backend.server.folder.pojo.FolderEntity;
import com.blossom.common.base.enums.YesNo;
import com.blossom.common.base.util.SortUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文档工具类
 *
 * @author xzzz
 */
public class DocUtil {

    /**
     * 即一次菜单的父ID
     */
    private static final Long ROOT_FOLDER_ID = 0L;

    /**
     * 将菜单列表构造成树状
     *
     * @param list         菜单列表
     * @param priorityType 是否优先按类型排序
     * @return 树状菜单对象
     */
    public static List<DocTreeRes> treeWrap(List<DocTreeRes> list, boolean priorityType) {
        // 将原始列表进行分组, 并排序每个分组的列表
        Map<Long, List<DocTreeRes>> pidMapping = list.stream().collect(
                Collectors.groupingBy(DocTreeRes::getP, HashMap::new,
                Collectors.collectingAndThen(Collectors.toList(),
                item -> item.stream().sorted(Comparator.comparingInt(DocTreeRes::getS)).collect(Collectors.toList()))));
        // 免递归方式赋值子菜单
        list.parallelStream().forEach(item -> {
            if(item.getTy().equals(DocTypeEnum.A.getType())) {
                return;
            }
            if (!CollUtil.isEmpty(pidMapping.get(item.getI()))){
                item.setChildren(pidMapping.get(item.getI()));
            }
        });
        // 查询根菜单
        List<DocTreeRes> rootLevel = list.stream()
                .filter(p -> p.getP().equals(ROOT_FOLDER_ID))
                .sorted(Comparator.comparing(DocTreeRes::getS))
                .sorted((d1, d2) -> {
                    if (priorityType) {
                        if (d2.getTy().equals(d1.getTy())) {
                            return d1.getS() - d2.getS();
                        }
                        return d2.getTy() - d1.getTy();
                    }
                    return SortUtil.intSort.compare(d1.getS(), d2.getS());
                })
                .collect(Collectors.toList());

        return rootLevel;
    }

    private static void setChild(DocTreeRes p, List<DocTreeRes> allFolder) {
        //将集合中自己去除
        List<DocTreeRes> child = allFolder.parallelStream()
                .filter(a -> a.getP().equals(p.getI()))
                .sorted(Comparator.comparing(DocTreeRes::getS))
                .collect(Collectors.toList());
        //如果集合不为空
        if (!CollUtil.isEmpty(child)) {
            p.setChildren(child);
            //递归设置子元素，多级菜单支持
            child.parallelStream().forEach(c -> setChild(c, allFolder));
        }
    }

    /**
     * 字符串标签转数组
     *
     * @param tags 便签字符串
     * @return 数组字符串
     */
    public static List<String> toTagList(String tags) {
        if (StrUtil.isBlank(tags)) {
            return new ArrayList<>();
        }
        return StrUtil.split(tags, ",");
    }

    /**
     * 字符串标签转数组
     *
     * @param tags 便签字符串
     * @return 数组字符串
     */
    public static String toTagStr(List<String> tags) {
        if (CollUtil.isNotEmpty(tags)) {
            return CollUtil.join(tags, ",");
        }
        return "";
    }

    /**
     * 文章转 docTree
     *
     * @param article 文章
     * @return docTree
     */
    public static DocTreeRes toDocTree(ArticleEntity article) {
        DocTreeRes tree = new DocTreeRes();
        tree.setI(article.getId());
        tree.setP(article.getPid());
        tree.setO(article.getOpenStatus());
        tree.setS(article.getSort());
        tree.setN(article.getName());
        tree.setStar(article.getStarStatus());
        tree.setIcon(article.getIcon());
        tree.setTy(DocTypeEnum.A.getType());

        // 判断文章的版本与公开版本是否有差异
        if (article.getOpenStatus().equals(YesNo.YES.getValue()) && article.getVersion() > article.getOpenVersion()) {
            tree.setVd(YesNo.YES.getValue());
        }

        if (StrUtil.isBlank(article.getTags())) {
            tree.setT(new ArrayList<>());
        } else {
            tree.setT(DocUtil.toTagList(article.getTags()));
        }
        return tree;
    }

    /**
     * 文件夹转 docTree
     *
     * @param folder 文件夹
     * @return docTree
     */
    public static DocTreeRes toDocTree(FolderEntity folder) {
        DocTreeRes tree = new DocTreeRes();
        tree.setI(folder.getId());
        tree.setP(folder.getPid());
        tree.setO(folder.getOpenStatus());
        tree.setS(folder.getSort());
        tree.setN(folder.getName());
        tree.setSp(folder.getStorePath());
        tree.setStar(0);
        tree.setTy(folder.getType());
        tree.setIcon(folder.getIcon());
        if (StrUtil.isBlank(folder.getTags())) {
            tree.setT(new ArrayList<>());
        } else {
            tree.setT(DocUtil.toTagList(folder.getTags()));
        }
        return tree;
    }

    /**
     * 文件夹集合转 docTree集合
     *
     * @param folders 文件夹集合
     * @return docTree
     */
    public static List<DocTreeRes> toTreeRes(List<FolderEntity> folders) {
        List<DocTreeRes> folderTrees = new ArrayList(folders.size());
        for (FolderEntity folder : folders) {
            folderTrees.add(toDocTree(folder));
        }
        return folderTrees;
    }

    /**
     * 从文件夹集合 {@param folders} 中获取 {@param id} 的子文件夹集合, 并返回这些文件夹的ID
     *
     * @param id      顶级父ID
     * @param folders 文件夹集合
     */
    public static List<Long> getChildrenIds(Long id, List<FolderEntity> folders) {
        List<FolderEntity> result = new ArrayList<>();
        getChildrenIds(result, folders, id);
        List<Long> ids = result.stream().map(FolderEntity::getId).collect(Collectors.toList());
        ids.add(id);
        return ids;
    }

    /**
     * 递归获取子ID
     */
    private static void getChildrenIds(List<FolderEntity> result, List<FolderEntity> folders, long id) {
        for (FolderEntity org : folders) {
            if (org.getPid() != null) {
                //遍历出父id等于参数的id，add进子节点集合
                if (org.getPid() == id) {
                    //递归遍历下一级
                    getChildrenIds(result, folders, org.getId());
                    result.add(org);
                }
            }
        }
    }

    public static void main(String[] args) {
        FolderEntity a = new FolderEntity();
        a.setId(1L);
        a.setPid(0L);
        FolderEntity b = new FolderEntity();
        b.setId(2L);
        b.setPid(1L);
        FolderEntity c = new FolderEntity();
        c.setId(3L);
        c.setPid(2L);
        FolderEntity d = new FolderEntity();
        d.setId(4L);
        d.setPid(2L);
        List<FolderEntity> folders = new ArrayList<>();
        folders.add(a);
        folders.add(b);
        folders.add(c);
        folders.add(d);

    }
}
