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

import java.util.Set;

/**
 * @author yu 2019/12/7.
 * @since 1.7.9
 */
public class ApiErrorCodeDictionary {

    /**
     * enumClass
     */
    private Class<?> enumClass;

    /**
     * enum implements
     * when enumClass is interface
     */
    private Set<Class<? extends Enum>> enumImplementSet;

    /**
     * enum class name
     */
    private String enumClassName;

    /**
     * customized enum values resolver,
     * class implement com.power.doc.extension.dict.DictionaryValuesResolver
     */
    private String valuesResolverClass;
    /**
     * code field
     */
    private String codeField;

    /**
     * description field
     */
    private String descField;

    @Deprecated
    public static ApiErrorCodeDictionary dict() {
        return new ApiErrorCodeDictionary();
    }

    public static ApiErrorCodeDictionary builder() {
        return new ApiErrorCodeDictionary();
    }

    public Class getEnumClass() {
        return enumClass;
    }

    public ApiErrorCodeDictionary setEnumClass(Class enumClass) {
        this.enumClass = enumClass;
        return this;
    }

    public Set<Class<? extends Enum>> getEnumImplementSet() {
        return enumImplementSet;
    }

    public ApiErrorCodeDictionary setEnumImplementSet(Set<Class<? extends Enum>> enumImplementSet) {
        this.enumImplementSet = enumImplementSet;
        return this;
    }

    public String getCodeField() {
        return codeField;
    }

    public ApiErrorCodeDictionary setCodeField(String codeField) {
        this.codeField = codeField;
        return this;
    }

    public String getDescField() {
        return descField;
    }

    public ApiErrorCodeDictionary setDescField(String descField) {
        this.descField = descField;
        return this;
    }

    public String getEnumClassName() {
        return enumClassName;
    }

    public ApiErrorCodeDictionary setEnumClassName(String enumClassName) {
        this.enumClassName = enumClassName;
        return this;
    }

    public String getValuesResolverClass() {
        return this.valuesResolverClass;
    }

    public ApiErrorCodeDictionary setValuesResolverClass(String valuesResolverClass) {
        this.valuesResolverClass = valuesResolverClass;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"enumClass\":")
            .append(enumClass);
        sb.append(",\"enumClassName\":\"")
            .append(enumClassName).append('\"');
        sb.append(",\"codeField\":\"")
            .append(codeField).append('\"');
        sb.append(",\"descField\":\"")
            .append(descField).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
