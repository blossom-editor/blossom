package com.blossom.backend.server.picture;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blossom.backend.server.article.reference.ArticleReferenceService;
import com.blossom.backend.server.folder.FolderService;
import com.blossom.backend.server.folder.pojo.FolderEntity;
import com.blossom.backend.server.picture.pojo.PictureEntity;
import com.blossom.backend.server.picture.pojo.PicturePageReq;
import com.blossom.backend.server.picture.pojo.PictureStatRes;
import com.blossom.common.base.exception.XzException400;
import com.blossom.common.base.exception.XzException400HTTP;
import com.blossom.common.base.exception.XzException404;
import com.blossom.common.base.util.PrimaryKeyUtil;
import com.blossom.common.db.aspect.Pages;
import com.blossom.common.iaas.OSManager;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片
 *
 * @author xzzz
 */
@Slf4j
@Service
public class PictureService extends ServiceImpl<PictureMapper, PictureEntity> {
    private FolderService folderService;
    private ArticleReferenceService articleReferenceService;
    private OSManager osManager;

    @Autowired
    public void setFolderService(FolderService folderService) {
        this.folderService = folderService;
    }

    @Autowired
    public void setArticleReferenceService(ArticleReferenceService articleReferenceService) {
        this.articleReferenceService = articleReferenceService;
    }

    @Autowired
    public void setOsManager(OSManager osManager) {
        this.osManager = osManager;
    }

    /**
     * 分页列表
     */
    @Pages
    public PageInfo<PictureEntity> page(PicturePageReq page) {
        return new PageInfo<>(baseMapper.listAll(page.to(PictureEntity.class)));
    }

    /**
     * 全部列表
     */
    public List<PictureEntity> listAll(PictureEntity req) {
        return baseMapper.listAll(req);
    }

    /**
     * 查询所有的 pid, 并去重, 相当于获取所有有图片的文件夹.
     * <pre>{@code select distinct pid from blossom_picture }</pre>
     *
     * @return 如果无结果则返回空集合
     */
    public List<Long> listDistinctPid(Long userId) {
        List<Long> pids = baseMapper.listDistinctPid(userId);
        if (CollUtil.isEmpty(pids)) {
            return new ArrayList<>();
        }
        return pids;
    }

    /**
     * 新增图片
     * <p>
     * 文件路径由组成
     * <ol>
     *  <li>服务器的域名 + nginx 配置的访问路径 + 访问图片的接口(/pic), 例如: <pre>[https://www.domain.com/bl/pic]</pre>这个需要在配置文件中进行配置: project.iaas.blos.domain</li>
     *  <li>文件的默认上传路径, 例如: /home/bl/img, 这个需要在配置文件中进行配置: project.iaas.blos.defaultPath</li>
     *  <li>U + 用户ID, 例如: U1 或 U2...</li>
     *  <li>上传的文件夹配置的图片路径, 例如: /test/. </li>
     * </ol>
     *
     * <p>1. suffix 文件后缀, 传入的 filename 参数优先级更高, 并且会替换
     * <p>2. StorePath 存储地址 <pre>{@code
     * 2.1. 图片的存储地址为 :
     *              os.defaultPath + UID + parentFolder.storePath + filename
     * 2.2. 图片的访问地址为 :
     *  os.domain + os.defaultPath + UID + parentFolder.storePath + filename
     * 2.3. 如果没有上级文件夹, 即 {@param pid} 为空, 则 parentFolder.storePath = "/"
     * }</pre>
     *
     * @param file     文件
     * @param filename 文件名, 文件名不能包含后缀
     * @param pid      上级ID
     */
    @Transactional(rollbackFor = Exception.class)
    public PictureEntity insert(MultipartFile file, String filename, Long pid, Long userId) {
        PictureEntity pic = new PictureEntity();
        pic.setUserId(userId);
        pic.setId(PrimaryKeyUtil.nextId());
        // 文件原名
        pic.setSourceName(file.getOriginalFilename());
        // 文件后缀, 后缀无法修改
        pic.setSuffix(FileUtil.getSuffix(pic.getSourceName()));
        // 文件名以传入文件名为优先
        if (StrUtil.isBlank(filename)) {
            pic.setName(file.getOriginalFilename());
        } else {
            pic.setName(filename);
            if (StrUtil.isBlank(FileUtil.getSuffix(filename))) {
                pic.setName(filename + "." + pic.getSuffix());
            }
        }
        pic.setSize(file.getSize());

        final String domain = osManager.getDomain();
        final String rootPath = osManager.getDefaultPath();
        final String uid = "/U" + userId;
        final String pname = "/" + pic.getName();

        // 上传文件夹为空, 则上传至默认文件夹, 默认文件夹是系统提供的无法删除的文件夹, ID为 userId * -1
        if (pid == null || pid <= 0) {
            pic.setPid(userId * -1);
            pic.setPathName(rootPath + uid + pname);
        } else {
            FolderEntity folder = folderService.selectById(pid);
            XzException400HTTP.throwBy(ObjUtil.isNull(folder), "上传文件夹[" + pid + "]不存在, 请核对后再上传");
            final String storePath = StrUtil.isBlank(folder.getStorePath()) ? "/" : folder.getStorePath();
            pic.setPid(pid);
            pic.setPathName(rootPath + uid + storePath + pname);
        }

        pic.setPathName(pic.getPathName().replaceAll("//", "/"));
        pic.setUrl(domain + pic.getPathName());

        System.out.println(pic.getPathName());
        System.out.println(pic.getUrl());

        if (baseMapper.exists(new LambdaQueryWrapper<PictureEntity>().eq(PictureEntity::getPathName, pic.getPathName()))) {
            throw new XzException400HTTP("图片[" + pic.getPathName() + "]已存在, 请重命名文件或选择其他路径!");
        }

        baseMapper.insert(pic);
        return pic;
    }


    /**
     * 修改
     */
    @Transactional(rollbackFor = Exception.class)
    public Long update(PictureEntity req) {
        XzException404.throwBy(req.getId() == null, "ID不得为空");
        baseMapper.updById(req);
        return req.getId();
    }

    /**
     * 删除
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        PictureEntity pic = baseMapper.selectById(id);
        if (pic == null) {
            return;
        }
        XzException400.throwBy(articleReferenceService.check(pic.getUrl()), "尚有文章正在引用该图片, 请先将文章中的图片引用删除后, 再删除图片!");
        baseMapper.deleteById(id);
        osManager.delete(pic.getPathName());
    }

    public PictureStatRes stat(Long userId, Long pid) {
        return baseMapper.stat(userId, pid);
    }
}