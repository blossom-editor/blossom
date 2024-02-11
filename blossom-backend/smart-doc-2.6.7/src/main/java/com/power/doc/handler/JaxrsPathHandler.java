/*
 * smart-doc https://github.com/smart-doc-group/smart-doc
 *
 * Copyright (C) 2018-2023 smart-doc
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.power.doc.handler;

import com.power.common.util.StringUtil;
import com.power.common.util.UrlUtil;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.JAXRSAnnotations;
import com.power.doc.constants.JakartaJaxrsAnnotations;
import com.power.doc.constants.MediaType;
import com.power.doc.model.request.JaxrsPathMapping;
import com.power.doc.utils.DocUrlUtil;
import com.power.doc.utils.DocUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.expression.AnnotationValue;

import java.util.*;

import static com.power.doc.constants.DocTags.DEPRECATED;
import static com.power.doc.constants.DocTags.IGNORE;

/**
 * Jaxrs Path Handler
 *
 * @author Zxq
 */
public class JaxrsPathHandler {

    /**
     * ANNOTATION_NAMES
     */
    private static final Set<String> ANNOTATION_NAMES = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList(
                    JakartaJaxrsAnnotations.JAXB_DELETE_FULLY, JakartaJaxrsAnnotations.JAX_PUT_FULLY,
                    JakartaJaxrsAnnotations.JAX_GET_FULLY, JakartaJaxrsAnnotations.JAX_POST_FULLY,
                    JakartaJaxrsAnnotations.JAX_PATCH_FULLY, JakartaJaxrsAnnotations.JAX_HEAD_FULLY,
                    JAXRSAnnotations.JAXB_DELETE_FULLY, JAXRSAnnotations.JAX_PUT_FULLY,
                    JAXRSAnnotations.JAX_GET_FULLY, JAXRSAnnotations.JAX_POST_FULLY,
                    JAXRSAnnotations.JAXB_PATCH_FULLY, JAXRSAnnotations.JAXB_HEAD_FULLY
            )));

    Map<String, String> constantsMap;

    public JaxrsPathMapping handle(ProjectDocConfigBuilder projectBuilder,
                                   String baseUrl,
                                   JavaMethod method,
                                   String mediaType) {

        List<JavaAnnotation> annotations = method.getAnnotations();
        this.constantsMap = projectBuilder.getConstantsMap();
        String methodType = null;
        String shortUrl = "";
        String serverUrl = projectBuilder.getServerUrl();
        String contextPath = projectBuilder.getApiConfig().getPathPrefix();
        boolean deprecated = false;
        for (JavaAnnotation annotation : annotations) {
            String annotationName = annotation.getType().getFullyQualifiedName();
            // method level annotation will override class level annotation
            if (annotationName.equals(JakartaJaxrsAnnotations.JAX_CONSUMES)
                    || annotationName.equals(JAXRSAnnotations.JAX_CONSUMES_FULLY)) {
                Object value = annotation.getNamedParameter("value");
                if (Objects.nonNull(value)) {
                    mediaType = MediaType.valueOf(value.toString());
                }
            }
            // Deprecated annotation on method
            if (DocGlobalConstants.JAVA_DEPRECATED_FULLY.equals(annotationName)) {
                deprecated = true;
            }
            if (JakartaJaxrsAnnotations.JAX_PATH_FULLY.equals(annotationName)
                    || JakartaJaxrsAnnotations.JAX_PATH_PARAM_FULLY.equals(annotationName)
                    || JakartaJaxrsAnnotations.JAXB_REST_PATH_FULLY.equals(annotationName)
                    || JAXRSAnnotations.JAX_PATH_FULLY.equals(annotationName)
                    || JAXRSAnnotations.JAX_PATH_PARAM_FULLY.equals(annotationName)) {
                shortUrl = DocUtil.handleMappingValue(annotation);
            }
            // annotationName like "Get" "Post", not "jakarta.ws.rs.Get" "jakarta.ws.rs.Post"
            if (ANNOTATION_NAMES.stream().anyMatch(it -> it.contains(annotationName))) {
                methodType = annotation.getType().getName();
            }
        }
        // @deprecated tag on method
        if (Objects.nonNull(method.getTagByName(DEPRECATED))) {
            deprecated = true;
        }
        JaxrsPathMapping jaxrsPathMapping = getJaxbPathMapping(projectBuilder, baseUrl, method, shortUrl, serverUrl, contextPath);
        if (jaxrsPathMapping != null) {
            return jaxrsPathMapping.setDeprecated(deprecated)
                    .setMethodType(methodType)
                    .setMediaType(mediaType);
        }
        return null;
    }

    private JaxrsPathMapping getJaxbPathMapping(ProjectDocConfigBuilder projectBuilder,
                                                String baseUrl, JavaMethod method,
                                                String shortUrl,
                                                String serverUrl,
                                                String contextPath) {
        String url;
        if (Objects.nonNull(shortUrl)) {
            if (Objects.nonNull(method.getTagByName(IGNORE))) {
                return null;
            }
            shortUrl = StringUtil.removeQuotes(shortUrl);
            List<String> urls = DocUtil.split(shortUrl);
            url = String.join(DocGlobalConstants.PATH_DELIMITER, serverUrl, contextPath, baseUrl, shortUrl);
            shortUrl = String.join(DocGlobalConstants.PATH_DELIMITER, DocGlobalConstants.PATH_DELIMITER, contextPath, baseUrl, shortUrl);
            if (urls.size() > 1) {
                url = DocUrlUtil.getMvcUrls(serverUrl, contextPath + "/" + baseUrl, urls);
                shortUrl = DocUrlUtil.getMvcUrls(DocGlobalConstants.EMPTY, contextPath + "/" + baseUrl, urls);
            }
            for (Map.Entry<String, String> entry : constantsMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (url.contains(key)) {
                    url = url.replace(key, value).replace("+", "");
                }
                if (shortUrl.contains(key)) {
                    shortUrl = shortUrl.replace(key, value).replace("+", "");
                }
            }
            String urlSuffix = projectBuilder.getApiConfig().getUrlSuffix();
            url = UrlUtil.simplifyUrl(url);
            shortUrl = UrlUtil.simplifyUrl(shortUrl);
            if (StringUtil.isNotEmpty(urlSuffix)) {
                url += urlSuffix;
                shortUrl += urlSuffix;
            }
            return JaxrsPathMapping.builder()
                    .setUrl(StringUtil.trim(url))
                    .setShortUrl(StringUtil.trim(shortUrl));
        }
        return null;
    }

}