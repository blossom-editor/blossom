package com.blossom.backend.base.search;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.blossom.backend.server.utils.DocUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 搜索类
 */
@Slf4j
@Component
public class Searcher {

    private final SimpleHTMLFormatter debugFmt = new SimpleHTMLFormatter("【", "】");

    private final String[] queryField;

    private final Map<String, Float> boostsMap;

    @Autowired
    private SearchProperties searchProperties;

    Searcher() {
        // 构造默认查询域
        this.queryField = new String[3];
        this.queryField[0] = "markdown";
        this.queryField[1] = "name";
        this.queryField[2] = "tags";
        // 构造权重配置
        this.boostsMap = new HashMap<>();
        this.boostsMap.put("name", 3F);
        this.boostsMap.put("tags", 3F);
        this.boostsMap.put("markdown", 1F);
    }


    /**
     * 搜索
     *
     * @param keyword  关键词
     * @param userId   用户id
     * @param hlColor  高亮颜色
     * @param operator 是否全部匹配
     * @param debug    是否DEBUG, 为 true 时高亮前后缀为【】
     * @return 查询结果
     */
    public SearchRes search(String keyword, Long userId, String hlColor, boolean operator, boolean debug) {
        SearchRes result = new SearchRes();
        result.setHits(new ArrayList<>());

        if (StrUtil.isBlank(keyword)) {
            result.setTotalHit(0L);
            return result;
        }

        if (userId == null) {
            throw new IllegalArgumentException("未获取到用户信息");
        }

        try (Directory directory = FSDirectory.open(searchProperties.getUserIndexDirectory(userId));
             IndexReader indexReader = DirectoryReader.open(directory)) {
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(queryField, new StandardAnalyzer(), boostsMap);
            if (operator) {
                multiFieldQueryParser.setDefaultOperator(QueryParser.Operator.AND);
            }

            Query query = multiFieldQueryParser.parse(keyword);

            TopDocs topDocs = indexSearcher.search(query, 20);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            result.setTotalHit(topDocs.totalHits.value);
            if (!ArrayUtil.isEmpty(scoreDocs)) {
                Highlighter highlighter;
                if (debug) {
                    highlighter = new Highlighter(debugFmt, new QueryScorer(query));
                } else {
                    highlighter = new Highlighter(new SimpleHTMLFormatter("<span style=\"background-color:" + hlColor + ";color:#ffffff\">", "</span>"), new QueryScorer(query));
                }
                highlighter.setTextFragmenter(new SimpleFragmenter(100));

                for (ScoreDoc doc : scoreDocs) {
                    Document document = indexSearcher.doc(doc.doc);
                    String id = document.get("id");
                    String name = document.get("name");
                    String markdown = document.get("markdown");
                    String tags = document.get("tags");

                    SearchRes.Hit hit = new SearchRes.Hit();
                    hit.setScore(doc.score);
                    hit.setId(Convert.toLong(id));
                    if (StrUtil.isNotBlank(name)) {
                        hit.setOriginName(name);
                        String hlName = highlighter.getBestFragment(new StandardAnalyzer(), "name", name);
                        if (StrUtil.isNotBlank(hlName)) {
                            hit.setName(hlName);
                        } else {
                            hit.setName(name);
                        }
                    } else {
                        hit.setName("");
                        hit.setOriginName("");
                    }

                    // 无内容或无高亮匹配时, 返回 ""
                    if (StrUtil.isNotBlank(markdown)) {
                        String hlMarkdown = highlighter.getBestFragment(new StandardAnalyzer(), "markdown", markdown);
                        if (StrUtil.isNotBlank(hlMarkdown)) {
                            hit.setMarkdown(fmtMarkdown(hlMarkdown));
                        } else {
                            hit.setMarkdown("");
                        }
                    } else {
                        hit.setMarkdown("");
                    }

                    if (StrUtil.isNotBlank(tags)) {
                        String hlTags = highlighter.getBestFragment(new StandardAnalyzer(), "tags", tags);
                        if (StrUtil.isNotBlank(hlTags)) {
                            hit.setTags(DocUtil.toTagList(hlTags));
                        } else {
                            hit.setTags(DocUtil.toTagList(tags));
                        }
                    } else {
                        hit.setTags(DocUtil.toTagList(tags));
                    }

                    result.getHits().add(hit);
                }
            }
        } catch (Exception e) {
            log.error("搜索异常: {}", e.getMessage());
            return result;
        }
        return result;
    }

    /**
     * 将正文中的换行转换成 html 内容
     *
     * @param markdown markdown 正文内容
     */
    public String fmtMarkdown(String markdown) {
        return markdown.replaceAll("(\r\n|\n\r|\r|\n)", "<br />");
    }
}
