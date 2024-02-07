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
package com.power.doc.model;

import java.io.Serializable;
import java.util.*;

import com.power.common.util.StringUtil;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.model.request.ApiRequestExample;

/**
 * java api method info model.
 */
public class ApiMethodDoc implements Serializable, Cloneable {


    private static final long serialVersionUID = 7211922919532562867L;

    private ApiDoc clazzDoc;

    /**
     * methodId handled by md5
     *
     * @since 1.7.3 +
     */
    private String methodId;

    /**
     * method name
     *
     * @since 1.7.3 +
     */
    private String name;

    /**
     * method order
     *
     * @since 1.7+
     */
    private int order;


    /**
     * method description
     */
    private String desc;

    /**
     * detailed introduction of the method
     */
    private String detail;

    /**
     * server url
     */
    private String serverUrl;

    /**
     * controller method url contains server
     */
    private String url;

    /**
     * controller path
     */
    private String path;

    /**
     * http request type
     */
    private String type;

    /**
     * http request author
     */
    private String author;

    /**
     * only used for generate markdown and adoc
     * http readers
     */
    private String headers;

    /**
     * http contentType
     */
    private String contentType = DocGlobalConstants.URL_CONTENT_TYPE;

    /**
     * http request headers
     */
    private List<ApiReqParam> requestHeaders;

    /**
     * path params
     */
    private List<ApiParam> pathParams;

    /**
     * query params
     */
    private List<ApiParam> queryParams;

    /**
     * http request params
     */
    private List<ApiParam> requestParams;


    /**
     * http request-example usage(requestUrlParam + requestBody)
     */
    private String requestUsage;

    /**
     * request example detail
     */
    private ApiRequestExample requestExample;

    /**
     * http response usage
     */
    private String responseUsage;

    /**
     * http response params
     */
    private List<ApiParam> responseParams;

    /**
     * method deprecated
     */
    private boolean deprecated;

    /**
     * return schema
     */
    private Map<String, Object> returnSchema;

    /**
     * request schema
     */
    private Map<String, Object> requestSchema;

    /**
     * api group
     */
    private String group;

    /**
     * marking download
     */
    private boolean download;

    /**
     * link
     */
    private String link;

    /**
     * mark page
     */
    private String page = "";
    /**
     * torna request is array
     */
    private Integer isRequestArray;
    /**
     * torna request is array-type
     */
    private String requestArrayType;
    /**
     * torna response is array
     */
    private Integer isResponseArray;
    /**
     * torna request is array
     */
    private String responseArrayType;

    /**
     * tags
     */
    private String[] tags;

    private final Set<TagDoc> tagRefs = Collections.synchronizedSet(new LinkedHashSet<>());

    public Integer getIsRequestArray() {
        return isRequestArray;
    }

    public void setIsRequestArray(Integer isRequestArray) {
        this.isRequestArray = isRequestArray;
    }

    public String getRequestArrayType() {
        return requestArrayType;
    }

    public void setRequestArrayType(String requestArrayType) {
        this.requestArrayType = requestArrayType;
    }

    public Integer getIsResponseArray() {
        return isResponseArray;
    }

    public void setIsResponseArray(Integer isResponseArray) {
        this.isResponseArray = isResponseArray;
    }

    public String getResponseArrayType() {
        return responseArrayType;
    }

    public void setResponseArrayType(String responseArrayType) {
        this.responseArrayType = responseArrayType;
    }


    public String getMethodId() {
        return methodId;
    }

    public void setMethodId(String methodId) {
        this.methodId = methodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResponseUsage() {
        return responseUsage;
    }

    public void setResponseUsage(String responseUsage) {
        this.responseUsage = responseUsage;
    }

    public String getRequestUsage() {
        return requestUsage;
    }

    public void setRequestUsage(String requestUsage) {
        this.requestUsage = requestUsage;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List<ApiParam> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(List<ApiParam> requestParams) {
        this.requestParams = requestParams;
    }

    public List<ApiParam> getResponseParams() {
        return responseParams;
    }

    public void setResponseParams(List<ApiParam> responseParams) {
        this.responseParams = responseParams;
    }

    public List<ApiReqParam> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(List<ApiReqParam> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public ApiRequestExample getRequestExample() {
        return requestExample;
    }

    public void setRequestExample(ApiRequestExample requestExample) {
        this.requestExample = requestExample;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public List<ApiParam> getPathParams() {
        return pathParams;
    }

    public void setPathParams(List<ApiParam> pathParams) {
        this.pathParams = pathParams;
    }

    public List<ApiParam> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(List<ApiParam> queryParams) {
        this.queryParams = queryParams;
    }


    public Map<String, Object> getReturnSchema() {
        return returnSchema;
    }

    public void setReturnSchema(Map<String, Object> returnSchema) {
        this.returnSchema = returnSchema;
    }

    public Map<String, Object> getRequestSchema() {
        return requestSchema;
    }

    public void setRequestSchema(Map<String, Object> requestSchema) {
        this.requestSchema = requestSchema;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isDownload() {
        return download;
    }

    public void setDownload(boolean download) {
        this.download = download;
    }

    public String getLink() {
        if (StringUtil.isNotEmpty(link)) {
            return link;
        }
        return desc.replace(" ", "_").toLowerCase();
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String[] getTags() {
        return tags;
    }

    public ApiMethodDoc setTags(String[] tags) {
        this.tags = tags;
        return this;
    }

    public Set<TagDoc> getTagRefs() {
        return tagRefs;
    }

    public ApiDoc getClazzDoc() {
        return clazzDoc;
    }

    public void setClazzDoc(ApiDoc clazzDoc) {
        this.clazzDoc = clazzDoc;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"methodId\":\"")
            .append(methodId).append('\"');
        sb.append(",\"name\":\"")
            .append(name).append('\"');
        sb.append(",\"order\":")
            .append(order);
        sb.append(",\"desc\":\"")
            .append(desc).append('\"');
        sb.append(",\"detail\":\"")
            .append(detail).append('\"');
        sb.append(",\"serverUrl\":\"")
            .append(serverUrl).append('\"');
        sb.append(",\"url\":\"")
            .append(url).append('\"');
        sb.append(",\"path\":\"")
            .append(path).append('\"');
        sb.append(",\"type\":\"")
            .append(type).append('\"');
        sb.append(",\"author\":\"")
            .append(author).append('\"');
        sb.append(",\"headers\":\"")
            .append(headers).append('\"');
        sb.append(",\"contentType\":\"")
            .append(contentType).append('\"');
        sb.append(",\"requestHeaders\":")
            .append(requestHeaders);
        sb.append(",\"pathParams\":")
            .append(pathParams);
        sb.append(",\"queryParams\":")
            .append(queryParams);
        sb.append(",\"requestParams\":")
            .append(requestParams);
        sb.append(",\"requestUsage\":\"")
            .append(requestUsage).append('\"');
        sb.append(",\"requestExample\":")
            .append(requestExample);
        sb.append(",\"responseUsage\":\"")
            .append(responseUsage).append('\"');
        sb.append(",\"responseParams\":")
            .append(responseParams);
        sb.append(",\"deprecated\":")
            .append(deprecated);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public ApiMethodDoc clone() {
        try {
            return (ApiMethodDoc) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("clone apiMethodDoc is error", e);
        }
    }
}
