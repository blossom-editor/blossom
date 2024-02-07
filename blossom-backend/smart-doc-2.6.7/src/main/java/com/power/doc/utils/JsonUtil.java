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
package com.power.doc.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * @author yu 2021/6/26.
 */
public class JsonUtil {

    /**
     * Convert a JSON string to pretty print
     *
     * @param jsonString json string
     * @return Format json string
     */
    public static String toPrettyFormat(String jsonString) {
        try {
            JsonElement jsonElement = JsonParser.parseString(jsonString);
            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            return gson.toJson(jsonElement);
        } catch (Exception e) {
            return jsonString;
        }
    }

    /**
     * Convert a JSON to String and pretty print
     *
     * @param src Json
     * @return Format json string
     */
    public static String toPrettyJson(Object src) {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        return gson.toJson(src);
    }
}
