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

import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.DocAnnotationConstants;
import com.power.doc.constants.Methods;
import com.power.doc.constants.SolonAnnotations;
import com.power.doc.function.RequestMappingFunc;
import com.power.doc.model.annotation.FrameworkAnnotations;
import com.power.doc.model.request.RequestMapping;
import com.power.doc.utils.DocUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaMethod;

import java.util.List;
import java.util.Objects;

import static com.power.doc.constants.DocTags.DEPRECATED;
import static com.power.doc.constants.DocTags.IGNORE;

/**
 * @author noear 2022/2/19 created
 */
public class SolonRequestMappingHandler implements IRequestMappingHandler {

    /**
     * handle solon request mapping
     *
     * @param projectBuilder    projectBuilder
     * @param controllerBaseUrl solon mvc controller base url
     * @param method            JavaMethod
     * @return RequestMapping
     */
    public RequestMapping handle(ProjectDocConfigBuilder projectBuilder,
        String controllerBaseUrl,
        JavaMethod method, FrameworkAnnotations frameworkAnnotations,
        RequestMappingFunc requestMappingFunc) {
        if (Objects.nonNull(method.getTagByName(IGNORE))) {
            return null;
        }
        List<JavaAnnotation> annotations = getAnnotations(method);
        String methodType = "GET";//default is get
        String shortUrl = null;
        String mediaType = null;
        boolean deprecated = false;
        for (JavaAnnotation annotation : annotations) {
            String annotationName = annotation.getType().getName();
            if (DocAnnotationConstants.DEPRECATED.equals(annotationName)) {
                deprecated = true;
            }
            if (SolonAnnotations.REQUEST_MAPPING.equals(annotationName) || SolonAnnotations.REQUEST_MAPPING_FULLY.equals(annotationName)) {
                shortUrl = DocUtil.handleMappingValue(annotation);
                Object produces = annotation.getNamedParameter("produces");
                if (Objects.nonNull(produces)) {
                    mediaType = produces.toString();
                }
            }
            if (SolonAnnotations.GET_MAPPING.equals(annotationName) || SolonAnnotations.GET_MAPPING_FULLY.equals(annotationName)) {
                methodType = Methods.GET.getValue();
            } else if (SolonAnnotations.POST_MAPPING.equals(annotationName) || SolonAnnotations.POST_MAPPING_FULLY.equals(annotationName)) {
                methodType = Methods.POST.getValue();
            } else if (SolonAnnotations.PUT_MAPPING.equals(annotationName) || SolonAnnotations.PUT_MAPPING_FULLY.equals(annotationName)) {
                methodType = Methods.PUT.getValue();
            } else if (SolonAnnotations.PATCH_MAPPING.equals(annotationName) || SolonAnnotations.PATCH_MAPPING_FULLY.equals(annotationName)) {
                methodType = Methods.PATCH.getValue();
            } else if (SolonAnnotations.DELETE_MAPPING.equals(annotationName) || SolonAnnotations.DELETE_MAPPING_FULLY.equals(annotationName)) {
                methodType = Methods.DELETE.getValue();
            }
        }
        if (Objects.nonNull(method.getTagByName(DEPRECATED))) {
            deprecated = true;
        }
        RequestMapping requestMapping = RequestMapping.builder()
            .setMediaType(mediaType)
            .setMethodType(methodType)
            .setDeprecated(deprecated)
            .setShortUrl(shortUrl);
        requestMapping = formatMappingData(projectBuilder, controllerBaseUrl, requestMapping);
        requestMappingFunc.process(method.getDeclaringClass(), requestMapping);
        return requestMapping;
    }
}
