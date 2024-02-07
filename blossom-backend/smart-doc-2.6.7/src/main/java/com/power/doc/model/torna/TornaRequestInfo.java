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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

import com.power.doc.constants.TornaConstants;

/**
 * Print the log of pushing documents to Torna
 *
 * @author xingzi 2021/3/20 22:11
 **/
public class TornaRequestInfo {

    private String code;
    private String message;
    private Object requestInfo;
    private String responseInfo;
    private String category;

    public String getCategory() {
        return category;
    }

    public TornaRequestInfo setCategory(String category) {
        this.category = category;
        return this;
    }

    public TornaRequestInfo of() {
        return this;
    }

    public String getCode() {
        return code;
    }

    public TornaRequestInfo setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public TornaRequestInfo setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getRequestInfo() {
        return requestInfo;
    }

    public TornaRequestInfo setRequestInfo(Object requestInfo) {
        this.requestInfo = requestInfo;
        return this;
    }

    public Object getResponseInfo() {
        return responseInfo;
    }

    public TornaRequestInfo setResponseInfo(String responseInfo) {
        this.responseInfo = responseInfo;
        return this;
    }

    public String buildInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("---------------------------PUSH START---------------------------\n")
            .append("API: ")
            .append(category)
            .append("\n")
            .append("Request Param: \n")
            .append(TornaConstants.GSON.toJson(requestInfo))
            .append("\n")
            .append("Response: \n")
            .append(TornaConstants.GSON.fromJson(responseInfo, HashMap.class))
            .append("\n")
            .append("---------------------------PUSH END---------------------------\n");
        try {
            return URLDecoder.decode(sb.toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
