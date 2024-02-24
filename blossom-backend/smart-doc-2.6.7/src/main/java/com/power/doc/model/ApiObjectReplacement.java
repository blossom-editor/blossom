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
 * @author yu 2020/4/18.
 */
public class ApiObjectReplacement {

    private String className;

    private String replacementClassName;

    public static ApiObjectReplacement builder() {
        return new ApiObjectReplacement();
    }

    public String getClassName() {
        return className;
    }

    public ApiObjectReplacement setClassName(String className) {
        this.className = className;
        return this;
    }

    public String getReplacementClassName() {
        return replacementClassName;
    }

    public ApiObjectReplacement setReplacementClassName(String replacementClassName) {
        this.replacementClassName = replacementClassName;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"className\":\"")
            .append(className).append('\"');
        sb.append(",\"replacementClassName\":\"")
            .append(replacementClassName).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
