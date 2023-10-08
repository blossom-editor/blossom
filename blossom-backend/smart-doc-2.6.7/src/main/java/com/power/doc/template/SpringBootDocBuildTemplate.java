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

import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.*;
import com.power.doc.handler.SpringMVCRequestHeaderHandler;
import com.power.doc.handler.SpringMVCRequestMappingHandler;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDoc;
import com.power.doc.model.ApiReqParam;
import com.power.doc.model.annotation.*;
import com.power.doc.model.request.RequestMapping;
import com.power.doc.utils.JavaClassValidateUtil;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yu 2019/12/21.
 */
public class SpringBootDocBuildTemplate implements IDocBuildTemplate<ApiDoc>, IRestDocTemplate {

    @Override
    public List<ApiDoc> getApiData(ProjectDocConfigBuilder projectBuilder) {
        ApiConfig apiConfig = projectBuilder.getApiConfig();
        List<ApiReqParam> configApiReqParams = Stream.of(apiConfig.getRequestHeaders(), apiConfig.getRequestParams()).filter(Objects::nonNull)
                .flatMap(Collection::stream).collect(Collectors.toList());
        FrameworkAnnotations frameworkAnnotations = registeredAnnotations();
        List<ApiDoc> apiDocList = this.processApiData(projectBuilder, frameworkAnnotations,
                configApiReqParams, new SpringMVCRequestMappingHandler(), new SpringMVCRequestHeaderHandler());
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
    public FrameworkAnnotations registeredAnnotations() {
        FrameworkAnnotations annotations = FrameworkAnnotations.builder();
        HeaderAnnotation headerAnnotation = HeaderAnnotation.builder()
                .setAnnotationName(SpringMvcAnnotations.REQUEST_HERDER)
                .setValueProp(DocAnnotationConstants.VALUE_PROP)
                .setDefaultValueProp(DocAnnotationConstants.DEFAULT_VALUE_PROP)
                .setRequiredProp(DocAnnotationConstants.REQUIRED_PROP);
        // add header annotation
        annotations.setHeaderAnnotation(headerAnnotation);

        // add entry annotation
        Map<String, EntryAnnotation> entryAnnotations = new HashMap<>();
        EntryAnnotation controllerAnnotation = EntryAnnotation.builder()
                .setAnnotationName(SpringMvcAnnotations.CONTROLLER)
                .setAnnotationFullyName(SpringMvcAnnotations.CONTROLLER);
        entryAnnotations.put(controllerAnnotation.getAnnotationName(), controllerAnnotation);

        EntryAnnotation restController = EntryAnnotation.builder()
                .setAnnotationName(SpringMvcAnnotations.REST_CONTROLLER);
        entryAnnotations.put(restController.getAnnotationName(), restController);
        annotations.setEntryAnnotations(entryAnnotations);

        // add request body annotation
        RequestBodyAnnotation bodyAnnotation = RequestBodyAnnotation.builder()
                .setAnnotationName(SpringMvcAnnotations.REQUEST_BODY)
                .setAnnotationFullyName(SpringMvcAnnotations.REQUEST_BODY_FULLY);
        annotations.setRequestBodyAnnotation(bodyAnnotation);

        // request param annotation
        RequestParamAnnotation requestAnnotation = RequestParamAnnotation.builder()
                .setAnnotationName(SpringMvcAnnotations.REQUEST_PARAM)
                .setDefaultValueProp(DocAnnotationConstants.DEFAULT_VALUE_PROP)
                .setRequiredProp(DocAnnotationConstants.REQUIRED_PROP);
        annotations.setRequestParamAnnotation(requestAnnotation);

        // add path variable annotation
        PathVariableAnnotation pathVariableAnnotation = PathVariableAnnotation.builder()
                .setAnnotationName(SpringMvcAnnotations.PATH_VARIABLE)
                .setDefaultValueProp(DocAnnotationConstants.DEFAULT_VALUE_PROP)
                .setRequiredProp(DocAnnotationConstants.REQUIRED_PROP);
        annotations.setPathVariableAnnotation(pathVariableAnnotation);

        // add mapping annotations
        Map<String, MappingAnnotation> mappingAnnotations = new HashMap<>();

        MappingAnnotation requestMappingAnnotation = MappingAnnotation.builder()
                .setAnnotationName(SpringMvcAnnotations.REQUEST_MAPPING)
                .setConsumesProp("consumes")
                .setProducesProp("produces")
                .setMethodProp("method")
                .setParamsProp("params")
                .setScope("class", "method")
                .setPathProps(DocAnnotationConstants.VALUE_PROP, DocAnnotationConstants.NAME_PROP, DocAnnotationConstants.PATH_PROP);
        mappingAnnotations.put(requestMappingAnnotation.getAnnotationName(), requestMappingAnnotation);

        MappingAnnotation postMappingAnnotation = MappingAnnotation.builder()
                .setAnnotationName(SpringMvcAnnotations.POST_MAPPING)
                .setConsumesProp("consumes")
                .setProducesProp("produces")
                .setMethodProp("method")
                .setParamsProp("params")
                .setMethodType(Methods.POST.getValue())
                .setPathProps(DocAnnotationConstants.VALUE_PROP, DocAnnotationConstants.NAME_PROP, DocAnnotationConstants.PATH_PROP);
        mappingAnnotations.put(postMappingAnnotation.getAnnotationName(), postMappingAnnotation);

        MappingAnnotation getMappingAnnotation = MappingAnnotation.builder()
                .setAnnotationName(SpringMvcAnnotations.GET_MAPPING)
                .setConsumesProp("consumes")
                .setProducesProp("produces")
                .setMethodProp("method")
                .setParamsProp("params")
                .setMethodType(Methods.GET.getValue())
                .setPathProps(DocAnnotationConstants.VALUE_PROP, DocAnnotationConstants.NAME_PROP, DocAnnotationConstants.PATH_PROP);
        mappingAnnotations.put(getMappingAnnotation.getAnnotationName(), getMappingAnnotation);

        MappingAnnotation putMappingAnnotation = MappingAnnotation.builder()
                .setAnnotationName(SpringMvcAnnotations.PUT_MAPPING)
                .setConsumesProp("consumes")
                .setProducesProp("produces")
                .setParamsProp("params")
                .setMethodProp("method")
                .setMethodType(Methods.PUT.getValue())
                .setPathProps(DocAnnotationConstants.VALUE_PROP, DocAnnotationConstants.NAME_PROP, DocAnnotationConstants.PATH_PROP);
        mappingAnnotations.put(putMappingAnnotation.getAnnotationName(), putMappingAnnotation);

        MappingAnnotation patchMappingAnnotation = MappingAnnotation.builder()
                .setAnnotationName(SpringMvcAnnotations.PATCH_MAPPING)
                .setConsumesProp("consumes")
                .setProducesProp("produces")
                .setMethodProp("method")
                .setParamsProp("params")
                .setMethodType(Methods.PATCH.getValue())
                .setPathProps(DocAnnotationConstants.VALUE_PROP, DocAnnotationConstants.NAME_PROP, DocAnnotationConstants.PATH_PROP);
        mappingAnnotations.put(patchMappingAnnotation.getAnnotationName(), patchMappingAnnotation);

        MappingAnnotation deleteMappingAnnotation = MappingAnnotation.builder()
                .setAnnotationName(SpringMvcAnnotations.DELETE_MAPPING)
                .setConsumesProp("consumes")
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

    @Override
    public boolean isEntryPoint(JavaClass javaClass, FrameworkAnnotations frameworkAnnotations) {
        if (javaClass.isAnnotation() || javaClass.isEnum()) {
            return false;
        }
        // use custom doc tag to support Feign.
        List<DocletTag> docletTags = javaClass.getTags();
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
        return SpringMvcRequestAnnotationsEnum.listSpringMvcRequestAnnotations();
    }

    @Override
    public void requestMappingPostProcess(JavaClass javaClass, JavaMethod method, RequestMapping requestMapping) {
        // do nothing
    }

    @Override
    public boolean ignoreMvcParamWithAnnotation(String annotation) {
        return JavaClassValidateUtil.ignoreSpringMvcParamWithAnnotation(annotation);
    }

}
