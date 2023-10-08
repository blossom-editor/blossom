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
package com.power.doc.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yu 2019/12/20.
 */
public enum SpringMvcRequestAnnotationsEnum {

    PATH_VARIABLE("PathVariable"),
    PATH_VARIABLE_FULLY("org.springframework.web.bind.annotation.PathVariable"),
    REQ_PARAM("RequestParam"),
    REQ_PARAM_FULLY("org.springframework.web.bind.annotation.RequestParam"),
    REQUEST_BODY("RequestBody"),
    REQUEST_BODY_FULLY("org.springframework.web.bind.annotation.RequestBody"),
    REQUEST_HERDER("RequestHeader"),
    REQUEST_HERDER_FULLY("org.springframework.web.bind.annotation.RequestHeader"),
    ;
    private final String value;

    SpringMvcRequestAnnotationsEnum(String value) {
        this.value = value;
    }

    public static List<String> listSpringMvcRequestAnnotations() {
        List<String> annotations = new ArrayList<>();
        for (SpringMvcRequestAnnotationsEnum annotation : SpringMvcRequestAnnotationsEnum.values()) {
            annotations.add(annotation.value);
        }
        return annotations;
    }
}
