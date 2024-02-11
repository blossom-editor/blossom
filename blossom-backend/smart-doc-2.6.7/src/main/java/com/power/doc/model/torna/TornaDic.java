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
package com.power.doc.model.torna;

import java.util.List;

/**
 * @author xingzi 2021/5/19 19:57
 **/
public class TornaDic {

    private String name;
    private String description;
    private List<HttpParam> items;

    public String getDescription() {
        return description;
    }

    public TornaDic setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<HttpParam> getItems() {
        return items;
    }

    public TornaDic setItems(List<HttpParam> items) {
        this.items = items;
        return this;
    }

    public String getName() {
        return name;
    }

    public TornaDic setName(String name) {
        this.name = name;
        return this;
    }
}
