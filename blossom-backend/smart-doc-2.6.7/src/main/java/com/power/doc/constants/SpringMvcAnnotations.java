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
 * @author yu 2019/12/21.
 */
public interface SpringMvcAnnotations {

    String REQUEST_MAPPING = "RequestMapping";

    String GET_MAPPING = "GetMapping";

    String POST_MAPPING = "PostMapping";

    String PUT_MAPPING = "PutMapping";

    String PATCH_MAPPING = "PatchMapping";

    String DELETE_MAPPING = "DeleteMapping";

    String REQUEST_HERDER = "RequestHeader";

    String REQUEST_PARAM = "RequestParam";

    String REQUEST_BODY = "RequestBody";

    String CONTROLLER = "Controller";

    String REST_CONTROLLER = "RestController";

    String PATH_VARIABLE = "PathVariable";

    String SESSION_ATTRIBUTE = "SessionAttribute";

    String REQUEST_ATTRIBUTE = "RequestAttribute";

    String REQUEST_BODY_FULLY = "org.springframework.web.bind.annotation.RequestBody";
}
