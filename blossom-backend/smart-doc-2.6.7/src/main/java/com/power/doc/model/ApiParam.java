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

import com.power.doc.model.torna.EnumInfo;
import com.power.doc.utils.StringUtils;

import java.util.List;

import static com.power.doc.constants.DocGlobalConstants.PARAM_PREFIX;

/**
 * @author yu 2019/9/27.
 */
public class ApiParam {

    /**
     * param class name
     */
    private String className;
    /**
     * field id
     */
    private int id = 1;

    /**
     * field
     */
    private String field;

    /**
     * field type
     */
    private String type;

    /**
     * description
     */
    private String desc;

    /**
     * require flag
     */
    private boolean required;

    /**
     * version
     */
    private String version;

    /**
     * field pid
     */
    private int pid;

    /**
     * PathVariableParams flag
     */
    private boolean pathParam;

    /**
     * query params flag
     */
    private boolean queryParam;

    /**
     * param mock value
     */
    private String value;

    /**
     * children params
     */
    private List<ApiParam> children;

    /**
     * openapi items
     */
    private boolean hasItems;

    /**
     * enum values
     */
    private List<String> enumValues;
    /**
     * enum
     */
    private EnumInfo enumInfo;
    /**
     * Valid @Max
     */
    private String maxLength;

    /**
     * is config.json config param
     * default false
     */
    private boolean configParam;
    /**
     * 自循环引用
     */
    private boolean selfReferenceLoop;

    public static ApiParam of() {
        return new ApiParam();
    }

    public EnumInfo getEnumInfo() {
        return enumInfo;
    }

    public ApiParam setEnumInfo(EnumInfo enumInfo) {
        this.enumInfo = enumInfo;
        return this;
    }

    public String getField() {
        return field;
    }

    public ApiParam setField(String field) {
        this.field = field;
        return this;
    }

    public String getSourceField() {
        if (StringUtils.isEmpty(field)) {
            return StringUtils.EMPTY;
        }
        return field.replaceAll(PARAM_PREFIX, "").replaceAll("&nbsp;", "");
    }

    public String getType() {
        return type;
    }

    public ApiParam setType(String type) {
        this.type = type;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public ApiParam setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public boolean isRequired() {
        return required;
    }

    public ApiParam setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public ApiParam setVersion(String version) {
        this.version = version;
        return this;
    }

    public int getId() {
        return id;
    }

    public ApiParam setId(int id) {
        this.id = id;
        return this;
    }

    public int getPid() {
        return pid;
    }

    public ApiParam setPid(int pid) {
        this.pid = pid;
        return this;
    }

    public List<ApiParam> getChildren() {
        return children;
    }

    public ApiParam setChildren(List<ApiParam> children) {
        this.children = children;
        return this;
    }

    public boolean isPathParam() {
        return pathParam;
    }

    public ApiParam setPathParam(boolean pathParam) {
        this.pathParam = pathParam;
        return this;
    }

    public boolean isQueryParam() {
        return queryParam;
    }

    public ApiParam setQueryParam(boolean queryParam) {
        this.queryParam = queryParam;
        return this;
    }

    public String getValue() {
        return value;
    }

    public ApiParam setValue(String value) {
        this.value = value;
        return this;
    }

    public boolean isHasItems() {
        return hasItems;
    }

    public ApiParam setHasItems(boolean hasItems) {
        this.hasItems = hasItems;
        return this;
    }

    public List<String> getEnumValues() {
        return enumValues;
    }

    public ApiParam setEnumValues(List<String> enumValues) {
        this.enumValues = enumValues;
        return this;
    }

    public String getMaxLength() {
        return maxLength;
    }

    public ApiParam setMaxLength(String maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public boolean isConfigParam() {
        return configParam;
    }

    public ApiParam setConfigParam(boolean configParam) {
        this.configParam = configParam;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public ApiParam setClassName(String className) {
        this.className = className;
        return this;
    }

    public boolean isSelfReferenceLoop() {
        return selfReferenceLoop;
    }

    public void setSelfReferenceLoop(boolean selfReferenceLoop) {
        this.selfReferenceLoop = selfReferenceLoop;
    }

    @Override
    public String toString() {
        return "ApiParam{" +
            "id=" + id +
            ", field='" + field + '\'' +
            ", type='" + type + '\'' +
            ", desc='" + desc + '\'' +
            ", required=" + required +
            ", version='" + version + '\'' +
            ", pid=" + pid +
            ", pathParam=" + pathParam +
            ", queryParam=" + queryParam +
            ", value='" + value + '\'' +
            ", children=" + children +
            ", hasItems=" + hasItems +
            ", enumValues=" + enumValues +
            ", enumInfo=" + enumInfo +
            ", maxLength='" + maxLength + '\'' +
            ", configParam=" + configParam +
            ", selfReferenceLoop=" + selfReferenceLoop +
            '}';
    }
}
