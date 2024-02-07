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
package com.power.doc.model.torna;

import java.util.List;

/**
 * @author xingzi 2021/2/25 1:09
 **/
public class TornaApi {

    /**
     * "debugEnvs": [
     * {
     * "name": "测试环境",
     * "url": "http://10.1.30.165:2222"
     * }
     * ],
     * "apis": [
     */
    List<DebugEnv> debugEnvs;
    List<Apis> apis;
    String author;
    List<CommonErrorCode> commonErrorCodes;
    /**
     * 是否替换文档，1：替换，0：不替换（追加）。缺省：1
     */
    Integer isReplace;

    public Integer getIsReplace() {
        return isReplace;
    }

    public void setIsReplace(Integer isReplace) {
        this.isReplace = isReplace;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<DebugEnv> getDebugEnvs() {
        return debugEnvs;
    }

    public void setDebugEnvs(List<DebugEnv> debugEnvs) {
        this.debugEnvs = debugEnvs;
    }

    public List<Apis> getApis() {
        return apis;
    }

    public void setApis(List<Apis> apis) {
        this.apis = apis;
    }

    public List<CommonErrorCode> getCommonErrorCodes() {
        return commonErrorCodes;
    }

    public void setCommonErrorCodes(List<CommonErrorCode> commonErrorCodes) {
        this.commonErrorCodes = commonErrorCodes;
    }
}
