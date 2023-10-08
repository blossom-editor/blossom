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
package com.power.doc.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.TornaConstants;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDoc;
import com.power.doc.model.ApiGroup;
import com.power.doc.model.ApiMethodDoc;
import com.power.doc.model.annotation.FrameworkAnnotations;
import com.power.doc.utils.DocPathUtil;
import com.power.doc.utils.DocUtil;

/**
 * @author yu 2019/12/21.
 */
public interface IDocBuildTemplate<T> {

    List<T> getApiData(ProjectDocConfigBuilder projectBuilder);

    FrameworkAnnotations registeredAnnotations();


    /**
     * handle group api docs
     *
     * @param apiDocList list of apiDocList
     * @param apiConfig  ApiConfig apiConfig
     * @return List of ApiDoc
     * @author cqmike
     */
    default List<ApiDoc> handleApiGroup(List<ApiDoc> apiDocList, ApiConfig apiConfig) {
        if (CollectionUtil.isEmpty(apiDocList) || apiConfig == null) {
            return apiDocList;
        }
        List<ApiGroup> groups = apiConfig.getGroups();
        List<ApiDoc> finalApiDocs = new ArrayList<>();

        ApiDoc defaultGroup = ApiDoc.buildGroupApiDoc(TornaConstants.DEFAULT_GROUP_CODE);
        // show default group
        AtomicInteger order = new AtomicInteger(1);
        finalApiDocs.add(defaultGroup);

        if (CollectionUtil.isEmpty(groups)) {
            defaultGroup.setOrder(order.getAndIncrement());
            defaultGroup.getChildrenApiDocs().addAll(apiDocList);
            return finalApiDocs;
        }
        Map<String, String> hasInsert = new HashMap<>();
        for (ApiGroup group : groups) {
            ApiDoc groupApiDoc = ApiDoc.buildGroupApiDoc(group.getName());
            finalApiDocs.add(groupApiDoc);
            for (ApiDoc doc : apiDocList) {
                if (hasInsert.containsKey(doc.getAlias())) {
                    continue;
                }
                if (!DocUtil.isMatch(group.getApis(), doc.getPackageName() + "." + doc.getName())) {
                    continue;
                }
                hasInsert.put(doc.getAlias(), null);
                groupApiDoc.getChildrenApiDocs().add(doc);
                doc.setOrder(groupApiDoc.getChildrenApiDocs().size());
                doc.setGroup(group.getName());
                if (StringUtil.isEmpty(group.getPaths())) {
                    continue;
                }
                List<ApiMethodDoc> methodDocs = doc.getList().stream()
                    .filter(l -> DocPathUtil.matches(l.getPath(), group.getPaths(), null))
                    .collect(Collectors.toList());
                doc.setList(methodDocs);
            }
        }
        // Ungrouped join the default group
        for (ApiDoc doc : apiDocList) {
            String key = doc.getAlias();
            if (!hasInsert.containsKey(key)) {
                defaultGroup.getChildrenApiDocs().add(doc);
                doc.setOrder(defaultGroup.getChildrenApiDocs().size());
                hasInsert.put(doc.getAlias(), null);
            }
        }
        if (CollectionUtil.isEmpty(defaultGroup.getChildrenApiDocs())) {
            finalApiDocs.remove(defaultGroup);
        }
        finalApiDocs.forEach(group -> group.setOrder(order.getAndIncrement()));
        return finalApiDocs;
    }
}
