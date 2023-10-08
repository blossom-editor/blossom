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
package com.power.doc.builder;

import com.power.doc.helper.JavaProjectBuilderHelper;
import com.power.doc.model.ApiAllData;
import com.power.doc.model.ApiConfig;
import com.thoughtworks.qdox.JavaProjectBuilder;

/**
 * @author yu 2019/12/7.
 * @since 1.7.9
 */
public class ApiDataBuilder {

    /**
     * Get list of ApiDoc
     *
     * @param config ApiConfig
     * @return List of ApiDoc
     */
    public static ApiAllData getApiData(ApiConfig config) {
        return getApiData(config, Boolean.FALSE);
    }

    /**
     * Get list of ApiDoc
     *
     * @param config ApiConfig
     * @return List of ApiDoc
     */
    public static ApiAllData getApiDataTree(ApiConfig config) {
        return getApiData(config, Boolean.TRUE);
    }

    private static ApiAllData getApiData(ApiConfig config, boolean toTree) {
        config.setParamsDataToTree(toTree);
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInitForGetApiData(config);
        JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
        ApiAllData apiAllData = builderTemplate.getApiData(config, javaProjectBuilder);
        return apiAllData;
    }
}
