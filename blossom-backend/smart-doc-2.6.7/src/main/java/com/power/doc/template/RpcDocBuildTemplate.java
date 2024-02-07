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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.power.common.util.StringUtil;
import com.power.common.util.ValidateUtil;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.DocAnnotationConstants;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.DocTags;
import com.power.doc.constants.DubboAnnotationConstants;
import com.power.doc.helper.ParamsBuildHelper;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiParam;
import com.power.doc.model.DocJavaMethod;
import com.power.doc.model.RpcJavaMethod;
import com.power.doc.model.annotation.FrameworkAnnotations;
import com.power.doc.model.rpc.RpcApiDoc;
import com.power.doc.utils.ApiParamTreeUtil;
import com.power.doc.utils.DocClassUtil;
import com.power.doc.utils.DocUtil;
import com.power.doc.utils.JavaClassUtil;
import com.power.doc.utils.JavaClassValidateUtil;
import com.power.doc.utils.JavaFieldUtil;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaType;

import static com.power.doc.constants.DocTags.DEPRECATED;
import static com.power.doc.constants.DocTags.IGNORE;

/**
 * @author yu 2020/1/29.
 */
public class RpcDocBuildTemplate implements IDocBuildTemplate<RpcApiDoc>, IRpcDocTemplate {

    /**
     * api index
     */
    private final AtomicInteger atomicInteger = new AtomicInteger(1);

    @Override
    public List<RpcApiDoc> getApiData(ProjectDocConfigBuilder projectBuilder) {
        ApiConfig apiConfig = projectBuilder.getApiConfig();
        List<RpcApiDoc> apiDocList = new ArrayList<>();
        int order = 0;
        boolean setCustomOrder = false;
        for (JavaClass cls : projectBuilder.getJavaProjectBuilder().getClasses()) {
            if (StringUtil.isNotEmpty(apiConfig.getPackageFilters())) {
                // check package
                if (!DocUtil.isMatch(apiConfig.getPackageFilters(), cls.getCanonicalName())) {
                    continue;
                }
            }
            DocletTag ignoreTag = cls.getTagByName(DocTags.IGNORE);
            if (!isEntryPoint(cls, null) || Objects.nonNull(ignoreTag)) {
                continue;
            }
            String strOrder = JavaClassUtil.getClassTagsValue(cls, DocTags.ORDER, Boolean.TRUE);
            order++;
            if (ValidateUtil.isNonNegativeInteger(strOrder)) {
                order = Integer.parseInt(strOrder);
                setCustomOrder = true;
            }
            List<RpcJavaMethod> apiMethodDocs = buildServiceMethod(cls, apiConfig, projectBuilder);
            this.handleJavaApiDoc(cls, apiDocList, apiMethodDocs, order, projectBuilder);
        }
        // sort
        if (apiConfig.isSortByTitle()) {
            Collections.sort(apiDocList);
        } else if (setCustomOrder) {
            // while set custom oder
            return apiDocList.stream()
                .sorted(Comparator.comparing(RpcApiDoc::getOrder))
                .peek(p -> p.setOrder(atomicInteger.getAndAdd(1))).collect(Collectors.toList());
        }
        return apiDocList;
    }

    public boolean ignoreReturnObject(String typeName, List<String> ignoreParams) {
        return false;
    }

    private List<RpcJavaMethod> buildServiceMethod(final JavaClass cls, ApiConfig apiConfig, ProjectDocConfigBuilder projectBuilder) {
        String clazName = cls.getCanonicalName();
        List<JavaMethod> methods = cls.getMethods();
        List<RpcJavaMethod> methodDocList = new ArrayList<>(methods.size());
        int methodOrder = 0;
        for (JavaMethod method : methods) {
            if (method.isPrivate()) {
                continue;
            }
            if (Objects.nonNull(method.getTagByName(IGNORE))) {
                continue;
            }
            if (StringUtil.isEmpty(method.getComment()) && apiConfig.isStrict()) {
                throw new RuntimeException("Unable to find comment for method " + method.getName() + " in " + cls.getCanonicalName());
            }
            methodOrder++;
            RpcJavaMethod apiMethodDoc = convertToRpcJavaMethod(apiConfig,method);
            apiMethodDoc.setOrder(methodOrder);
            String methodUid = DocUtil.generateId(clazName + method.getName() + methodOrder);
            apiMethodDoc.setMethodId(methodUid);
            // build request params
            List<ApiParam> requestParams = requestParams(method, projectBuilder, new AtomicInteger(0));
            // build response params
            List<ApiParam> responseParams = buildReturnApiParams(DocJavaMethod.builder().setJavaMethod(method), projectBuilder);
            if (apiConfig.isParamsDataToTree()) {
                apiMethodDoc.setRequestParams(ApiParamTreeUtil.apiParamToTree(requestParams));
                apiMethodDoc.setResponseParams(ApiParamTreeUtil.apiParamToTree(responseParams));
            } else {
                apiMethodDoc.setRequestParams(requestParams);
                apiMethodDoc.setResponseParams(responseParams);
            }
            methodDocList.add(apiMethodDoc);

        }
        return methodDocList;
    }

    private List<ApiParam> requestParams(final JavaMethod javaMethod, ProjectDocConfigBuilder builder, AtomicInteger atomicInteger) {
        boolean isStrict = builder.getApiConfig().isStrict();
        boolean isShowJavaType = builder.getApiConfig().getShowJavaType();
        String className = javaMethod.getDeclaringClass().getCanonicalName();
        Map<String, String> paramTagMap = DocUtil.getCommentsByTag(javaMethod, DocTags.PARAM, className);
        List<JavaParameter> parameterList = javaMethod.getParameters();
        if (parameterList.size() < 1) {
            return null;
        }
        List<ApiParam> paramList = new ArrayList<>();
        for (JavaParameter parameter : parameterList) {
            boolean required = false;
            String paramName = parameter.getName();
            String typeName = parameter.getType().getGenericCanonicalName();
            String simpleName = parameter.getType().getValue().toLowerCase();
            String fullTypeName = parameter.getType().getFullyQualifiedName();
            String paramPre = paramName + ".";
            if (!paramTagMap.containsKey(paramName) && JavaClassValidateUtil.isPrimitive(fullTypeName) && isStrict) {
                throw new RuntimeException("ERROR: Unable to find javadoc @param for actual param \""
                    + paramName + "\" in method " + javaMethod.getName() + " from " + className);
            }
            StringBuilder comment = new StringBuilder(this.paramCommentResolve(paramTagMap.get(paramName)));
            String mockValue = JavaFieldUtil.createMockValue(paramTagMap, paramName, typeName, typeName);
            JavaClass javaClass = builder.getJavaProjectBuilder().getClassByName(fullTypeName);
            List<JavaAnnotation> annotations = parameter.getAnnotations();
            for (JavaAnnotation a : annotations) {
                if (JavaClassValidateUtil.isJSR303Required(a.getType().getValue())) {
                    required = true;
                }
            }
            comment.append(JavaFieldUtil.getJsrComment(annotations));
            Set<String> groupClasses = JavaClassUtil.getParamGroupJavaClass(annotations, builder.getJavaProjectBuilder());
            if (JavaClassValidateUtil.isCollection(fullTypeName) || JavaClassValidateUtil.isArray(fullTypeName)) {
                if (JavaClassValidateUtil.isCollection(typeName)) {
                    typeName = typeName + "<T>";
                }
                String[] gicNameArr = DocClassUtil.getSimpleGicName(typeName);
                String gicName = gicNameArr[0];
                if (JavaClassValidateUtil.isArray(gicName)) {
                    gicName = gicName.substring(0, gicName.indexOf("["));
                }
                if (JavaClassValidateUtil.isPrimitive(gicName)) {
                    String processedType = isShowJavaType ?
                        JavaClassUtil.getClassSimpleName(typeName) : DocClassUtil.processTypeNameForParams(simpleName);
                    ApiParam param = ApiParam.of().setId(atomicInteger.incrementAndGet()).setField(paramName)
                        .setDesc(comment + "   (children type : " + gicName + ")")
                        .setRequired(required)
                        .setType(processedType);
                    paramList.add(param);
                } else {
                    paramList.addAll(ParamsBuildHelper.buildParams(gicNameArr[0], paramPre, 0, "true",
                        Boolean.FALSE, new HashMap<>(), builder, groupClasses, 0, Boolean.FALSE, atomicInteger));
                }
            } else if (JavaClassValidateUtil.isPrimitive(fullTypeName)) {
                ApiParam param = ApiParam.of().setId(atomicInteger.incrementAndGet()).setField(paramName)
                    .setType(JavaClassUtil.getClassSimpleName(typeName))
                    .setDesc(comment.toString())
                    .setRequired(required)
                    .setMaxLength(JavaFieldUtil.getParamMaxLength(parameter.getAnnotations()))
                    .setValue(mockValue)
                    .setVersion(DocGlobalConstants.DEFAULT_VERSION);
                paramList.add(param);
            } else if (JavaClassValidateUtil.isMap(fullTypeName)) {
                if (JavaClassValidateUtil.isMap(typeName)) {
                    ApiParam apiParam = ApiParam.of().setId(atomicInteger.incrementAndGet()).setField(paramName).setType(typeName)
                        .setDesc(comment.toString()).setRequired(required).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                    paramList.add(apiParam);
                    continue;
                }
                String[] gicNameArr = DocClassUtil.getSimpleGicName(typeName);
                paramList.addAll(ParamsBuildHelper.buildParams(gicNameArr[1], paramPre, 0, "true",
                    Boolean.FALSE, new HashMap<>(), builder, groupClasses, 0, Boolean.FALSE, atomicInteger));
            } else if (javaClass.isEnum()) {
                ApiParam param = ApiParam.of().setId(atomicInteger.incrementAndGet()).setField(paramName)
                    .setType("Enum").setRequired(required).setDesc(comment.toString()).setVersion(DocGlobalConstants.DEFAULT_VERSION);
                paramList.add(param);
            } else {
                paramList.addAll(ParamsBuildHelper.buildParams(typeName, paramPre, 0, "true",
                    Boolean.FALSE, new HashMap<>(), builder, groupClasses, 0, Boolean.FALSE, atomicInteger));
            }
        }
        return paramList;
    }

    public boolean isEntryPoint(JavaClass cls, FrameworkAnnotations frameworkAnnotations) {
        // Exclude DubboSwaggerService from dubbo 2.7.x
        if (DocGlobalConstants.DUBBO_SWAGGER.equals(cls.getCanonicalName())) {
            return false;
        }
        List<JavaAnnotation> classAnnotations = cls.getAnnotations();
        for (JavaAnnotation annotation : classAnnotations) {
            String name = annotation.getType().getCanonicalName();
            if (DubboAnnotationConstants.SERVICE.equals(name)
                || DubboAnnotationConstants.DUBBO_SERVICE.equals(name)
                || DubboAnnotationConstants.ALI_DUBBO_SERVICE.equals(name)) {
                return true;
            }
        }
        List<DocletTag> docletTags = cls.getTags();
        for (DocletTag docletTag : docletTags) {
            String value = docletTag.getName();
            if (DocTags.DUBBO.equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public FrameworkAnnotations registeredAnnotations() {
        return null;
    }

    private void handleJavaApiDoc(JavaClass cls, List<RpcApiDoc> apiDocList, List<RpcJavaMethod> apiMethodDocs,
        int order, ProjectDocConfigBuilder builder) {
        String className = cls.getCanonicalName();
        String shortName = cls.getName();
        String comment = cls.getComment();
        List<JavaType> javaTypes = cls.getImplements();
        if (javaTypes.size() >= 1 && !cls.isInterface()) {
            JavaType javaType = javaTypes.get(0);
            className = javaType.getCanonicalName();
            shortName = className;
            JavaClass javaClass = builder.getClassByName(className);
            if (StringUtil.isEmpty(comment) && Objects.nonNull(javaClass)) {
                comment = javaClass.getComment();
            }
        }
        RpcApiDoc apiDoc = new RpcApiDoc();
        apiDoc.setOrder(order);
        apiDoc.setName(className);
        apiDoc.setShortName(shortName);
        apiDoc.setAlias(className);
        apiDoc.setUri(builder.getServerUrl() + "/" + className);
        apiDoc.setProtocol("dubbo");
        if (builder.getApiConfig().isMd5EncryptedHtmlName()) {
            String name = DocUtil.generateId(apiDoc.getName());
            apiDoc.setAlias(name);
        }
        apiDoc.setDesc(DocUtil.getEscapeAndCleanComment(comment));
        apiDoc.setList(apiMethodDocs);
        List<DocletTag> docletTags = cls.getTags();
        List<String> authorList = new ArrayList<>();
        for (DocletTag docletTag : docletTags) {
            String name = docletTag.getName();
            if (DocTags.VERSION.equals(name)) {
                apiDoc.setVersion(docletTag.getValue());
            }
            if (DocTags.AUTHOR.equals(name)) {
                authorList.add(docletTag.getValue());
            }
        }
        apiDoc.setAuthor(String.join(", ", authorList));
        apiDocList.add(apiDoc);
    }
}
