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
 * spring validator annotations
 *
 * @author yu 2019/9/19.
 */
public enum DocValidatorAnnotationEnum {

    NOT_EMPTY("NotEmpty"),

    NOT_BLANK("NotBlank"),

    NOT_NULL("NotNull"),

    NULL("Null"),

    ASSERT_TRUE("AssertTrue"),

    ASSERT_FALSE("AssertFalse"),

    MIN("Min"),

    MAX("Max"),

    DECIMAL_MIN("DecimalMin"),

    DECIMAL_MAX("DecimalMax"),

    SIZE("Size"),

    DIGITS("Digits"),

    PAST("Past"),

    FUTURE("Future"),

    PATTERN("Pattern"),

    EMAIL("Email"),

    LENGTH("Length"),

    RANGE("Range"),

    VALIDATED("Validated");

    private final String value;

    DocValidatorAnnotationEnum(String value) {
        this.value = value;
    }

    public static List<String> listValidatorAnnotations() {
        List<String> annotations = new ArrayList<>();
        for (DocValidatorAnnotationEnum annotation : DocValidatorAnnotationEnum.values()) {
            annotations.add(annotation.value);
        }
        return annotations;
    }
}
