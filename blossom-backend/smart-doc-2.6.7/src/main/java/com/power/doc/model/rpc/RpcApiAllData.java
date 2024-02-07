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
package com.power.doc.model.rpc;

import java.util.List;

import com.power.doc.model.ApiDocDict;
import com.power.doc.model.ApiErrorCode;
import com.power.doc.model.RevisionLog;

/**
 * @author yu 2020/5/24.
 */
public class RpcApiAllData {

    /**
     * project name
     */
    private String projectName;

    /**
     * project id
     */
    private String projectId;

    /**
     * docLanguage
     */
    private String language;

    /**
     * doc list
     */
    private List<RpcApiDoc> apiDocList;

    /**
     *
     */
    private List<ApiDocDict> apiDocDictList;

    /**
     * error code list
     */
    private List<ApiErrorCode> errorCodeList;

    /**
     * List of change log
     */
    private List<RevisionLog> revisionLogs;

    /**
     * list of dependency
     */
    private List<RpcApiDependency> dependencyList;


    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<RpcApiDoc> getApiDocList() {
        return apiDocList;
    }

    public void setApiDocList(List<RpcApiDoc> apiDocList) {
        this.apiDocList = apiDocList;
    }

    public List<ApiDocDict> getApiDocDictList() {
        return apiDocDictList;
    }

    public void setApiDocDictList(List<ApiDocDict> apiDocDictList) {
        this.apiDocDictList = apiDocDictList;
    }

    public List<ApiErrorCode> getErrorCodeList() {
        return errorCodeList;
    }

    public void setErrorCodeList(List<ApiErrorCode> errorCodeList) {
        this.errorCodeList = errorCodeList;
    }

    public List<RevisionLog> getRevisionLogs() {
        return revisionLogs;
    }

    public void setRevisionLogs(List<RevisionLog> revisionLogs) {
        this.revisionLogs = revisionLogs;
    }

    public List<RpcApiDependency> getDependencyList() {
        return dependencyList;
    }

    public void setDependencyList(List<RpcApiDependency> dependencyList) {
        this.dependencyList = dependencyList;
    }
}
