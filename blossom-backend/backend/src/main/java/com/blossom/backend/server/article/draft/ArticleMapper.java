package com.blossom.backend.server.article.draft;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blossom.backend.server.article.draft.pojo.ArticleEntity;
import com.blossom.backend.server.article.draft.pojo.ArticleStatRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章
 *
 * @author xzzz
 */
@Mapper
public interface ArticleMapper extends BaseMapper<ArticleEntity> {

    /**
     * 批量查询文章正文
     *
     * @param ids         文章ID
     * @param contentType 正文类型 MARKDOWN/HTML
     */
    List<ArticleEntity> listAllContent(@Param("ids") List<Long> ids, @Param("contentType") String contentType);

    /**
     * 查询全部
     */
    List<ArticleEntity> listAll(ArticleEntity entity);

    /**
     * 获取指定用户的全部文章ID
     * @param userId 用户ID
     */
    List<Long> listIdByUserId(Long userId);

    /**
     * 查询全部需要索引的字段
     */
    List<ArticleEntity> listAllIndexField();

    /**
     * 根据ID修改
     */
    void updById(ArticleEntity entity);

    /**
     * 修改文章内容
     */
    void updContentById(ArticleEntity entity);

    /**
     * 查询某段时间内编辑过内容的文章数
     *
     * @param beginUpdTime 开始修改日期
     * @param endUpdTime   结束修改日期
     */
    ArticleStatRes statUpdArticleCount(@Param("beginUpdTime") String beginUpdTime,
                             @Param("endUpdTime") String endUpdTime,
                             @Param("userId") Long userId);

    /**
     * 修改某段日期内修改的文章数据
     *
     * @param beginUpdTime 开始修改日期
     * @param endUpdTime   结束修改日期
     */
    ArticleStatRes statCount(@Param("beginUpdTime") String beginUpdTime,
                             @Param("endUpdTime") String endUpdTime,
                             @Param("userId") Long userId);

    /**
     * 同步版本号, 将文章的 version 同步到 openVersion, 只有 open_status 为 1 才会修改成功
     *
     * @param articleId 文章ID
     */
    void sync(@Param("articleId") Long articleId);

    /**
     * 递增 UV, PV
     *
     * @param article 文章ID
     * @param pv      pv
     * @param uv      uv
     */
    void uvAndPv(@Param("articleId") Long article, @Param("pv") Integer pv, @Param("uv") Integer uv);

    /**
     * 删除文章
     */
    void delByUserId(@Param("userId") Long userId);
}
