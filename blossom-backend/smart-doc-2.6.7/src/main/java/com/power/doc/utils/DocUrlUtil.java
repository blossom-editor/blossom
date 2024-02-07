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
package com.power.doc.utils;

import java.util.List;
import java.util.Optional;

import com.power.common.util.StringUtil;
import com.power.common.util.UrlUtil;
import com.power.doc.constants.DocGlobalConstants;

/**
 * @author yu 2019/12/22.
 */
public class DocUrlUtil {

    public static String getMvcUrls(String baseServer, String baseUrl, List<String> urls) {
        StringBuilder sb = new StringBuilder();
        List<String> baseUrls = DocUtil.split(baseUrl);
        int size = urls.size();
        int baseSize = baseUrls.size();
        for (int j = 0; j < baseSize; j++) {
            String base = baseUrls.get(j);
            String trimBase = Optional.ofNullable(StringUtil.trimBlank(base)).orElse(StringUtil.EMPTY);
            trimBase = trimBase.replace("[", "").replace("]", "");
            for (int i = 0; i < size; i++) {
                String trimUrl = Optional.ofNullable(StringUtil.trimBlank(urls.get(i))).orElse(StringUtil.EMPTY);
                String url = baseServer;
                if (StringUtil.isNotEmpty(trimBase)) {
                    url = url + "/" + trimBase;
                }
                if (StringUtil.isNotEmpty(trimUrl)) {
                    url = url + "/" + trimUrl;
                }
                sb.append(UrlUtil.simplifyUrl(url));
                if (i < size - 1) {
                    sb.append(DocGlobalConstants.MULTI_URL_SEPARATOR);
                }
            }
            if (j < baseSize - 1) {
                sb.append(DocGlobalConstants.MULTI_URL_SEPARATOR);
            }
        }

        return sb.toString();
    }

    public static String getMvcUrls(String baseServer, String baseUrl, String shortUrl) {
        List<String> urls = DocUtil.split(shortUrl);
        return getMvcUrls(baseServer, baseUrl, urls);
    }
}
