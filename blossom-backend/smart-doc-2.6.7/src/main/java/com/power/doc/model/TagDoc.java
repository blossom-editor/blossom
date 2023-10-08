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
package com.power.doc.model;

import java.util.*;

/**
 * @author CKM
 *
 * tag relationship 2023/03/20 10:13:00
 */
public class TagDoc {
    private String tag;

    private final Set<ApiDoc> clazzDocs = Collections.synchronizedSet(new LinkedHashSet<>());

    private final Set<ApiMethodDoc> methodDocs = Collections.synchronizedSet(new LinkedHashSet<>(64));

    private TagDoc() {
    }

    public TagDoc(String tag) {
        super();
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Set<ApiDoc> getClazzDocs() {
        return clazzDocs;
    }

    public Set<ApiMethodDoc> getMethodDocs() {
        return methodDocs;
    }
}
