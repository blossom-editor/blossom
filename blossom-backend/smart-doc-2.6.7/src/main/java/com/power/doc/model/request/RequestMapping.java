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
package com.power.doc.model.request;

/**
 * @author yu 2019/12/22.
 */
public class RequestMapping {

    /**
     * url
     */
    private String url;

    /**
     * path
     */
    private String shortUrl;
    /**
     * method type
     */
    private String methodType;

    /**
     * media type
     */
    private String mediaType;

    /**
     * method deprecated
     */
    private boolean deprecated;

    public static RequestMapping builder() {
        return new RequestMapping();
    }

    public String getUrl() {
        return url;
    }

    public RequestMapping setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public RequestMapping setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
        return this;
    }

    public String getMethodType() {
        return methodType;
    }

    public RequestMapping setMethodType(String methodType) {
        this.methodType = methodType;
        return this;
    }

    public String getMediaType() {
        return mediaType;
    }

    public RequestMapping setMediaType(String mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public RequestMapping setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
        return this;
    }
}
