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

/**
 * api group
 *
 * @author cqmike
 * @version 1.0.0
 * @since 2021/07/31 16:39:00
 */
public class ApiGroup {

    /**
     * group name
     */
    private String name;
    /**
     * package name
     * support patten
     */
    private String apis;
    /**
     * url path
     * support patten
     */
    private String paths;

    public static ApiGroup builder() {
        return new ApiGroup();
    }

    public String getName() {
        return name;
    }

    public ApiGroup setName(String name) {
        this.name = name;
        return this;
    }

    public String getApis() {
        return apis;
    }

    public ApiGroup setApis(String apis) {
        this.apis = apis;
        return this;
    }

    public String getPaths() {
        return paths;
    }

    public ApiGroup setPaths(String paths) {
        this.paths = paths;
        return this;
    }
}
