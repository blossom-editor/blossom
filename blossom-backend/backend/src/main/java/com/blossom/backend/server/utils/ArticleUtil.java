package com.blossom.backend.server.utils;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.blossom.backend.base.user.pojo.UserEntity;
import com.blossom.backend.server.article.draft.pojo.ArticleEntity;
import com.blossom.common.base.enums.YesNo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

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

    /**
     *
     */
    private static final String HEAD_SCRIPT_BLOG_COLOR = "  <script>\n" +
            "      window.addEventListener('load', function() {\n" +
            "        const rgb = '{BLOSSOM_WEB_BLOG_COLOR}'\n" +
            "        if (rgb && !rgb.toLowerCase().startsWith('rgb(')) {\n" +
            "          return\n" +
            "        }\n" +
            "        const prefix = rgb.substring(4, rgb.length - 1)\n" +
            "        let text = `:root {\n" +
            "          --bl-color-primary: ${rgb};\n" +
            "        }`\n" +
            "\n" +
            "        let themeStyleTag = document.createElement('style')\n" +
            "        themeStyleTag.type = 'text/css'\n" +
            "        themeStyleTag.id = 'BLOSSOM_TEMPVISIT_STYLE_TAG'\n" +
            "        themeStyleTag.innerHTML = text\n" +
            "        document\n" +
            "          .getElementsByTagName('head')\n" +
            "          .item(0)\n" +
            "          .appendChild(themeStyleTag)\n" +
            "      });\n" +
            "  </script>\n";

    private static final String HEAD_SCRIPT_WATERMARK = "  <script>\n" +
            "      window.addEventListener(\"load\", function () {\n" +
            "        const FontGap = 3\n" +
            "\n" +
            "        function prepareCanvas(width, height, ratio = 1){\n" +
            "          const canvas = document.createElement('canvas')\n" +
            "          const ctx = canvas.getContext('2d')\n" +
            "          const realWidth = width * ratio\n" +
            "          const realHeight = height * ratio\n" +
            "          canvas.setAttribute('width', `${realWidth}px`)\n" +
            "          canvas.setAttribute('height', `${realHeight}px`)\n" +
            "          ctx.save()\n" +
            "          return [ctx, canvas, realWidth, realHeight]\n" +
            "        }\n" +
            "\n" +
            "        // Get single clips\n" +
            "        function getClips(content, rotate, ratio, width, height, font, gapX, gapY) {\n" +
            "          // ================= Text =================\n" +
            "          const [ctx, canvas, contentWidth, contentHeight] = prepareCanvas(width, height, ratio)\n" +
            "          const {color, fontSize, fontStyle, fontWeight, fontFamily } = font\n" +
            "          const mergedFontSize = Number(fontSize) * ratio\n" +
            "          ctx.font = `${fontStyle} normal ${fontWeight} ${mergedFontSize}px/${height}px ${fontFamily}`\n" +
            "          ctx.fillStyle = color\n" +
            "          ctx.textAlign = 'center'\n" +
            "          ctx.textBaseline = 'top'\n" +
            "          const contents = Array.isArray(content) ? content : [content]\n" +
            "          contents?.forEach((item, index) => {\n" +
            "            ctx.fillText(\n" +
            "              item ?? 'ccccc',\n" +
            "              contentWidth / 2,\n" +
            "              index * (mergedFontSize + FontGap * ratio)\n" +
            "            )\n" +
            "          })\n" +
            "\n" +
            "          // ==================== Rotate ====================\n" +
            "          const angle = (Math.PI / 180) * Number(rotate)\n" +
            "          const maxSize = Math.max(width, height)\n" +
            "          const [rCtx, rCanvas, realMaxSize] = prepareCanvas(maxSize, maxSize, ratio)\n" +
            "\n" +
            "          // Copy from `ctx` and rotate\n" +
            "          rCtx.translate(realMaxSize / 2, realMaxSize / 2)\n" +
            "          rCtx.rotate(angle)\n" +
            "          if (contentWidth > 0 && contentHeight > 0) {\n" +
            "            rCtx.drawImage(canvas, -contentWidth / 2, -contentHeight / 2)\n" +
            "          }\n" +
            "\n" +
            "          // Get boundary of rotated text\n" +
            "          function getRotatePos(x, y) {\n" +
            "            const targetX = x * Math.cos(angle) - y * Math.sin(angle)\n" +
            "            const targetY = x * Math.sin(angle) + y * Math.cos(angle)\n" +
            "            return [targetX, targetY]\n" +
            "          }\n" +
            "\n" +
            "          let left = 0\n" +
            "          let right = 0\n" +
            "          let top = 0\n" +
            "          let bottom = 0\n" +
            "\n" +
            "          const halfWidth = contentWidth / 2\n" +
            "          const halfHeight = contentHeight / 2\n" +
            "          const points = [\n" +
            "            [0 - halfWidth, 0 - halfHeight],\n" +
            "            [0 + halfWidth, 0 - halfHeight],\n" +
            "            [0 + halfWidth, 0 + halfHeight],\n" +
            "            [0 - halfWidth, 0 + halfHeight],\n" +
            "          ]\n" +
            "          points.forEach(([x, y]) => {\n" +
            "            const [targetX, targetY] = getRotatePos(x, y)\n" +
            "            left = Math.min(left, targetX)\n" +
            "            right = Math.max(right, targetX)\n" +
            "            top = Math.min(top, targetY)\n" +
            "            bottom = Math.max(bottom, targetY)\n" +
            "          })\n" +
            "\n" +
            "          const cutLeft = left + realMaxSize / 2\n" +
            "          const cutTop = top + realMaxSize / 2\n" +
            "          const cutWidth = right - left\n" +
            "          const cutHeight = bottom - top\n" +
            "\n" +
            "          // ================ Fill Alternate ================\n" +
            "          const realGapX = gapX * ratio\n" +
            "          const realGapY = gapY * ratio\n" +
            "          const filledWidth = (cutWidth + realGapX) * 2\n" +
            "          const filledHeight = cutHeight + realGapY\n" +
            "\n" +
            "          const [fCtx, fCanvas] = prepareCanvas(filledWidth, filledHeight)\n" +
            "\n" +
            "          function drawImg(targetX = 0, targetY = 0) {\n" +
            "            fCtx.drawImage(rCanvas, cutLeft, cutTop, cutWidth, cutHeight, targetX, targetY, cutWidth, cutHeight)\n" +
            "          }\n" +
            "          drawImg()\n" +
            "          drawImg(cutWidth + realGapX, -cutHeight / 2 - realGapY / 2)\n" +
            "          drawImg(cutWidth + realGapX, +cutHeight / 2 + realGapY / 2)\n" +
            "          return [fCanvas.toDataURL(), filledWidth / ratio, filledHeight / ratio]\n" +
            "        }\n" +
            "        \n" +
            "        const getMarkSize = (ctx, content) => {\n" +
            "          let defaultWidth = 120\n" +
            "          let defaultHeight = 64\n" +
            "          const width = 120\n" +
            "          const height = 64\n" +
            "          if (ctx.measureText) {\n" +
            "            ctx.font = `${Number('15')}px sans-serif`\n" +
            "            const contents = Array.isArray(content) ? content : [content]\n" +
            "            const sizes = contents.map((item) => {\n" +
            "              const metrics = ctx.measureText(item)\n" +
            "\n" +
            "              return [metrics.width,\n" +
            "                metrics.fontBoundingBoxAscent !== undefined\n" +
            "                  ? metrics.fontBoundingBoxAscent + metrics.fontBoundingBoxDescent\n" +
            "                  : metrics.actualBoundingBoxAscent + metrics.actualBoundingBoxDescent,\n" +
            "              ]\n" +
            "            })\n" +
            "            defaultWidth = Math.ceil(Math.max(...sizes.map((size) => size[0])))\n" +
            "            defaultHeight =\n" +
            "              Math.ceil(Math.max(...sizes.map((size) => size[1]))) * contents.length +\n" +
            "              (contents.length - 1) * FontGap\n" +
            "          }\n" +
            "          return [width ?? defaultWidth, height ?? defaultHeight]\n" +
            "        }\n" +
            "        \n" +
            "        function toLowercaseSeparator(key) {\n" +
            "          return key.replace(/([A-Z])/g, '-$1').toLowerCase()\n" +
            "        }\n" +
            "\n" +
            "        function getStyleStr(style) {\n" +
            "          return Object.keys(style)\n" +
            "            .map(\n" +
            "              (key) =>\n" +
            "                `${toLowercaseSeparator(key)}: ${style[key]};`\n" +
            "            )\n" +
            "            .join(' ')\n" +
            "        }\n" +
            "\n" +
            "        const getMarkStyle = (rotate) => {\n" +
            "          const markStyle = {zIndex: 9, position: 'absolute', left: 0, top: 0, width: '100%', height: '100%', pointerEvents: 'none', backgroundRepeat: 'repeat' }\n" +
            "\n" +
            "          /** Calculate the style of the offset */\n" +
            "          let positionLeft = rotate / 2 - rotate / 2\n" +
            "          let positionTop = rotate / 2 - rotate / 2\n" +
            "          if (positionLeft > 0) {\n" +
            "            markStyle.left = `${positionLeft}px`\n" +
            "            markStyle.width = `calc(100% - ${positionLeft}px)`\n" +
            "            positionLeft = 0\n" +
            "          }\n" +
            "          if (positionTop > 0) {\n" +
            "            markStyle.top = `${positionTop}px`\n" +
            "            markStyle.height = `calc(100% - ${positionTop}px)`\n" +
            "            positionTop = 0\n" +
            "          }\n" +
            "          markStyle.backgroundPosition = `${positionLeft}px ${positionTop}px`\n" +
            "          return markStyle\n" +
            "        }\n" +
            "        \n" +
            "        let stopObservation = false\n" +
            "        const observerContainer = document.getElementsByClassName('bl-preview')[0]\n" +
            "        const observerConfig = { attributes: true, childList: true, subtree: true };\n" +
            "        const observer = new MutationObserver(() => {\n" +
            "          renderWatermark()\n" +
            "        });\n" +
            "\n" +
            "        const renderWatermark = () => {\n" +
            "          const canvas = document.createElement(\"canvas\")\n" +
            "          const ctx = canvas.getContext(\"2d\")\n" +
            "          const rotate = -22\n" +
            "          const content = '{WEB_BLOG_WATERMARK_CONTENT}'\n" +
            "          const color = '{WEB_BLOG_WATERMARK_COLOR}'\n" +
            "          const fontSize = {WEB_BLOG_WATERMARK_FONTSIZE}\n" +
            "          const gap = {WEB_BLOG_WATERMARK_GAP}\n" +
            "\n" +
            "          if (ctx) {\n" +
            "            const ratio = window.devicePixelRatio || 1\n" +
            "            const [markWidth, markHeight] = getMarkSize(ctx, content)\n" +
            "\n" +
            "            const drawCanvas = (drawContent) => {\n" +
            "              const [textClips, clipWidth] = getClips(drawContent, rotate, ratio, markWidth, markHeight, \n" +
            "                {color: color, fontSize: fontSize, fontStyle: 'normal', fontWeight: 'normal'}, gap, gap\n" +
            "              )\n" +
            "              let watermark = document.createElement('div')\n" +
            "              let container = document.getElementsByClassName('bl-preview')[0]\n" +
            "              stopObservation = true\n" +
            "              observer.disconnect()\n" +
            "              watermark.setAttribute('style',getStyleStr({\n" +
            "                  ...getMarkStyle(rotate),\n" +
            "                  backgroundImage: `url('${textClips}')`,\n" +
            "                  backgroundSize: `${Math.floor(clipWidth)}px`,\n" +
            "                })\n" +
            "              )\n" +
            "              container.append(watermark)\n" +
            "              setTimeout(() => {\n" +
            "                observer.observe(observerContainer, observerConfig)\n" +
            "              })\n" +
            "            };\n" +
            "            \n" +
            "            drawCanvas(content);\n" +
            "          }\n" +
            "        };\n" +
            "\n" +
            "        renderWatermark()\n" +
            "      })\n" +
            "  </script>\n";


    private static final String BODY_HEADER = "\n" +
            "<body>\n" +
            "  <div class=\"header\">\n" +
            "    <div class=\"copyright\">本文作者：{BLOSSOM_EXPORT_HTML_AUTHOR}。著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。</div><a\n" +
            "  href=\"https://github.com/blossom-editor/blossom\" target=\"_blank\"><span>Export by Blossom</span><svg\n" +
            "  xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-github\"\n" +
            "  viewBox=\"0 0 16 16\">\n" +
            "    <path\n" +
            "    d=\"M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.012 8.012 0 0 0 16 8c0-4.42-3.58-8-8-8z\" />\n" +
            "    </svg></a>\n" +
            "  </div>\n" +
            "  <div class=\"content \">\n" +
            "    <div class=\"main\">\n" +
            "      <div class=\"bl-preview\" id=\"blossom-view\">";

    private static final String BODY_CONTENT_TOC = "\n" +
            "  <div class=\"toc\" id=\"blossom-toc\">\n" +
            "    <div style=\"font-size: 15px;color:#727272;padding:10px 0\">《{BLOSSOM_EXPORT_HTML_ARTICLE_NAME}》</div>\n" +
            "    <div style=\"font-size: 20px;color:#727272;border-bottom:2px solid #eaeaea;padding-bottom: 10px;margin-bottom: 10px;\">目录</div>\n" +
            "  </div>\n";

    private static final String suffix = "</div></body></html>";

    private static String htmlTemplate;

    static {
        Resource resource = new ClassPathResource("exportTemplate.html");
        try (InputStream is = resource.getInputStream()) {
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            htmlTemplate = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将文章转换为 html 格式
     *
     * @param article                     文章
     * @param user                        用户, 用户获取作者
     * @param blogColor                   主颜色
     * @param WEB_BLOG_WATERMARK_ENABLED  开启水印
     * @param WEB_BLOG_WATERMARK_CONTENT  水印内容
     * @param WEB_BLOG_WATERMARK_FONTSIZE 水印字体大小
     * @param WEB_BLOG_WATERMARK_COLOR    水印颜色
     * @param WEB_BLOG_WATERMARK_GAP      水印密集度
     * @return html 内容
     */
    public static String toHtml(ArticleEntity article,
                                UserEntity user,
                                String blogColor,
                                String WEB_BLOG_WATERMARK_ENABLED,
                                String WEB_BLOG_WATERMARK_CONTENT,
                                String WEB_BLOG_WATERMARK_FONTSIZE,
                                String WEB_BLOG_WATERMARK_COLOR,
                                String WEB_BLOG_WATERMARK_GAP) {
        return htmlTemplate
                + appendHeadScript(blogColor, WEB_BLOG_WATERMARK_ENABLED, WEB_BLOG_WATERMARK_CONTENT, WEB_BLOG_WATERMARK_FONTSIZE, WEB_BLOG_WATERMARK_COLOR, WEB_BLOG_WATERMARK_GAP)
                + appendBodyHeader(user)
                + appendArticleContent(article)
                + appendToc(article)
                + suffix;
    }

    /**
     * 添加 head 下的动态 script
     *
     * @param blogColor                   主题色
     * @param WEB_BLOG_WATERMARK_ENABLED  开启水印
     * @param WEB_BLOG_WATERMARK_CONTENT  水印内容
     * @param WEB_BLOG_WATERMARK_FONTSIZE 水印字体大小
     * @param WEB_BLOG_WATERMARK_COLOR    水印颜色
     * @param WEB_BLOG_WATERMARK_GAP      水印密集度
     * @return head 下的所有动态 script
     */
    private static String appendHeadScript(String blogColor,
                                           String WEB_BLOG_WATERMARK_ENABLED,
                                           String WEB_BLOG_WATERMARK_CONTENT,
                                           String WEB_BLOG_WATERMARK_FONTSIZE,
                                           String WEB_BLOG_WATERMARK_COLOR,
                                           String WEB_BLOG_WATERMARK_GAP) {
        String script = HEAD_SCRIPT_BLOG_COLOR.replaceAll("\\{BLOSSOM_WEB_BLOG_COLOR}", blogColor);
        if (YesNo.YES.getValue().toString().equals(WEB_BLOG_WATERMARK_ENABLED)) {
            script += HEAD_SCRIPT_WATERMARK
                    .replaceAll("\\{WEB_BLOG_WATERMARK_CONTENT}", WEB_BLOG_WATERMARK_CONTENT)
                    .replaceAll("\\{WEB_BLOG_WATERMARK_FONTSIZE}", WEB_BLOG_WATERMARK_FONTSIZE)
                    .replaceAll("\\{WEB_BLOG_WATERMARK_COLOR}", WEB_BLOG_WATERMARK_COLOR)
                    .replaceAll("\\{WEB_BLOG_WATERMARK_GAP}", WEB_BLOG_WATERMARK_GAP);
        }
        return script + "</head>";
    }

    /**
     * 添加页面顶部的文章作者和目录顶部的文章名称
     *
     * @param article 文章
     * @param user    用户, 用户获取作者
     * @return 页面顶部的文章作者和文章名称
     */
    private static String appendBodyHeader(UserEntity user) {
        return BODY_HEADER
                .replaceAll("\\{BLOSSOM_EXPORT_HTML_AUTHOR}", user.getNickName());
    }

    /**
     * 添加正文
     *
     * @param article 文章
     */
    private static String appendArticleContent(ArticleEntity article) {
        return article.getHtml()
                + "\n    </div>"
                + "\n  </div>";
    }

    /**
     * 添加目录
     *
     * @param article 文章
     */
    private static String appendToc(ArticleEntity article) {
        return BODY_CONTENT_TOC
                .replaceAll("\\{BLOSSOM_EXPORT_HTML_ARTICLE_NAME}", article.getName());
    }
}
