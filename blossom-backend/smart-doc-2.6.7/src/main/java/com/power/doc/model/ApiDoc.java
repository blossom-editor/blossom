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


import java.util.*;

import com.power.common.util.StringUtil;

public class ApiDoc implements Comparable<ApiDoc> {

    /**
     * Order of controller
     *
     * @since 1.7+
     */
    public Integer order;

    /**
     * controller name
     */
    private String name;

    /**
     * controller alias handled by md5
     *
     * @since 1.7+
     */
    private String alias;

    /**
     * tags
     *
     * @author cqmike
     */
    private String[] tags;

    private final Set<TagDoc> tagRefs = Collections.synchronizedSet(new LinkedHashSet<>());
    /**
     * group
     *
     * @author cqmike
     */
    private String group;

    /**
     * class in package name
     */
    private String packageName;

    /**
     * List of method doc
     */
    private List<ApiMethodDoc> list;

    /**
     * method description
     */
    private String desc;

    /**
     * link
     */
    private String link;

    private String author;

    /**
     * if this is group, then is true
     */
    private boolean isFolder;

    /**
     * children
     */
    private List<ApiDoc> childrenApiDocs = new ArrayList<>();

    public static ApiDoc buildTagApiDoc(ApiDoc source, String tag, ApiMethodDoc methodDoc) {
        ApiDoc apiDoc = new ApiDoc();
        apiDoc.setAlias(source.getAlias());
        apiDoc.setLink(source.getLink());
        apiDoc.setDesc(tag);
        apiDoc.setAuthor(source.getAuthor());
        apiDoc.setPackageName(source.getPackageName());
        apiDoc.setName(tag);
        apiDoc.setList(new ArrayList<>());
        ApiMethodDoc clone = methodDoc.clone();
        clone.setOrder(apiDoc.getList().size() + 1);
        apiDoc.getList().add(clone);
        return apiDoc;
    }

    public static ApiDoc buildGroupApiDoc(String group) {
        ApiDoc apiDoc = new ApiDoc();
        apiDoc.setFolder(true);
        apiDoc.setGroup(group);
        apiDoc.setName(group);
        apiDoc.setDesc(group);
        apiDoc.setChildrenApiDocs(new ArrayList<>());
        return apiDoc;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ApiMethodDoc> getList() {
        return list;
    }

    public void setList(List<ApiMethodDoc> list) {
        this.list = list;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getLink() {
        if (StringUtil.isNotEmpty(link)) {
            return link;
        }
        return desc.replace(" ", "_").toLowerCase();
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    public List<ApiDoc> getChildrenApiDocs() {
        return childrenApiDocs;
    }

    public void setChildrenApiDocs(List<ApiDoc> childrenApiDocs) {
        this.childrenApiDocs = childrenApiDocs;
    }

    public Set<TagDoc> getTagRefs() {
        return tagRefs;
    }

    @Override
    public int compareTo(ApiDoc o) {
        if (Objects.nonNull(o.getDesc())) {
            return desc.compareTo(o.getDesc());
        }
        return name.compareTo(o.getName());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"order\":")
                .append(order);
        sb.append(",\"name\":\"")
                .append(name).append('\"');
        sb.append(",\"alias\":\"")
                .append(alias).append('\"');
        sb.append(",\"list\":")
                .append(list);
        sb.append(",\"desc\":\"")
                .append(desc).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
