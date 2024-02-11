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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.power.common.util.StringUtil;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.DocTags;
import com.power.doc.model.ApiReqParam;
import com.power.doc.model.annotation.HeaderAnnotation;
import com.power.doc.utils.DocClassUtil;
import com.power.doc.utils.DocUtil;
import com.power.doc.utils.JavaFieldUtil;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaType;

/**
 * @author yu3.sun on 2022/8/30
 */
public interface IHeaderHandler {

    default List<ApiReqParam> handle(JavaMethod method, ProjectDocConfigBuilder projectBuilder) {
        Map<String, String> constantsMap = projectBuilder.getConstantsMap();
        List<ApiReqParam> mappingHeaders = new ArrayList<>();
        List<JavaAnnotation> annotations = method.getAnnotations();
        HeaderAnnotation headerAnnotation = getHeaderAnnotation();
        for (JavaAnnotation annotation : annotations) {
            String annotationName = annotation.getType().getValue();
            Object headersObject = annotation.getNamedParameter("headers");
            if (!isMapping(annotationName) || Objects.isNull(headersObject)) {
                continue;
            }
            String mappingHeader = StringUtil.removeQuotes(headersObject.toString());
            if (!mappingHeader.startsWith("[")) {
                processMappingHeaders(mappingHeader, mappingHeaders);
                continue;
            }
            List<String> headers = (LinkedList) headersObject;
            for (String str : headers) {
                String header = StringUtil.removeQuotes(str);
                if (header.startsWith("!")) {
                    continue;
                }
                processMappingHeaders(header, mappingHeaders);
            }
        }
        List<ApiReqParam> reqHeaders = new ArrayList<>();
        for (JavaParameter javaParameter : method.getParameters()) {
            List<JavaAnnotation> javaAnnotations = javaParameter.getAnnotations();
            String className = method.getDeclaringClass().getCanonicalName();
            Map<String, String> paramMap = DocUtil.getCommentsByTag(method, DocTags.PARAM, className);
            String paramName = javaParameter.getName();
            JavaType javaType = javaParameter.getType();
            String simpleTypeName = javaType.getValue();
            ApiReqParam apiReqHeader;
            for (JavaAnnotation annotation : javaAnnotations) {
                String annotationName = annotation.getType().getValue();
                if (headerAnnotation.getAnnotationName().equals(annotationName)) {
                    apiReqHeader = new ApiReqParam();
                    Map<String, Object> requestHeaderMap = annotation.getNamedParameterMap();
                    //  Obtain header value
                    if (requestHeaderMap.get(headerAnnotation.getValueProp()) != null) {
                        String attrValue = DocUtil.handleRequestHeaderValue(annotation);
                        String constValue = ((String) requestHeaderMap.get(headerAnnotation.getValueProp())).replaceAll("\"", "");
                        if (StringUtil.isEmpty(attrValue)) {
                            Object value = constantsMap.get(constValue);
                            if (value != null) {
                                apiReqHeader.setName(value.toString());
                            } else {
                                apiReqHeader.setName(constValue);
                            }
                        } else {
                            apiReqHeader.setName(attrValue);
                        }
                    } else {
                        apiReqHeader.setName(paramName);
                    }
                    StringBuilder desc = new StringBuilder();
                    String comments = paramMap.get(paramName);
                    desc.append(DocUtil.paramCommentResolve(comments));
                    String mockValue = JavaFieldUtil.createMockValue(paramMap, paramName, javaType.getGenericCanonicalName(), simpleTypeName);
                    apiReqHeader.setValue(mockValue);
                    //  Obtain header default value
                    if (requestHeaderMap.get(headerAnnotation.getDefaultValueProp()) != null) {
                        apiReqHeader.setValue(StringUtil.removeQuotes((String) requestHeaderMap.get(headerAnnotation.getDefaultValueProp())));
                        desc.append("(defaultValue: ")
                            .append(StringUtil.removeQuotes((String) requestHeaderMap.get(headerAnnotation.getDefaultValueProp())))
                            .append(")");
                    }
                    apiReqHeader.setDesc(desc.toString());
                    if (requestHeaderMap.get(headerAnnotation.getRequiredProp()) != null) {
                        apiReqHeader.setRequired(!Boolean.FALSE.toString()
                            .equals(requestHeaderMap.get(headerAnnotation.getRequiredProp())));
                    } else {
                        apiReqHeader.setRequired(true);
                    }
                    String typeName = javaParameter.getType().getValue().toLowerCase();
                    apiReqHeader.setType(DocClassUtil.processTypeNameForParams(typeName));
                    reqHeaders.add(apiReqHeader);
                    break;
                }
            }
        }
        return Stream.of(mappingHeaders, reqHeaders)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }

    default void processMappingHeaders(String header, List<ApiReqParam> mappingHeaders) {
        if (header.contains("!=")) {
            String headerName = header.substring(0, header.indexOf("!"));
            ApiReqParam apiReqHeader = ApiReqParam.builder()
                .setName(headerName)
                .setRequired(true)
                .setValue(null)
                .setDesc("header condition")
                .setType("string");
            mappingHeaders.add(apiReqHeader);
        } else {
            String headerName;
            String headerValue = null;
            if (header.contains("=")) {
                int index = header.indexOf("=");
                headerName = header.substring(0, index);
                headerValue = header.substring(index + 1);
            } else {
                headerName = header;
            }
            ApiReqParam apiReqHeader = ApiReqParam.builder()
                .setName(headerName)
                .setRequired(true)
                .setValue(headerValue)
                .setDesc("header condition")
                .setType("string");
            mappingHeaders.add(apiReqHeader);
        }
    }

    /**
     * check mapping annotation
     *
     * @param annotationName annotation name
     * @return boolean
     */
    boolean isMapping(String annotationName);


    /**
     * Get framework annotation info
     *
     * @return Header annotation info
     */
    HeaderAnnotation getHeaderAnnotation();
}
