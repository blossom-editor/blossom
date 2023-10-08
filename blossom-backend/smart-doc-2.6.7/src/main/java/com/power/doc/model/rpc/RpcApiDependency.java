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
package com.power.doc.model.rpc;

/**
 * @author yu 2020/5/17.
 */
public class RpcApiDependency {

    private String groupId;

    private String artifactId;

    private String version;

    public static RpcApiDependency builder() {
        return new RpcApiDependency();
    }

    public String getGroupId() {
        return groupId;
    }

    public RpcApiDependency setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public RpcApiDependency setArtifactId(String artifactId) {
        this.artifactId = artifactId;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public RpcApiDependency setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public String toString() {
        return "<dependency>" + "\r" +
            "\t" + "<groupId>" + groupId + "</groupId>" + "\n" +
            "\t" + "<artifactId>" + artifactId + "</artifactId>" + "\n" +
            "\t" + "<version>" + version + "</version>" + "" +
            "\r" + "</dependency>";
    }
}
