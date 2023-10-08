/*
 * Copyright 2002-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.power.doc.constants;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * copied from springframework<br/>
 * as defined in the HTTP specification.
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @author Sebastien Deleuze
 * @author Kazuki Shimizu
 * @author Sam Brannen
 * @see <a href="https://tools.ietf.org/html/rfc7231#section-3.1.1.1">
 * HTTP 1.1: Semantics and Content, section 3.1.1.1</a>
 * @since 3.0
 */
public class MediaType implements Serializable {

    public static final String ALL_VALUE = "*/*";

    public static final String APPLICATION_ATOM_XML_VALUE = "application/atom+xml";

    public static final String APPLICATION_CBOR_VALUE = "application/cbor";

    public static final String APPLICATION_FORM_URLENCODED_VALUE = "application/x-www-form-urlencoded";

    public static final String APPLICATION_GRAPHQL_VALUE = "application/graphql+json";

    public static final String APPLICATION_JSON_VALUE = "application/json";

    /**
     * Add for JAX-RS
     */
    public static final String APPLICATION_JSON = "application/json";

    public static final String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";

    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

    public static final String APPLICATION_PDF_VALUE = "application/pdf";

    public static final String APPLICATION_PROBLEM_JSON_VALUE = "application/problem+json";

    public static final String APPLICATION_PROBLEM_XML_VALUE = "application/problem+xml";

    public static final String APPLICATION_RSS_XML_VALUE = "application/rss+xml";

    public static final String APPLICATION_NDJSON_VALUE = "application/x-ndjson";

    public static final String APPLICATION_XHTML_XML_VALUE = "application/xhtml+xml";

    public static final String APPLICATION_XML_VALUE = "application/xml";

    public static final String IMAGE_GIF_VALUE = "image/gif";

    public static final String IMAGE_JPEG_VALUE = "image/jpeg";

    public static final String IMAGE_PNG_VALUE = "image/png";

    public static final String MULTIPART_FORM_DATA_VALUE = "multipart/form-data";

    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    public static final String MULTIPART_MIXED_VALUE = "multipart/mixed";

    public static final String MULTIPART_RELATED_VALUE = "multipart/related";

    public static final String TEXT_EVENT_STREAM_VALUE = "text/event-stream";

    public static final String TEXT_HTML_VALUE = "text/html";

    public static final String TEXT_HTML = "text/html";
    public static final String TEXT_MARKDOWN_VALUE = "text/markdown";

    public static final String TEXT_PLAIN_VALUE = "text/plain";

    public static final String TEXT_PLAIN = "text/plain";

    public static final String TEXT_XML_VALUE = "text/xml";

    private static final long serialVersionUID = 2069937152339670231L;

    private static final Map<String, String> fieldMap;

    static {
        fieldMap = Arrays.stream(MediaType.class.getDeclaredFields())
                .filter(it -> it.getType().equals(String.class))
                .map(it -> {
                    try {
                        return new String[]{it.getName(), String.valueOf(it.get(null))};
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toMap(it -> it[0], it -> it[1]));
    }

    public static String valueOf(String name) {
        // if not springframework MediaType constant, return original value
        if (!name.contains("MediaType.")) return name.replace("\"", "");
        String[] split = name.replace("[", "").replace("]", "").split(",");
        return Arrays.stream(split)
                .map(it -> it.replace("MediaType.", "").trim())
                .distinct()
                .map(fieldMap::get)
                .collect(Collectors.joining(";"));
    }

}
