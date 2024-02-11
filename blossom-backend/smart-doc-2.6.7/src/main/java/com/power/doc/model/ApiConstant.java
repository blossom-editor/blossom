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
package com.power.doc.model;

/**
 * @author yu 2020/7/2.
 */
public class ApiConstant {

    /**
     * Constants class
     */
    private Class constantsClass;

    /**
     * Constants class name
     */
    private String constantsClassName;

    /**
     * Description
     */
    private String description;

    public static ApiConstant builder() {
        return new ApiConstant();
    }

    public Class getConstantsClass() {
        return constantsClass;
    }

    public ApiConstant setConstantsClass(Class constantsClass) {
        this.constantsClass = constantsClass;
        return this;
    }

    public String getConstantsClassName() {
        return constantsClassName;
    }

    public ApiConstant setConstantsClassName(String constantsClassName) {
        this.constantsClassName = constantsClassName;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ApiConstant setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"constantsClass\":")
            .append(constantsClass);
        sb.append(",\"constantsClassName\":\"")
            .append(constantsClassName).append('\"');
        sb.append(",\"description\":\"")
            .append(description).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
