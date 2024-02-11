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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.DocAnnotationConstants;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.DocTags;
import com.power.doc.constants.Methods;
import com.power.doc.constants.SolonAnnotations;
import com.power.doc.constants.SolonRequestAnnotationsEnum;
import com.power.doc.handler.SolonRequestHeaderHandler;
import com.power.doc.handler.SolonRequestMappingHandler;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDoc;
import com.power.doc.model.ApiReqParam;
import com.power.doc.model.annotation.EntryAnnotation;
import com.power.doc.model.annotation.FrameworkAnnotations;
import com.power.doc.model.annotation.HeaderAnnotation;
import com.power.doc.model.annotation.MappingAnnotation;
import com.power.doc.model.annotation.PathVariableAnnotation;
import com.power.doc.model.annotation.RequestBodyAnnotation;
import com.power.doc.model.annotation.RequestParamAnnotation;
import com.power.doc.model.request.RequestMapping;
import com.power.doc.utils.JavaClassValidateUtil;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;

/**
 * @author noear 2022/2/19 created
 */
public class SolonDocBuildTemplate implements IDocBuildTemplate<ApiDoc>, IRestDocTemplate {

    @Override
    public List<ApiDoc> getApiData(ProjectDocConfigBuilder projectBuilder) {
        ApiConfig apiConfig = projectBuilder.getApiConfig();
        List<ApiReqParam> configApiReqParams = Stream.of(apiConfig.getRequestHeaders(), apiConfig.getRequestParams()).filter(Objects::nonNull)
            .flatMap(Collection::stream).collect(Collectors.toList());
        FrameworkAnnotations frameworkAnnotations = registeredAnnotations();
        List<ApiDoc> apiDocList = processApiData(projectBuilder, frameworkAnnotations, configApiReqParams,
            new SolonRequestMappingHandler(), new SolonRequestHeaderHandler());
        // sort
        if (apiConfig.isSortByTitle()) {
            Collections.sort(apiDocList);
        }
        return apiDocList;
    }


    @Override
    public boolean ignoreReturnObject(String typeName, List<String> ignoreParams) {
        return JavaClassValidateUtil.isMvcIgnoreParams(typeName, ignoreParams);
    }

    @Override
    public boolean isEntryPoint(JavaClass cls, FrameworkAnnotations frameworkAnnotations) {
        for (JavaAnnotation annotation : cls.getAnnotations()) {
            String name = annotation.getType().getValue();
            if (SolonAnnotations.REMOTING.equals(name)) {
                return true;
            }
        }
        // use custom doc tag to support Feign.
        List<DocletTag> docletTags = cls.getTags();
        for (DocletTag docletTag : docletTags) {
            String value = docletTag.getName();
            if (DocTags.REST_API.equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> listMvcRequestAnnotations() {
        return SolonRequestAnnotationsEnum.listMvcRequestAnnotations();
    }

    @Override
    public void requestMappingPostProcess(JavaClass javaClass, JavaMethod method, RequestMapping requestMapping) {
        if (Objects.isNull(requestMapping)) {
            return;
        }
        if (javaClass.isAnnotation() || javaClass.isEnum()) {
            return;
        }
        boolean isRemote = false;
        for (JavaAnnotation annotation : javaClass.getAnnotations()) {
            String name = annotation.getType().getValue();
            if (SolonAnnotations.REMOTING.equals(name)) {
                isRemote = true;
            }
        }
        if (isRemote) {
            requestMapping.setMethodType(Methods.POST.getValue());
            String shortUrl = requestMapping.getShortUrl();
            String mediaType = requestMapping.getMediaType();
            if (shortUrl == null) {
                requestMapping.setShortUrl(method.getName());
            }
            if (mediaType == null) {
                requestMapping.setMediaType("text/json");
            }
        }
    }

    @Override
    public boolean ignoreMvcParamWithAnnotation(String annotation) {
        return JavaClassValidateUtil.ignoreSolonMvcParamWithAnnotation(annotation);
    }


    @Override
    public FrameworkAnnotations registeredAnnotations() {
        FrameworkAnnotations annotations = FrameworkAnnotations.builder();
        HeaderAnnotation headerAnnotation = HeaderAnnotation.builder()
            .setAnnotationName(SolonAnnotations.REQUEST_HERDER)
            .setValueProp(DocAnnotationConstants.VALUE_PROP)
            .setDefaultValueProp(DocAnnotationConstants.DEFAULT_VALUE_PROP)
            .setRequiredProp(DocAnnotationConstants.REQUIRED_PROP);
        // add header annotation
        annotations.setHeaderAnnotation(headerAnnotation);

        // add entry annotation
        Map<String, EntryAnnotation> entryAnnotations = new HashMap<>();
        EntryAnnotation controllerAnnotation = EntryAnnotation.builder()
            .setAnnotationName(SolonAnnotations.CONTROLLER)
            .setAnnotationFullyName(SolonAnnotations.CONTROLLER);
        entryAnnotations.put(controllerAnnotation.getAnnotationName(), controllerAnnotation);

        EntryAnnotation remoteController = EntryAnnotation.builder()
            .setAnnotationName(SolonAnnotations.REMOTING);
        entryAnnotations.put(remoteController.getAnnotationName(), remoteController);

        EntryAnnotation componentController = EntryAnnotation.builder()
            .setAnnotationName(SolonAnnotations.COMPONENT);
        entryAnnotations.put(componentController.getAnnotationName(), componentController);

        annotations.setEntryAnnotations(entryAnnotations);

        // add request body annotation
        RequestBodyAnnotation bodyAnnotation = RequestBodyAnnotation.builder()
            .setAnnotationName(SolonAnnotations.REQUEST_BODY)
            .setAnnotationFullyName(SolonAnnotations.REQUEST_BODY_FULLY);
        annotations.setRequestBodyAnnotation(bodyAnnotation);

        // request param annotation
        RequestParamAnnotation requestAnnotation = RequestParamAnnotation.builder()
            .setAnnotationName(SolonAnnotations.REQUEST_PARAM)
            .setDefaultValueProp(DocAnnotationConstants.DEFAULT_VALUE_PROP)
            .setRequiredProp(DocAnnotationConstants.REQUIRED_PROP);
        annotations.setRequestParamAnnotation(requestAnnotation);

        // add path variable annotation
        PathVariableAnnotation pathVariableAnnotation = PathVariableAnnotation.builder()
            .setAnnotationName(SolonAnnotations.PATH_VAR)
            .setDefaultValueProp(DocAnnotationConstants.DEFAULT_VALUE_PROP)
            .setRequiredProp(DocAnnotationConstants.REQUIRED_PROP);
        annotations.setPathVariableAnnotation(pathVariableAnnotation);

        // add mapping annotations
        Map<String, MappingAnnotation> mappingAnnotations = new HashMap<>();

        MappingAnnotation requestMappingAnnotation = MappingAnnotation.builder()
            .setAnnotationName(SolonAnnotations.REQUEST_MAPPING)
            .setProducesProp("produces")
            .setMethodProp("method")
            .setParamsProp("params")
            .setScope("class", "method")
            .setPathProps(DocAnnotationConstants.VALUE_PROP, DocAnnotationConstants.NAME_PROP, DocAnnotationConstants.PATH_PROP);
        mappingAnnotations.put(requestMappingAnnotation.getAnnotationName(), requestMappingAnnotation);

        MappingAnnotation postMappingAnnotation = MappingAnnotation.builder()
            .setAnnotationName(SolonAnnotations.POST_MAPPING)
            .setProducesProp("produces")
            .setMethodProp("method")
            .setParamsProp("params")
            .setMethodType(Methods.POST.getValue())
            .setPathProps(DocAnnotationConstants.VALUE_PROP, DocAnnotationConstants.NAME_PROP, DocAnnotationConstants.PATH_PROP);
        mappingAnnotations.put(postMappingAnnotation.getAnnotationName(), postMappingAnnotation);

        MappingAnnotation getMappingAnnotation = MappingAnnotation.builder()
            .setAnnotationName(SolonAnnotations.GET_MAPPING)
            .setProducesProp("produces")
            .setMethodProp("method")
            .setParamsProp("params")
            .setMethodType(Methods.GET.getValue())
            .setPathProps(DocAnnotationConstants.VALUE_PROP, DocAnnotationConstants.NAME_PROP, DocAnnotationConstants.PATH_PROP);
        mappingAnnotations.put(getMappingAnnotation.getAnnotationName(), getMappingAnnotation);

        MappingAnnotation putMappingAnnotation = MappingAnnotation.builder()
            .setAnnotationName(SolonAnnotations.PUT_MAPPING)
            .setProducesProp("produces")
            .setParamsProp("params")
            .setMethodProp("method")
            .setMethodType(Methods.PUT.getValue())
            .setPathProps(DocAnnotationConstants.VALUE_PROP, DocAnnotationConstants.NAME_PROP, DocAnnotationConstants.PATH_PROP);
        mappingAnnotations.put(putMappingAnnotation.getAnnotationName(), putMappingAnnotation);

        MappingAnnotation patchMappingAnnotation = MappingAnnotation.builder()
            .setAnnotationName(SolonAnnotations.PATCH_MAPPING)
            .setProducesProp("produces")
            .setMethodProp("method")
            .setParamsProp("params")
            .setMethodType(Methods.PATCH.getValue())
            .setPathProps(DocAnnotationConstants.VALUE_PROP, DocAnnotationConstants.NAME_PROP, DocAnnotationConstants.PATH_PROP);
        mappingAnnotations.put(patchMappingAnnotation.getAnnotationName(), patchMappingAnnotation);

        MappingAnnotation deleteMappingAnnotation = MappingAnnotation.builder()
            .setAnnotationName(SolonAnnotations.DELETE_MAPPING)
            .setProducesProp("produces")
            .setMethodProp("method")
            .setParamsProp("params")
            .setMethodType(Methods.DELETE.getValue())
            .setPathProps(DocAnnotationConstants.VALUE_PROP, DocAnnotationConstants.NAME_PROP, DocAnnotationConstants.PATH_PROP);
        mappingAnnotations.put(deleteMappingAnnotation.getAnnotationName(), deleteMappingAnnotation);

        MappingAnnotation feignClientAnnotation = MappingAnnotation.builder()
            .setAnnotationName(DocGlobalConstants.FEIGN_CLIENT)
            .setAnnotationFullyName(DocGlobalConstants.FEIGN_CLIENT_FULLY);
        mappingAnnotations.put(feignClientAnnotation.getAnnotationName(), feignClientAnnotation);

        annotations.setMappingAnnotations(mappingAnnotations);
        return annotations;
    }
}
