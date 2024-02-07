/*
 * smart-doc
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
package com.power.doc.template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.power.common.util.StringUtil;
import com.power.common.util.UrlUtil;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.DocAnnotationConstants;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.DocTags;
import com.power.doc.helper.ParamsBuildHelper;
import com.power.doc.model.*;
import com.power.doc.model.annotation.FrameworkAnnotations;
import com.power.doc.utils.*;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaType;

import static com.power.doc.constants.DocGlobalConstants.NO_COMMENTS_FOUND;
import static com.power.doc.constants.DocTags.*;

/**
 * @author yu3.sun on 2022/10/2
 */
public interface IBaseDocBuildTemplate {

    default String paramCommentResolve(String comment) {
        if (StringUtil.isEmpty(comment)) {
            comment = NO_COMMENTS_FOUND;
        } else {
            if (comment.contains("|")) {
                comment = comment.substring(0, comment.indexOf("|"));
            }
        }
        return comment;
    }

    default List<ApiParam> buildReturnApiParams(DocJavaMethod docJavaMethod, ProjectDocConfigBuilder projectBuilder) {
        JavaMethod method = docJavaMethod.getJavaMethod();
        if (method.getReturns().isVoid() && Objects.isNull(projectBuilder.getApiConfig().getResponseBodyAdvice())) {
            return new ArrayList<>(0);
        }
        DocletTag downloadTag = method.getTagByName(DocTags.DOWNLOAD);
        if (Objects.nonNull(downloadTag)) {
            return new ArrayList<>(0);
        }
        String returnTypeGenericCanonicalName = method.getReturnType().getGenericCanonicalName();
        if (Objects.nonNull(projectBuilder.getApiConfig().getResponseBodyAdvice())
            && Objects.isNull(method.getTagByName(IGNORE_RESPONSE_BODY_ADVICE))) {
            String responseBodyAdvice = projectBuilder.getApiConfig().getResponseBodyAdvice().getClassName();
            if (!returnTypeGenericCanonicalName.startsWith(responseBodyAdvice)) {
                returnTypeGenericCanonicalName = new StringBuffer()
                    .append(responseBodyAdvice)
                    .append("<")
                    .append(returnTypeGenericCanonicalName).append(">").toString();
            }
        }
        Map<String, JavaType> actualTypesMap = docJavaMethod.getActualTypesMap();
        ApiReturn apiReturn = DocClassUtil.processReturnType(returnTypeGenericCanonicalName);
        String returnType = apiReturn.getGenericCanonicalName();
        if (Objects.nonNull(actualTypesMap)) {
            for (Map.Entry<String, JavaType> entry : actualTypesMap.entrySet()) {
                returnType = returnType.replace(entry.getKey(), entry.getValue().getCanonicalName());
            }
        }

        String typeName = apiReturn.getSimpleName();
        if (this.ignoreReturnObject(typeName, projectBuilder.getApiConfig().getIgnoreRequestParams())) {
            return new ArrayList<>(0);
        }
        if (JavaClassValidateUtil.isPrimitive(typeName)) {
            docJavaMethod.setReturnSchema(OpenApiSchemaUtil.primaryTypeSchema(typeName));
            return new ArrayList<>(0);
        }
        if (JavaClassValidateUtil.isCollection(typeName)) {
            if (returnType.contains("<")) {
                String gicName = returnType.substring(returnType.indexOf("<") + 1, returnType.lastIndexOf(">"));
                if (JavaClassValidateUtil.isPrimitive(gicName)) {
                    docJavaMethod.setReturnSchema(OpenApiSchemaUtil.arrayTypeSchema(gicName));
                    return new ArrayList<>(0);
                }
                 return ParamsBuildHelper.buildParams(gicName, "", 0, null, Boolean.TRUE,
                    new HashMap<>(), projectBuilder, null, 0, Boolean.FALSE, null);
            } else {
                return new ArrayList<>(0);
            }
        }
        if (JavaClassValidateUtil.isMap(typeName)) {
            String[] keyValue = DocClassUtil.getMapKeyValueType(returnType);
            if (keyValue.length == 0) {
                return new ArrayList<>(0);
            }
            return ParamsBuildHelper.buildParams(returnType, "", 0, null, Boolean.TRUE,
                new HashMap<>(), projectBuilder, null, 0, Boolean.FALSE, null);
        }
        if (StringUtil.isNotEmpty(returnType)) {
            return ParamsBuildHelper.buildParams(returnType, "", 0, null, Boolean.TRUE,
                new HashMap<>(), projectBuilder, null, 0, Boolean.FALSE, null);
        }
        return new ArrayList<>(0);
    }

    default void convertParamsDataToTree(ApiMethodDoc apiMethodDoc) {
        apiMethodDoc.setPathParams(ApiParamTreeUtil.apiParamToTree(apiMethodDoc.getPathParams()));
        apiMethodDoc.setQueryParams(ApiParamTreeUtil.apiParamToTree(apiMethodDoc.getQueryParams()));
        apiMethodDoc.setRequestParams(ApiParamTreeUtil.apiParamToTree(apiMethodDoc.getRequestParams()));
    }


    default String formatRequestUrl(Map<String, String> pathParamsMap, Map<String, String> queryParamsMap,
        String serverUrl, String path) {
        path = DocUtil.formatAndRemove(path, pathParamsMap);
        String url = UrlUtil.urlJoin(path, queryParamsMap);
        url = StringUtil.removeQuotes(url);
        url = serverUrl + "/" + url;
        url = UrlUtil.simplifyUrl(url);
        return url;
    }

    default List<DocJavaParameter> getJavaParameterList(ProjectDocConfigBuilder builder, final DocJavaMethod docJavaMethod,
        FrameworkAnnotations frameworkAnnotations) {
        JavaMethod javaMethod = docJavaMethod.getJavaMethod();
        Map<String, String> replacementMap = builder.getReplaceClassMap();
        Map<String, String> paramTagMap = docJavaMethod.getParamTagMap();
        List<JavaParameter> parameterList = javaMethod.getParameters();
        if (parameterList.isEmpty()) {
            return new ArrayList<>(0);
        }
        Set<String> ignoreSets = ignoreParamsSets(javaMethod);
        List<DocJavaParameter> apiJavaParameterList = new ArrayList<>(parameterList.size());
        Map<String, JavaType> actualTypesMap = docJavaMethod.getActualTypesMap();
        for (JavaParameter parameter : parameterList) {
            String paramName = parameter.getName();
            if (ignoreSets.contains(paramName)) {
                continue;
            }
            DocJavaParameter apiJavaParameter = new DocJavaParameter();
            apiJavaParameter.setJavaParameter(parameter);
            JavaType javaType = parameter.getType();
            if (Objects.nonNull(actualTypesMap) && Objects.nonNull(actualTypesMap.get(javaType.getCanonicalName()))) {
                javaType = actualTypesMap.get(javaType.getCanonicalName());
            }
            apiJavaParameter.setTypeValue(javaType.getValue());
            String genericCanonicalName = javaType.getGenericCanonicalName();
            String fullTypeName = javaType.getFullyQualifiedName();
            String commentClass = paramTagMap.get(paramName);
            //ignore request params
            if (Objects.nonNull(commentClass) && commentClass.contains(IGNORE)) {
                continue;
            }
            String rewriteClassName = getRewriteClassName(replacementMap, fullTypeName, commentClass);
            // rewrite class
            if (JavaClassValidateUtil.isClassName(rewriteClassName)) {
                genericCanonicalName = rewriteClassName;
                fullTypeName = DocClassUtil.getSimpleName(rewriteClassName);
            }
            if (JavaClassValidateUtil.isMvcIgnoreParams(genericCanonicalName, builder.getApiConfig().getIgnoreRequestParams())) {
                continue;
            }
            fullTypeName = DocClassUtil.rewriteRequestParam(fullTypeName);
            genericCanonicalName = DocClassUtil.rewriteRequestParam(genericCanonicalName);
            List<JavaAnnotation> annotations = parameter.getAnnotations();
            apiJavaParameter.setAnnotations(annotations);
            for (JavaAnnotation annotation : annotations) {
                String annotationName = annotation.getType().getValue();
                if (Objects.nonNull(frameworkAnnotations) && frameworkAnnotations.getRequestBodyAnnotation().getAnnotationName()
                    .equals(annotationName)) {
                    if (Objects.nonNull(builder.getApiConfig().getRequestBodyAdvice())
                        && Objects.isNull(javaMethod.getTagByName(IGNORE_REQUEST_BODY_ADVICE))) {
                        String requestBodyAdvice = builder.getApiConfig().getRequestBodyAdvice().getClassName();
                        fullTypeName = requestBodyAdvice;
                        genericCanonicalName = requestBodyAdvice + "<" + genericCanonicalName + ">";
                    }
                }
            }
            if (JavaClassValidateUtil.isCollection(fullTypeName) || JavaClassValidateUtil.isArray(fullTypeName)) {
                if (JavaClassValidateUtil.isCollection(genericCanonicalName)) {
                    genericCanonicalName = genericCanonicalName + "<T>";
                }
            }
            apiJavaParameter.setGenericCanonicalName(genericCanonicalName);
            apiJavaParameter.setFullyQualifiedName(fullTypeName);
            apiJavaParameterList.add(apiJavaParameter);
        }
        return apiJavaParameterList;
    }

    default String getRewriteClassName(Map<String, String> replacementMap, String fullTypeName, String commentClass) {
        String rewriteClassName;
        if (Objects.nonNull(commentClass) && !DocGlobalConstants.NO_COMMENTS_FOUND.equals(commentClass)) {
            String[] comments = commentClass.split("\\|");
            if (comments.length < 1) {
                return replacementMap.get(fullTypeName);
            }
            rewriteClassName = comments[comments.length - 1];
            if (JavaClassValidateUtil.isClassName(rewriteClassName)) {
                return rewriteClassName;
            }
        }
        return replacementMap.get(fullTypeName);
    }

    default Set<String> ignoreParamsSets(JavaMethod method) {
        Set<String> ignoreSets = new HashSet<>();
        DocletTag ignoreParam = method.getTagByName(DocTags.IGNORE_PARAMS);
        if (Objects.nonNull(ignoreParam)) {
            String[] igParams = ignoreParam.getValue().split(" ");
            Collections.addAll(ignoreSets, igParams);
        }
        return ignoreSets;
    }

    boolean ignoreReturnObject(String typeName, List<String> ignoreParams);
}
