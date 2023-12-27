package com.blossom.backend.base.search;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import com.blossom.common.base.exception.XzException500;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
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
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Searcher {

    private SimpleHTMLFormatter simpleHTMLFormatter;

    private String[] queryField;

    private Map<String, Float> boostsMap;

    @Autowired
    private SearchProperties searchProperties;


    Searcher() {
        // 构造f高亮显示formatter
        this.simpleHTMLFormatter = new SimpleHTMLFormatter("<B>", "<B>");
        // 构造默认查询域
        this.queryField = new String[3];
        this.queryField[0] = "content";
        this.queryField[1] = "title";
        this.queryField[2] = "tags";
        // 构造权重配置
        this.boostsMap = new HashMap<>();
        boostsMap.put("title", 2F);
        boostsMap.put("tags", 2F);
        boostsMap.put("content", 1F);
    }


    /**
     * 进行索引查询， 传入关键词以及用户id
     * @param keyword 关键词
     * @param userId 用户id
     * @return 查询结果
     */
    public List<SearchResult> search(String keyword, Long userId) {
        List<SearchResult> result = new ArrayList<>();
        if (!StringUtils.hasText(searchProperties.getPath())) {
            throw new XzException500("未配置索引库地址,无法进行全文检索");
        }

        if (userId == null){
            throw new IllegalArgumentException("未获取到用户信息");
        }

        try (Directory directory = FSDirectory.open(searchProperties.getUserIndexDirectory(userId));
             IndexReader indexReader = DirectoryReader.open(directory);
        ) {
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(queryField, new StandardAnalyzer(),boostsMap);
            Query query = multiFieldQueryParser.parse(keyword);
            TopDocs topDocs = indexSearcher.search(query, 10);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            if (!ArrayUtil.isEmpty(scoreDocs)) {
                Highlighter highlighter = new Highlighter(simpleHTMLFormatter, new QueryScorer(query));
                highlighter.setTextFragmenter(new SimpleFragmenter(20));
                for (ScoreDoc doc : scoreDocs) {
                    Document document = indexSearcher.doc(doc.doc);
                    String id = document.get("id");
                    String title = document.get("title");
                    String content = document.get("content");
                    String tags = document.get("tags");
                    SearchResult searchResult = new SearchResult();
                    searchResult.setId(Convert.toLong(id));
                    if (StringUtils.hasText(title)){
                        String matchTitle = highlighter.getBestFragment(new StandardAnalyzer(), "title", title);
                        if (StringUtils.hasText(matchTitle)){
                            searchResult.setTitle(matchTitle);
                        }else {
                            searchResult.setTitle(title);
                        }
                    }else {
                        searchResult.setContent(title);
                    }
                    if (StringUtils.hasText(content)){
                        String matchContent = highlighter.getBestFragment(new StandardAnalyzer(), "content", content);
                        if (StringUtils.hasText(matchContent)){
                            searchResult.setContent(matchContent);
                        }else {
                            searchResult.setContent(content);
                        }
                    }else {
                        searchResult.setContent(content);
                    }

                    if (StringUtils.hasText(tags)){
                        String matchTags= highlighter.getBestFragment(new StandardAnalyzer(), "tags", tags);
                        if (StringUtils.hasText(matchTags)){
                            searchResult.setTags(matchTags);
                        }else {
                            searchResult.setTags(tags);
                        }
                    }else {
                        searchResult.setTags(tags);
                    }
                    result.add(searchResult);
                }
            }


        } catch (Exception e) {
            throw new XzException500("索引查询异常");
        }
        return result;

    }
}
