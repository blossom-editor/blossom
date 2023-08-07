package com.blossom.backend.server.article.stat.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 文章字数折线图统计
 *
 * @author xzzz
 */
@Data
public class ArticleLineRes implements Serializable {

    private static final long serialVersionUID = 1L;

    List<String> statDates;
    List<Integer> statValues;
}
