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
package com.power.doc.model.postman;

import com.power.common.util.DateTimeUtil;
import com.power.doc.utils.StringUtils;

import java.util.UUID;


/**
 * @author xingzi
 */
public class InfoBean {

    String schema;
    private String _postman_id = UUID.randomUUID().toString();
    private String name;

    public InfoBean(String name) {
        if (StringUtils.isBlank(name)) {
            this.name = "smart-doc    " + DateTimeUtil.long2Str(System.currentTimeMillis(), DateTimeUtil.DATE_FORMAT_SECOND);
        } else {
            this.name = name;
        }
        this.schema = "https://schema.getpostman.com/json/collection/v2.1.0/collection.json";
    }
}
