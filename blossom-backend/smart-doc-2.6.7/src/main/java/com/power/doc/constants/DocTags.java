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

/**
 * @author yu 2019/9/13.
 */
public interface DocTags {

    /**
     * java since tag
     */
    String SINCE = "since";

    /**
     * java required tag
     */
    String REQUIRED = "required";

    /**
     * java param tag
     */
    String PARAM = "param";

    /**
     * java apiNote tag for method detail
     */
    String API_NOTE = "apiNote";

    /**
     * java author tag for method author
     */
    String AUTHOR = "author";

    /**
     * java version tag
     */
    String VERSION = "version";

    /**
     * java deprecated tag
     */
    String DEPRECATED = "deprecated";

    /**
     * custom ignore tag
     */
    String IGNORE = "ignore";

    /**
     * custom @mock tag
     */
    String MOCK = "mock";

    /**
     * custom @dubbo tag
     */
    String DUBBO = "dubbo";

    /**
     * custom @api tag
     */
    String REST_API = "restApi";

    /**
     * custom @order tag
     */
    String ORDER = "order";

    /**
     * custom @group tag
     */
    String GROUP = "group";

    /**
     * custom @download tag
     */
    String DOWNLOAD = "download";

    /**
     * custom @page tag
     */
    String PAGE = "page";

    /**
     * custom @ignoreParams tag
     */
    String IGNORE_PARAMS = "ignoreParams";


    /**
     * Ignore ResponseBodyAdvice
     */
    String IGNORE_RESPONSE_BODY_ADVICE = "ignoreResponseBodyAdvice";

    String IGNORE_REQUEST_BODY_ADVICE = "ignoreRequestBodyAdvice";

    /**
     * response tag @since 2.2.0
     */
    String API_RESPONSE = "response";

    /**
     * custom @tag
     */
    String TAG = "tag";

    /**
     * custom @dubboRest tag
     */
    String DUBBO_REST = "dubboRest";

    /**
     * tag @see
     */
    String SEE = "see";
}
