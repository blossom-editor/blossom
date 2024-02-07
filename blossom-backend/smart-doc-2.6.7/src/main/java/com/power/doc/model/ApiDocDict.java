/*
 * smart-doc
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
package com.power.doc.model;

import java.util.List;

/**
 * @author yu 2019/10/31.
 */

public class ApiDocDict {

    /**
     * order
     */
    private int order;

    /**
     * dict title
     */
    private String title;

    /**
     * dict description
     */
    private String description;

    /**
     * data dict
     */
    private List<DataDict> dataDictList;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<DataDict> getDataDictList() {
        return dataDictList;
    }

    public void setDataDictList(List<DataDict> dataDictList) {
        this.dataDictList = dataDictList;
    }

    public String getDescription() {
        return description;
    }

    public ApiDocDict setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"order\":")
            .append(order);
        sb.append(",\"title\":\"")
            .append(title).append('\"');
        sb.append(",\"dataDictList\":")
            .append(dataDictList);
        sb.append('}');
        return sb.toString();
    }
}

