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

import java.util.List;

/**
 * for rpc
 * @author yu 2020/1/29.
 */
public class RpcJavaMethod {

    /**
     * methodId handled by md5
     */
    private String methodId;

    /**
     * method name
     */
    private String name;

    /**
     * method order
     */
    private int order;


    /**
     * method description
     */
    private String desc;

    /**
     * method definition
     */
    private String methodDefinition;

    /**
     * escape method definition
     */
    private String escapeMethodDefinition;

    /**
     * detailed introduction of the method
     */
    private String detail;

    /**
     * method describe
     */
    private String throwsInfo;

    /**
     * return class Info
     */
    private String returnClassInfo;

    /**
     * http request params
     */
    private List<ApiParam> requestParams;

    /**
     * http request author
     */
    private String author;

    /**
     * http response params
     */
    private List<ApiParam> responseParams;

    /**
     * method deprecated
     */
    private boolean deprecated;

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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getThrowsInfo() {
        return throwsInfo;
    }

    public void setThrowsInfo(String throwsInfo) {
        this.throwsInfo = throwsInfo;
    }

    public String getReturnClassInfo() {
        return returnClassInfo;
    }

    public void setReturnClassInfo(String returnClassInfo) {
        this.returnClassInfo = returnClassInfo;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<ApiParam> getResponseParams() {
        return responseParams;
    }

    public void setResponseParams(List<ApiParam> responseParams) {
        this.responseParams = responseParams;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public List<ApiParam> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(List<ApiParam> requestParams) {
        this.requestParams = requestParams;
    }

    public String getMethodDefinition() {
        return methodDefinition;
    }

    public void setMethodDefinition(String methodDefinition) {
        this.methodDefinition = methodDefinition;
    }

    public String getEscapeMethodDefinition() {
        return escapeMethodDefinition;
    }

    public void setEscapeMethodDefinition(String escapeMethodDefinition) {
        this.escapeMethodDefinition = escapeMethodDefinition;
    }
}
