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
package com.power.doc.filter;

import java.util.HashSet;
import java.util.Set;

import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.model.ApiReturn;
import com.power.doc.utils.DocClassUtil;

/**
 * @author yu 2020/4/17.
 */
public class BoxReturnFilter implements ReturnTypeFilter {

    private static final Set<String> TYPE_SET = new HashSet<>();

    static {
        TYPE_SET.add("java.util.concurrent.Callable");
        TYPE_SET.add("java.util.concurrent.Future");
        TYPE_SET.add("java.util.concurrent.CompletableFuture");
        TYPE_SET.add("org.springframework.web.context.request.async.DeferredResult");
        TYPE_SET.add("org.springframework.web.context.request.async.WebAsyncTask");
        TYPE_SET.add("reactor.core.publisher.Mono");
        TYPE_SET.add("org.springframework.http.ResponseEntity");
    }

    @Override
    public ApiReturn doFilter(String fullyName) {
        if (TYPE_SET.stream().anyMatch(fullyName::startsWith)) {
            ApiReturn apiReturn = new ApiReturn();
            if (fullyName.contains("<")) {
                String[] strings = DocClassUtil.getSimpleGicName(fullyName);
                String newFullName = strings[0];
                if (newFullName.contains("<")) {
                    apiReturn.setGenericCanonicalName(newFullName);
                    apiReturn.setSimpleName(newFullName.substring(0, newFullName.indexOf("<")));
                } else {
                    apiReturn.setGenericCanonicalName(newFullName);
                    apiReturn.setSimpleName(newFullName);
                }
            } else {
                //directly return Java Object
                apiReturn.setGenericCanonicalName(DocGlobalConstants.JAVA_OBJECT_FULLY);
                apiReturn.setSimpleName(DocGlobalConstants.JAVA_OBJECT_FULLY);
            }
            return apiReturn;
        }
        return null;
    }
}
