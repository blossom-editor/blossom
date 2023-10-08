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

import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaType;

import java.util.Map;

/**
 * @author yu 2020/10/30.
 * @since 1.9.8
 */
public class DocJavaMethod {

    private JavaMethod javaMethod;

    private Map<String, Object> returnSchema;

    private Map<String, Object> requestSchema;

    private Map<String, JavaType> actualTypesMap;

    private boolean download;

    /**
     * mark page
     */
    private String page = "";

    /**
     * api group
     */
    private String group;

    private String author;

    /**
     * method description
     */
    private String desc;

    /**
     * detailed introduction of the method
     */
    private String detail;

    Map<String, String> paramTagMap;

    Map<String, String> paramsComments;

    public static DocJavaMethod builder() {
        return new DocJavaMethod();
    }

    public JavaMethod getJavaMethod() {
        return javaMethod;
    }

    public DocJavaMethod setJavaMethod(JavaMethod javaMethod) {
        this.javaMethod = javaMethod;
        return this;
    }

    public Map<String, JavaType> getActualTypesMap() {
        return actualTypesMap;
    }

    public DocJavaMethod setActualTypesMap(Map<String, JavaType> actualTypesMap) {
        this.actualTypesMap = actualTypesMap;
        return this;
    }

    public Map<String, Object> getReturnSchema() {
        return returnSchema;
    }

    public DocJavaMethod setReturnSchema(Map<String, Object> returnSchema) {
        this.returnSchema = returnSchema;
        return this;
    }

    public Map<String, Object> getRequestSchema() {
        return requestSchema;
    }

    public DocJavaMethod setRequestSchema(Map<String, Object> requestSchema) {
        this.requestSchema = requestSchema;
        return this;
    }

    public boolean isDownload() {
        return download;
    }

    public DocJavaMethod setDownload(boolean download) {
        this.download = download;
        return this;
    }

    public Map<String, String> getParamTagMap() {
        return paramTagMap;
    }

    public DocJavaMethod setParamTagMap(Map<String, String> paramTagMap) {
        this.paramTagMap = paramTagMap;
        return this;
    }

    public Map<String, String> getParamsComments() {
        return paramsComments;
    }

    public DocJavaMethod setParamsComments(Map<String, String> paramsComments) {
        this.paramsComments = paramsComments;
        return this;
    }

    public String getPage() {
        return page;
    }

    public DocJavaMethod setPage(String page) {
        this.page = page;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public DocJavaMethod setGroup(String group) {
        this.group = group;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public DocJavaMethod setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public DocJavaMethod setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public String getDetail() {
        return detail;
    }

    public DocJavaMethod setDetail(String detail) {
        this.detail = detail;
        return this;
    }
}
