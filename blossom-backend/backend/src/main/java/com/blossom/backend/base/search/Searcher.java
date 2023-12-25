package com.blossom.backend.base.search;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import com.blossom.common.base.exception.XzException500;
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
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class Searcher {

    private SimpleHTMLFormatter simpleHTMLFormatter;

    private String[] queryField;

    @Autowired
    private SearchProperties searchProperties;


    Searcher() {
        // 构造f高亮显示formatter
        this.simpleHTMLFormatter = new SimpleHTMLFormatter("<B>", "<B>");
        // 构造默认查询域
        this.queryField = new String[2];
        this.queryField[0] = "title";
        this.queryField[1] = "content";
    }

    public List<SearchResult> search(String keyword) {
        List<SearchResult> result = new ArrayList<>();
        if (!StringUtils.hasText(searchProperties.getPath())) {
            throw new XzException500("未配置索引库地址,无法进行全文检索");
        }
        try (Directory directory = FSDirectory.open(new File(searchProperties.getPath()).toPath());
             IndexReader indexReader = DirectoryReader.open(directory);
        ) {
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(queryField, new IKAnalyzer());
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
                    SearchResult searchResult = new SearchResult();
                    searchResult.setId(Convert.toLong(id));
                    if (StringUtils.hasText(title)){
                        String matchTitle = highlighter.getBestFragment(new IKAnalyzer(), "title", title);
                        if (StringUtils.hasText(matchTitle)){
                            searchResult.setTitle(matchTitle);
                        }else {
                            searchResult.setTitle(title);
                        }
                    }else {
                        searchResult.setContent(title);
                    }
                    if (StringUtils.hasText(content)){
                        String matchContent = highlighter.getBestFragment(new IKAnalyzer(), "content", content);
                        if (StringUtils.hasText(matchContent)){
                            searchResult.setContent(matchContent);
                        }else {
                            searchResult.setTitle(content);
                        }
                    }else {
                        searchResult.setContent(content);
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
