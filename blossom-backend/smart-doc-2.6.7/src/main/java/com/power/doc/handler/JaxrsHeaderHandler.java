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
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.DocTags;
import com.power.doc.constants.JAXRSAnnotations;
import com.power.doc.constants.JakartaJaxrsAnnotations;
import com.power.doc.model.ApiReqParam;
import com.power.doc.utils.DocClassUtil;
import com.power.doc.utils.DocUtil;
import com.power.doc.utils.StringUtils;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Jaxrs Header Handler
 *
 * @author Zxq
 */
public class JaxrsHeaderHandler {

    /**
     * Handle JAX RS Header
     *
     * @param method         method
     * @param projectBuilder ProjectDocConfigBuilder
     * @return list of ApiReqParam
     */
    public List<ApiReqParam> handle(JavaMethod method, ProjectDocConfigBuilder projectBuilder) {
        Map<String, String> constantsMap = projectBuilder.getConstantsMap();

        List<ApiReqParam> apiReqHeaders = new ArrayList<>();
        List<JavaParameter> parameters = method.getParameters();
        for (JavaParameter javaParameter : parameters) {
            List<JavaAnnotation> annotations = javaParameter.getAnnotations();
            String paramName = javaParameter.getName();

            // hit target head annotation
            ApiReqParam apiReqHeader = new ApiReqParam();

            String defaultValue = "";
            for (JavaAnnotation annotation : annotations) {
                String annotationName = annotation.getType().getFullyQualifiedName();
                //Obtain header default value
                if (JakartaJaxrsAnnotations.JAX_DEFAULT_VALUE_FULLY.equals(annotationName)
                    || JAXRSAnnotations.JAX_DEFAULT_VALUE_FULLY.equals(annotationName)) {
                    defaultValue = StringUtil.removeQuotes(DocUtil.getRequestHeaderValue(annotation));
                    defaultValue = DocUtil.handleConstants(constantsMap, defaultValue);
                }
                apiReqHeader.setValue(defaultValue);

                // Obtain header value
                if (JakartaJaxrsAnnotations.JAX_HEADER_PARAM_FULLY.equals(annotationName)
                    || JAXRSAnnotations.JAX_HEADER_PARAM_FULLY.equals(annotationName)) {
                    String name = StringUtil.removeQuotes(DocUtil.getRequestHeaderValue(annotation));
                    name = DocUtil.handleConstants(constantsMap, name);
                    apiReqHeader.setName(name);

                    String typeName = javaParameter.getType().getValue().toLowerCase();
                    apiReqHeader.setType(DocClassUtil.processTypeNameForParams(typeName));

                    String className = method.getDeclaringClass().getCanonicalName();
                    Map<String, String> paramMap = DocUtil.getCommentsByTag(method, DocTags.PARAM, className);
                    String paramComments = paramMap.get(paramName);
                    apiReqHeader.setDesc(getComments(defaultValue, paramComments));
                    apiReqHeaders.add(apiReqHeader);
                }
            }
        }
        return apiReqHeaders;
    }


    private String getComments(String defaultValue, String paramComments) {
        if (Objects.nonNull(paramComments)) {
            StringBuilder desc = new StringBuilder();
            desc.append(paramComments);
            if (StringUtils.isNotBlank(defaultValue)) {
                desc.append("(defaultValue: ")
                    .append(defaultValue)
                    .append(")");
            }
            return desc.toString();
        }
        return "";
    }

}