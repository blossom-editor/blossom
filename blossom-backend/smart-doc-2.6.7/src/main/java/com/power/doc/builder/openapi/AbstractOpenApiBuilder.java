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

package com.power.doc.builder.openapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.power.doc.builder.DocBuilderTemplate;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.factory.BuildTemplateFactory;
import com.power.doc.model.*;
import com.power.doc.model.openapi.OpenApiTag;
import com.power.doc.template.IDocBuildTemplate;
import com.power.doc.utils.DocUtil;
import com.power.doc.utils.OpenApiSchemaUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;

import static com.power.doc.constants.DocGlobalConstants.*;


/**
 * @author xingzi
 * Date 2022/10/12 18:49
 */
@SuppressWarnings("all")
public abstract class AbstractOpenApiBuilder {

    private String componentKey;

    public String getComponentKey() {
        return componentKey;
    }

    public void setComponentKey(String componentKey) {
        this.componentKey = componentKey;
    }

    abstract String getModuleName();

    /**
     * Create OpenAPI definition
     *
     * @param apiConfig  Configuration of smart-doc
     * @param apiDocList List of API DOC
     */
    abstract void openApiCreate(ApiConfig apiConfig, List<ApiDoc> apiDocList);

    /**
     * Build request
     *
     * @param apiConfig    Configuration of smart-doc
     * @param apiMethodDoc Data of method
     * @param apiDoc       singe api doc
     * @return Map of request urls
     */
    abstract Map<String, Object> buildPathUrlsRequest(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, ApiDoc apiDoc);

    /**
     * response body
     *
     * @param apiMethodDoc ApiMethodDoc
     */
    abstract Map<String, Object> buildResponsesBody(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc);

    protected static final Map<String, String> STRING_COMPONENT = new HashMap<>();

    static {
        STRING_COMPONENT.put("type", "string");
        STRING_COMPONENT.put("format", "string");
    }

    /**
     * Build openapi paths
     *
     * @param apiConfig  Configuration of smart-doc
     * @param apiDocList List of API DOC
     * @param tags       tags
     */
    public Map<String, Object> buildPaths(ApiConfig apiConfig, List<ApiDoc> apiDocList, Set<OpenApiTag> tags) {
        Map<String, Object> pathMap = new HashMap<>(500);
        Set<ApiMethodDoc> methodDocs = DocMapping.METHOD_DOCS;
        for (ApiMethodDoc methodDoc : methodDocs) {
            String path = methodDoc.getPath();
            Map<String, Object> request = buildPathUrls(apiConfig, methodDoc, methodDoc.getClazzDoc());
            if (!pathMap.containsKey(path)) {
                pathMap.put(path, request);
            } else {
                Map<String, Object> oldRequest = (Map<String, Object>) pathMap.get(path);
                oldRequest.putAll(request);
            }
        }
        for (Map.Entry<String, TagDoc> docEntry : DocMapping.TAG_DOC.entrySet()) {
            String tag = docEntry.getKey();
            tags.add(OpenApiTag.of(tag, tag));
        }
        return pathMap;
    }

    /**
     * Build path urls
     *
     * @param apiConfig    ApiConfig
     * @param apiMethodDoc Method
     * @param apiDoc       ApiDoc
     */
    public Map<String, Object> buildPathUrls(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, ApiDoc apiDoc) {
        Map<String, Object> request = new HashMap<>(4);
        request.put(apiMethodDoc.getType().toLowerCase(), buildPathUrlsRequest(apiConfig, apiMethodDoc, apiDoc));
        return request;
    }

    /**
     * Build content for responses and requestBody
     *
     * @param apiConfig    ApiConfig
     * @param apiMethodDoc ApiMethodDoc
     * @param isRep        is response
     */
    public Map<String, Object> buildContent(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, boolean isRep) {
        Map<String, Object> content = new HashMap<>(8);
        String contentType = apiMethodDoc.getContentType();
        if (isRep) {
            contentType = "*/*";
        }
        content.put(contentType, buildContentBody(apiConfig, apiMethodDoc, isRep));
        return content;

    }

    /**
     * Build data of content
     *
     * @param apiConfig    ApiConfig
     * @param apiMethodDoc ApiMethodDoc
     * @param isRep        is response
     */
    public Map<String, Object> buildContentBody(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, boolean isRep) {
        Map<String, Object> content = new HashMap<>(8);
        if (Objects.nonNull(apiMethodDoc.getReturnSchema()) && isRep) {
            content.put("schema", apiMethodDoc.getReturnSchema());
        } else if (!isRep && Objects.nonNull(apiMethodDoc.getRequestSchema())) {
            content.put("schema", apiMethodDoc.getRequestSchema());
        } else {
            content.put("schema", buildBodySchema(apiMethodDoc, isRep));
        }

        if (OPENAPI_2_COMPONENT_KRY.equals(componentKey) && !isRep) {
            content.put("name", apiMethodDoc.getName());
        }
        if (OPENAPI_3_COMPONENT_KRY.equals(componentKey) &&
                (!isRep && apiConfig.isRequestExample() || (isRep && apiConfig.isResponseExample()))) {
            content.put("examples", buildBodyExample(apiMethodDoc, isRep));
        }
        return content;

    }

    /**
     * Build schema of Body
     *
     * @param apiMethodDoc ApiMethodDoc
     * @param isRep        is response
     */
    public Map<String, Object> buildBodySchema(ApiMethodDoc apiMethodDoc, boolean isRep) {
        Map<String, Object> schema = new HashMap<>(10);
        Map<String, Object> innerScheme = new HashMap<>(10);
        String requestRef;
        if (apiMethodDoc.getContentType().equals(DocGlobalConstants.URL_CONTENT_TYPE)) {
            requestRef = componentKey + OpenApiSchemaUtil.getClassNameFromParams(apiMethodDoc.getQueryParams(), COMPONENT_REQUEST_SUFFIX);
        } else {
            requestRef = componentKey + OpenApiSchemaUtil.getClassNameFromParams(apiMethodDoc.getRequestParams(), COMPONENT_REQUEST_SUFFIX);
        }
        //remove special characters in url
        String responseRef = componentKey + OpenApiSchemaUtil.getClassNameFromParams(apiMethodDoc.getResponseParams(), COMPONENT_RESPONSE_SUFFIX);
        if (!isRep && CollectionUtil.isNotEmpty(apiMethodDoc.getRequestParams())) {
            if (apiMethodDoc.getIsRequestArray() == 1) {
                schema.put("type", ARRAY);
                innerScheme.put("$ref", requestRef);
                schema.put("items", innerScheme);
            } else {
                schema.put("$ref", requestRef);
            }
        } else {
            if (apiMethodDoc.getIsResponseArray() == 1) {
                schema.put("type", ARRAY);
                innerScheme.put("$ref", responseRef);
                schema.put("items", innerScheme);
            } else if (CollectionUtil.isNotEmpty(apiMethodDoc.getResponseParams())) {
                schema.put("$ref", responseRef);
            }
        }
        return schema;
    }


    /**
     * Build body example
     *
     * @param apiMethodDoc ApiMethodDoc
     * @param isRep        is response
     */
    public static Map<String, Object> buildBodyExample(ApiMethodDoc apiMethodDoc, boolean isRep) {
        Map<String, Object> content = new HashMap<>(8);
        content.put("json", buildExampleData(apiMethodDoc, isRep));
        return content;

    }

    /**
     * Build example data
     *
     * @param apiMethodDoc ApiMethodDoc
     * @param isRep        is response
     */
    public static Map<String, Object> buildExampleData(ApiMethodDoc apiMethodDoc, boolean isRep) {
        Map<String, Object> content = new HashMap<>(8);
        content.put("summary", "test data");
        if (!isRep) {
            content.put("value", StringUtil.isEmpty(
                    apiMethodDoc.getRequestExample().getJsonBody()) ? apiMethodDoc.getRequestExample().getExampleBody() :
                    apiMethodDoc.getRequestExample().getJsonBody());
        } else {
            content.put("value", apiMethodDoc.getResponseUsage());
        }
        return content;

    }

    /**
     * Build request parameters
     *
     * @param apiMethodDoc API data for the method
     */
    abstract List<Map<String, Object>> buildParameters(ApiMethodDoc apiMethodDoc);

    abstract Map<String, Object> getStringParams(ApiParam apiParam, boolean hasItems);

    /**
     * If it is a get request or @PathVariable set the request parameters
     *
     * @param apiParam Parameter information
     * @return parameters schema
     */
    public Map<String, Object> buildParametersSchema(ApiParam apiParam) {
        Map<String, Object> schema = new HashMap<>(10);
        String openApiType = DocUtil.javaTypeToOpenApiTypeConvert(apiParam.getType());
        schema.put("type", openApiType);
        if ("file".equals(apiParam.getType())) {
            schema.put("format", "binary");
            schema.put("type", "string");
        } else if ("object".equals(openApiType)) {
            if ("enum".equals(apiParam.getType())) {
                schema.put("enum", apiParam.getEnumValues());
            }
        } else if (ARRAY.equals(apiParam.getType())) {
            if (CollectionUtil.isNotEmpty(apiParam.getEnumValues())) {
                schema.put("type", "string");
                schema.put("items", apiParam.getEnumValues());
            } else {
                schema.put("type", ARRAY);
                Map<String, String> map = new HashMap<>(4);
                map.put("type", "string");
                map.put("format", "string");
                schema.put("items", map);
            }
        } else {
            schema.put("type", apiParam.getType());
            schema.put("format", "integer");
        }
        return schema;
    }

    /**
     * If the header is included, set the request parameters
     *
     * @param header header
     */
    public static Map<String, Object> buildParametersSchema(ApiReqParam header) {
        Map<String, Object> schema = new HashMap<>(10);
        String openApiType = DocUtil.javaTypeToOpenApiTypeConvert(header.getType());
        schema.put("type", openApiType);
        schema.put("format", "int16".equals(header.getType()) ? "int32" : header.getType());
        return schema;
    }

    /**
     * build response
     *
     * @param apiMethodDoc ApiMethodDoc
     * @return response info
     */
    public Map<String, Object> buildResponses(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc) {
        Map<String, Object> response = new HashMap<>(10);
        response.put("200", buildResponsesBody(apiConfig, apiMethodDoc));
        return response;
    }

    /**
     * component schema
     *
     * @param apiDocs List of ApiDoc
     */
    abstract public Map<String, Object> buildComponentsSchema(List<ApiDoc> apiDocs);

    /**
     * component schema properties
     *
     * @param apiParam list of ApiParam
     */
    public Map<String, Object> buildProperties(List<ApiParam> apiParam, Map<String, Object> component, boolean isResp) {
        Map<String, Object> properties = new HashMap<>();
        Map<String, Object> propertiesData = new LinkedHashMap<>();
        List<String> requiredList = new ArrayList<>();
        if (apiParam != null) {
            int paramsSize = apiParam.size();
            for (ApiParam param : apiParam) {
                if (param.isRequired()) {
                    requiredList.add(param.getField());
                }
                if (param.getType().equals("map") &&StringUtil.isEmpty(param.getClassName())) {
                    continue;
                }
                if (param.isQueryParam() || param.isPathParam()) {
                    continue;
                }
                String field = param.getField();
                propertiesData.put(field, buildPropertiesData(param, component, isResp));
            }
            if (!propertiesData.isEmpty()) {
                properties.put("properties", propertiesData);
            }
            if (!CollectionUtil.isEmpty(requiredList)) {
                properties.put("required", requiredList);
            }
            return properties;
        } else {
            return new HashMap<>();
        }

    }

    /**
     * component schema properties data
     *
     * @param apiParam ApiParam
     */
    private Map<String, Object> buildPropertiesData(ApiParam apiParam, Map<String, Object> component, boolean isResp) {
        Map<String, Object> propertiesData = new HashMap<>();
        String openApiType = DocUtil.javaTypeToOpenApiTypeConvert(apiParam.getType());
        //array object file map
        propertiesData.put("description", apiParam.getDesc());
        if (StringUtil.isNotEmpty(apiParam.getValue())) {
            propertiesData.put("example", StringUtil.removeDoubleQuotes(apiParam.getValue()));
        }

        if (!"object".equals(openApiType)) {
            propertiesData.put("type", openApiType);
            propertiesData.put("format", "int16".equals(apiParam.getType()) ? "int32" : apiParam.getType());
        }
        if ("map".equals(apiParam.getType())) {
            propertiesData.put("type", "object");
            propertiesData.put("description", apiParam.getDesc() + "(map data)");
        }
        if ("array".equals(apiParam.getType())) {
            propertiesData.put("type", "array");
            if (CollectionUtil.isNotEmpty(apiParam.getChildren())) {
                if (!apiParam.isSelfReferenceLoop()) {
                    Map<String, Object> arrayRef = new HashMap<>(4);
                    String suffix = isResp ? COMPONENT_RESPONSE_SUFFIX : COMPONENT_REQUEST_SUFFIX;
                    String childSchemaName = OpenApiSchemaUtil.getClassNameFromParams(apiParam.getChildren(), suffix);
                    if (childSchemaName.contains(OpenApiSchemaUtil.NO_BODY_PARAM)) {
                        propertiesData.put("type", "object");
                        propertiesData.put("description", apiParam.getDesc() + "(object)");
                    } else {
                        component.put(childSchemaName, buildProperties(apiParam.getChildren(), component, isResp));
                        arrayRef.put("$ref", componentKey + childSchemaName);
                        propertiesData.put("items", arrayRef);
                    }
                }
            }
            //基础数据类型
            else {
                Map<String, Object> arrayRef = new HashMap<>(4);
                arrayRef.put("type", "string");
                propertiesData.put("items", arrayRef);
            }
        }
        if ("file".equals(apiParam.getType())) {
            propertiesData.put("type", "string");
            propertiesData.put("format", "binary");
        }
        if ("object".equals(apiParam.getType())) {
            if (CollectionUtil.isNotEmpty(apiParam.getChildren())) {
                propertiesData.put("type", "object");
                propertiesData.put("description", apiParam.getDesc() + "(object)");
                String suffix = isResp ? COMPONENT_RESPONSE_SUFFIX : COMPONENT_REQUEST_SUFFIX;
                if (!apiParam.isSelfReferenceLoop()) {
                    String childSchemaName = OpenApiSchemaUtil.getClassNameFromParams(apiParam.getChildren(), suffix);
                    if (childSchemaName.contains(OpenApiSchemaUtil.NO_BODY_PARAM)) {
                        propertiesData.put("type", "object");
                        propertiesData.put("description", apiParam.getDesc() + "(object)");
                    } else {
                        component.put(childSchemaName, buildProperties(apiParam.getChildren(), component, isResp));
                        propertiesData.put("$ref", componentKey + childSchemaName);
                    }
                }
            } else {
                propertiesData.put("type", "object");
                propertiesData.put("description", apiParam.getDesc() + "(object)");
            }
        }

        return propertiesData;
    }

    /**
     * Get a list of OpenAPI's document data
     *
     * @param config         Configuration of smart-doc
     * @param projectBuilder JavaDocBuilder of QDox
     */
    public List<ApiDoc> getOpenApiDocs(ApiConfig config, JavaProjectBuilder projectBuilder) {
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config, false);
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, projectBuilder);
        config.setParamsDataToTree(true);
        IDocBuildTemplate docBuildTemplate = BuildTemplateFactory.getDocBuildTemplate(config.getFramework());
        return docBuildTemplate.getApiData(configBuilder);
    }
}
