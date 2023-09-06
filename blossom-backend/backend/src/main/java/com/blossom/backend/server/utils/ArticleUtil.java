package com.blossom.backend.server.utils;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.blossom.backend.base.user.pojo.UserEntity;
import com.blossom.backend.server.article.draft.pojo.ArticleEntity;
import com.blossom.common.base.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * 文章工具类
 *
 * @author xzzz
 */
@Slf4j
public class ArticleUtil {

    /**
     * 统计文章字数, 只统计中文, 英文
     *
     * @param markdown markdown 内容
     * @return 字数
     */
    public static Integer statWords(String markdown) {
        if (StrUtil.isBlank(markdown)) {
            return 0;
        }
        String[] lines = markdown.split("\n");
        if (ArrayUtil.isEmpty(lines)) {
            return 0;
        }
        int ch = 0;// 中文字符
        int en = 0;// 英文字符
        int space = 0;// 空格
        int number = 0;// 数字
        int other = 0;// 其他字符

        // 是否多行代码块内, 多行代码块内的内容不计算在内.
        boolean isPreTag = false;
        for (String line : lines) {
            if (line.length() == 0) {
                continue;
            }
            if (!isPreTag && line.startsWith("```")) {
                isPreTag = true;
            } else if (isPreTag && line.startsWith("```")) {
                isPreTag = false;
            }

            if (isPreTag) {
                continue;
            }

            for (int i = 0; i < line.length(); i++) {
                char tmp = line.charAt(i);
                if ((tmp >= 'A' && tmp <= 'Z') || (tmp >= 'a' && tmp <= 'z')) {
                    en++;
                } else if ((tmp >= '0') && (tmp <= '9')) {
                    number++;
                } else if (tmp == ' ') {
                    space++;
                } else if (isChinese(tmp)) {
                    ch++;
                } else {
                    other++;
                }
            }
        }
//        System.out.println("中文: " + ch + "个");
//        System.out.println("英文: " + en + "个");
//        System.out.println("数字: " + number + "");
//        System.out.println("空格: " + space + "个");
//        System.out.println("其他: " + other + "个");
        return ch + en + number;
    }

    /**
     * 字符是否中文
     *
     * @param ch 字符
     * @return 是否中文
     */
    private static boolean isChinese(char ch) {
        //获取此字符的UniCodeBlock
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(ch);
        // GENERAL_PUNCTUATION 判断中文的“号
        // CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;

    }


    private static final String prefix = "\n" +
            "<body><div class=\"header\">\n" +
            "  <div class=\"copyright\">本文作者：{BLOSSOM_EXPORT_HTML_AUTHOR}。著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。</div><a\n" +
            "  href=\"https://github.com/blossom-editor/blossom\" target=\"_blank\"><span>Export by Blossom</span><svg\n" +
            "  xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-github\"\n" +
            "  viewBox=\"0 0 16 16\">\n" +
            "  <path\n" +
            "    d=\"M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.012 8.012 0 0 0 16 8c0-4.42-3.58-8-8-8z\" />\n" +
            "</svg></a>\n" +
            "</div><div class=\"content \">\n" +
            "  <div class=\"toc\" id=\"blossom-toc\">\n" +
            "    <div style=\"font-size: 15px;color:#727272;padding:10px 0\">《{BLOSSOM_EXPORT_HTML_ARTICLE_NAME}》</div>\n" +
            "    <div style=\"font-size: 20px;color:#727272;border-bottom:2px solid #eaeaea;padding-bottom: 10px\">目录</div>\n" +
            "  </div><div class=\"main bl-preview\" id=\"blossom-view\">";

    private static final String suffix = "</div></div></body></html>";

    private static String htmlTag;

    static {
        Resource resource = new ClassPathResource("exportTemplate.html");
        try (InputStream is = resource.getInputStream()) {
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            htmlTag = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将文章转换为 html 格式
     *
     * @param article 文章
     * @param user    用户, 用户获取作者
     * @return html 内容
     */
    public static String toHtml(ArticleEntity article, UserEntity user) {
        return htmlTag + prefix
                // 替换作者, 文章名称
                .replaceAll("\\{BLOSSOM_EXPORT_HTML_AUTHOR}", user.getNickName())
                .replaceAll("\\{BLOSSOM_EXPORT_HTML_ARTICLE_NAME}", article.getName()) +
                article.getHtml() + suffix;
    }


    private static void genHeatmap() {
        Date begin = DateUtils.parse("2023-04-01", DateUtils.PATTERN_YYYYMMDD);
        Date end = DateUtils.parse("2023-06-30", DateUtils.PATTERN_YYYYMMDD);
        for (; DateUtils.compare(begin, end) <= 0; begin = DateUtils.offsetDay(begin, 1)) {
            String dt = DateUtils.format(begin, DateUtils.PATTERN_YYYYMMDD);
            int value = RandomUtil.randomInt(0, 10);
            System.out.println(String.format("insert into blossom_stat values(null, 1, '%s' ,%s);", dt, value));
        }
    }

    private static void genWords() {
        int value = 203012;
        Date begin = DateUtils.parse("2019-01-01", DateUtils.PATTERN_YYYYMMDD);
        Date end = DateUtils.parse("2023-06-30", DateUtils.PATTERN_YYYYMMDD);
        for (; DateUtils.compare(begin, end) <= 0; begin = DateUtils.offsetMonth(begin, 1)) {
            String dt = DateUtils.format(begin, DateUtils.PATTERN_YYYYMMDD);
            value = value + RandomUtil.randomInt(0, 10000);
            System.out.println(String.format("insert into blossom_stat values(null, 2, '%s' ,%s);", dt, value));
        }
    }

    public static void main(String[] args) {
//        ArticleEntity a = new ArticleEntity();
//        a.setName("123123213");
//        a.setHtml("asdasd");
//        System.out.println(exportHtml(a));
    }
}
