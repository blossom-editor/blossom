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
package com.power.doc.constants;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.power.common.util.DateTimeUtil;
import com.power.doc.model.ApiConfig;
import com.power.doc.utils.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.*;

/**
 * @author xingzi 2020/2/2
 */
public class TornaConstants {


    public static final String ID = "id";
    public static final String CODE = "code";
    public static final String MESSAGE = "msg";
    public static final String DATA = "data";
    public static final String SUCCESS_CODE = "0";

    public static final String DEFAULT_GROUP_CODE = "default";


    public static final String YES = "1";
    public static final String NO = "0";
    public static final String ARRAY = "array";

    public static final String CATEGORY_CREATE = "doc.category.create";
    public static final String PUSH = "doc.push";

    public static final String ENUM_PUSH = "enum.batch.push";
    /**
     * torna文件数组类型
     */
    public static final String PARAM_TYPE_FILE_ARRAY = "file[]";

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    /**
     * build torna params
     *
     * @param name   interface name
     * @param data   json
     * @param config ApiConfig
     * @return Map
     */
    public static Map<String, String> buildParams(String name, String data, ApiConfig config) {
        Map<String, String> param = new HashMap<>(8);
        try {
            if (StringUtils.isNotBlank(data)) {
                data = URLEncoder.encode(data, "utf-8");
            }
            // Public request parameters for pushing documents to Torna
            param.put("name", name);
            param.put("app_key", config.getAppKey());
            param.put("data", data);
            param.put("timestamp", DateTimeUtil.nowStrTime(DateTimeUtil.DATE_FORMAT_SECOND));
            param.put("version", "1.0");
            param.put("access_token", config.getAppToken());
            String sign = buildSign(param, config.getSecret());
            param.put("sign", sign);
            return param;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return param;
    }

    /**
     * build sign
     *
     * @param paramsMap param
     * @param secret    secret
     * @return String
     */
    public static String buildSign(Map<String, ?> paramsMap, String secret) {
        Set<String> keySet = paramsMap.keySet();
        List<String> paramNames = new ArrayList<>(keySet);

        Collections.sort(paramNames);
        StringBuilder paramNameValue = new StringBuilder();
        for (String paramName : paramNames) {
            Object value = paramsMap.get(paramName);
            if (value != null) {
                paramNameValue.append(paramName).append(value);
            }
        }
        String source = secret + paramNameValue + secret;
        return md5(source);
    }

    /**
     * Generate md5 and convert to uppercase
     *
     * @param message message
     * @return String
     */
    public static String md5(String message) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] input = message.getBytes();
            byte[] buff = md.digest(input);
            return byte2hex(buff);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert byte array to hex
     *
     * @param bytes byte array
     * @return String
     */
    private static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }
}
