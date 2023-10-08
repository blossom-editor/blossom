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
package com.power.doc.handler;

import com.power.doc.constants.DocAnnotationConstants;
import com.power.doc.constants.SpringMvcAnnotations;
import com.power.doc.model.annotation.HeaderAnnotation;

/**
 * @author yu 2019/12/22.
 */
public class SpringMVCRequestHeaderHandler implements IHeaderHandler {

    @Override
    public boolean isMapping(String annotationName) {
        switch (annotationName) {
            case "GetMapping":
            case "RequestMapping":
            case "PostMapping":
            case "PutMapping":
            case "PatchMapping":
            case "DeleteMapping":
                return true;
            default:
                return false;
        }
    }

    @Override
    public HeaderAnnotation getHeaderAnnotation() {
        return HeaderAnnotation.builder()
            .setAnnotationName(SpringMvcAnnotations.REQUEST_HERDER)
            .setValueProp(DocAnnotationConstants.VALUE_PROP)
            .setDefaultValueProp(DocAnnotationConstants.DEFAULT_VALUE_PROP)
            .setRequiredProp(DocAnnotationConstants.REQUIRED_PROP);
    }
}
