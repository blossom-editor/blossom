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
package com.power.doc.helper;

import java.util.Map;

import com.power.common.util.StringUtil;
import com.power.doc.constants.DocTags;
import com.power.doc.utils.DocUtil;
import com.power.doc.utils.JavaClassValidateUtil;

/**
 * @author yu3.sun on 2022/10/14
 */
public abstract class BaseHelper {

    protected static String getFieldValueFromMock(String subTypeName, Map<String, String> tagsMap, String typeSimpleName) {
        String fieldValue = "";
        if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
            fieldValue = tagsMap.get(DocTags.MOCK);
            if (!DocUtil.javaPrimaryType(typeSimpleName)
                && !JavaClassValidateUtil.isCollection(subTypeName)
                && !JavaClassValidateUtil.isMap(subTypeName)
                && !JavaClassValidateUtil.isArray(subTypeName)) {
                fieldValue = DocUtil.handleJsonStr(fieldValue);
            }
        }
        return fieldValue;
    }
}
