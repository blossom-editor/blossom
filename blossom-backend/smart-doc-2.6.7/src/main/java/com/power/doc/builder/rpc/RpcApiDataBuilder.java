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
package com.power.doc.builder.rpc;

import com.power.common.util.StringUtil;
import com.power.doc.constants.FrameworkEnum;
import com.power.doc.helper.JavaProjectBuilderHelper;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.rpc.RpcApiAllData;
import com.thoughtworks.qdox.JavaProjectBuilder;

/**
 * @author yu 2020/5/24.
 */
public class RpcApiDataBuilder {

    /**
     * Get list of ApiDoc
     *
     * @param config ApiConfig
     * @return List of ApiDoc
     */
    public static RpcApiAllData getApiData(ApiConfig config) {
        config.setShowJavaType(true);
        if (StringUtil.isEmpty(config.getFramework())) {
            config.setFramework(FrameworkEnum.DUBBO.getFramework());
        }
        RpcDocBuilderTemplate builderTemplate = new RpcDocBuilderTemplate();
        builderTemplate.checkAndInitForGetApiData(config);
        JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
        builderTemplate.getApiData(config, javaProjectBuilder);
        return builderTemplate.getApiData(config, javaProjectBuilder);
    }
}
