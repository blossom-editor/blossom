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
package com.power.doc.model.request;

import com.power.common.util.StringUtil;
import com.power.doc.model.FormData;
import com.power.doc.utils.StringUtils;

import java.util.List;

/**
 * @author yu 2019/12/22.
 */

public class ApiRequestExample {

    /**
     * json body
     */
    private String jsonBody;

    /**
     * example body
     */
    private String exampleBody;

    /**
     * url
     */
    private String url;

    /**
     * list of form data
     */
    private List<FormData> formDataList;

    private boolean json;

    public static ApiRequestExample builder() {
        return new ApiRequestExample();
    }

    public String getJsonBody() {
        return jsonBody;
    }

    public ApiRequestExample setJsonBody(String jsonBody) {
        this.jsonBody = jsonBody;
        return this;
    }

    public ApiRequestExample addJsonBody(String jsonBody) {
        if (StringUtil.isNotEmpty(jsonBody)) {
            this.jsonBody = StringUtils.join("&", this.jsonBody, jsonBody);
        } else {
            this.jsonBody = jsonBody;
        }
        return this;
    }

    public String getUrl() {
        return url;
    }

    public ApiRequestExample setUrl(String url) {
        this.url = url;
        return this;
    }

    public List<FormData> getFormDataList() {
        return formDataList;
    }

    public ApiRequestExample setFormDataList(List<FormData> formDataList) {
        this.formDataList = formDataList;
        return this;
    }

    public boolean isJson() {
        return json;
    }

    public ApiRequestExample setJson(boolean json) {
        this.json = json;
        return this;
    }

    public String getExampleBody() {
        return exampleBody;
    }

    public ApiRequestExample setExampleBody(String exampleBody) {
        this.exampleBody = exampleBody;
        return this;
    }
}
