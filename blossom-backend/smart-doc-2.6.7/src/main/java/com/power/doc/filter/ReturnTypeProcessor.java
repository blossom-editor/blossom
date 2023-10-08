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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.power.doc.model.ApiReturn;

/**
 * @author yu 2020/4/17.
 */
public class ReturnTypeProcessor {

    private List<ReturnTypeFilter> filters = new ArrayList<>();

    private String typeName;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public ApiReturn process() {
        filters.add(new WebFluxReturnFilter());
        filters.add(new BoxReturnFilter());
        filters.add(new DefaultReturnFilter());
        for (ReturnTypeFilter filter : filters) {
            ApiReturn apiReturn = filter.doFilter(typeName);
            if (Objects.nonNull(apiReturn)) {
                return apiReturn;
            }
        }
        return null;
    }
}
