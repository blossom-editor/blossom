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
 * @author yu 2020/11/26.
 */
public class ApiMethodReqParam {

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

    public static ApiMethodReqParam builder() {
        return new ApiMethodReqParam();
    }

    public List<ApiParam> getPathParams() {
        return pathParams;
    }

    public ApiMethodReqParam setPathParams(List<ApiParam> pathParams) {
        this.pathParams = pathParams;
        return this;
    }

    public List<ApiParam> getQueryParams() {
        return queryParams;
    }

    public ApiMethodReqParam setQueryParams(List<ApiParam> queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    public List<ApiParam> getRequestParams() {
        return requestParams;
    }

    public ApiMethodReqParam setRequestParams(List<ApiParam> requestParams) {
        this.requestParams = requestParams;
        return this;
    }
}
