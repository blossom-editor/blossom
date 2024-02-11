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

/**
 * Source code path
 *
 * @author yu 2018/7/14.
 */
public class SourceCodePath {

    /**
     * Source code path
     */
    private String path;

    /**
     * path description
     */
    private String desc;

    public static SourceCodePath builder() {
        return new SourceCodePath();
    }

    public String getPath() {
        return path;
    }

    public SourceCodePath setPath(String path) {
        this.path = path;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public SourceCodePath setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"path\":\"").append(path).append('\"');
        sb.append(",\"desc\":\"").append(desc).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
