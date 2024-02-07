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

import com.power.common.model.EnumDictionary;
import com.power.common.util.EnumUtil;
import com.power.doc.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;


/**
 * @author yu 2019/10/31.
 */
public class ApiDataDictionary {

    /**
     * Dictionary
     */
    private String title;

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
     * code field
     */
    private String codeField;

    /**
     * description field
     */
    private String descField;

    public static ApiDataDictionary builder() {
        return new ApiDataDictionary();
    }


    public String getTitle() {
        return title;
    }

    public ApiDataDictionary setTitle(String title) {
        this.title = title;
        return this;
    }

    public Class getEnumClass() {
        return enumClass;
    }

    public ApiDataDictionary setEnumClass(Class<?> enumClass) {
        this.enumClass = enumClass;
        if (StringUtils.isBlank(this.enumClassName) && Objects.nonNull(enumClass)) {
            this.enumClassName = enumClass.getSimpleName();
        }
        return this;
    }

    public Set<Class<? extends Enum>> getEnumImplementSet() {
        return enumImplementSet;
    }

    public ApiDataDictionary setEnumImplementSet(Set<Class<? extends Enum>> enumImplementSet) {
        this.enumImplementSet = enumImplementSet;
        return this;
    }

    public String getCodeField() {
        return codeField;
    }

    public ApiDataDictionary setCodeField(String codeField) {
        this.codeField = codeField;
        return this;
    }

    public String getDescField() {
        return descField;
    }

    public ApiDataDictionary setDescField(String descField) {
        this.descField = descField;
        return this;
    }

    public String getEnumClassName() {
        return enumClassName;
    }

    public ApiDataDictionary setEnumClassName(String enumClassName) {
        this.enumClassName = enumClassName;
        return this;
    }

    public List<EnumDictionary> getEnumDataDict(Class enumClass) {
        if (Objects.nonNull(enumClass)) {
            return EnumUtil.getEnumInformation(enumClass, this.getCodeField(),
                this.getDescField());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"title\":\"")
            .append(title).append('\"');
        sb.append(",\"enumClass\":")
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
