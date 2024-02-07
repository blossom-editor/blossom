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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.model.ApiMethodReqParam;
import com.power.doc.model.ApiParam;
import com.power.doc.model.ApiReqParam;

/**
 * @author yu 2020/8/8.
 */
public class ApiParamTreeUtil {

    public static List<ApiParam> apiParamToTree(List<ApiParam> apiParamList) {
        if (CollectionUtil.isEmpty(apiParamList)) {
            return new ArrayList<>(0);
        }
        List<ApiParam> params = new ArrayList<>();
        // find root
        for (ApiParam apiParam : apiParamList) {
            // remove pre of field
            apiParam.setField(apiParam.getField().replaceAll(DocGlobalConstants.PARAM_PREFIX, "").replaceAll("&nbsp;", ""));
            // pid == 0
            if (apiParam.getPid() == 0) {
                params.add(apiParam);
            }
        }
        for (ApiParam apiParam : params) {
            // remove pre of field
            apiParam.setChildren(getChild(apiParam.getId(), apiParamList, 0));
        }
        return params;
    }

    /**
     * find child
     *
     * @param id           param id
     * @param apiParamList List of ApiParam
     * @param counter      invoked counter
     * @return List of ApiParam
     */
    private static List<ApiParam> getChild(int id, List<ApiParam> apiParamList, int counter) {
        List<ApiParam> childList = new ArrayList<>();
        if (counter > 7) {
            return childList;
        }
        for (ApiParam param : apiParamList) {
            if (param.getPid() == id) {
                childList.add(param);
            }
        }
        counter++;
        for (ApiParam param : childList) {
            param.setChildren(getChild(param.getId(), apiParamList, counter));
        }
        if (childList.size() == 0) {
            return new ArrayList<>(0);
        }
        return childList;
    }

    /**
     * buildMethodReqParam handle configParam
     *
     * @param paramList          unConfigParam
     * @param queryReqParamMap   configQueryParam
     * @param pathReqParamMap    configPathParam
     * @param requestBodyCounter hasRequestBody
     * @return ApiMethodReqParam
     */
    public static ApiMethodReqParam buildMethodReqParam(List<ApiParam> paramList, final Map<String, ApiReqParam> queryReqParamMap,
        final Map<String, ApiReqParam> pathReqParamMap, int requestBodyCounter) {
        List<ApiParam> pathParams = new ArrayList<>();
        List<ApiParam> queryParams = new ArrayList<>();
        List<ApiParam> bodyParams = new ArrayList<>();
        for (ApiParam param : paramList) {
            param.setValue(StringUtil.removeDoubleQuotes(param.getValue()));
            if (param.isPathParam()) {
                if (pathReqParamMap.containsKey(param.getField())) {
                    param.setConfigParam(true).setValue(pathReqParamMap.get(param.getField()).getValue());
                }
                param.setId(pathParams.size() + 1);
                pathParams.add(param);
            } else if (param.isQueryParam() || requestBodyCounter < 1) {
                if (queryReqParamMap.containsKey(param.getField())) {
                    param.setConfigParam(true).setValue(queryReqParamMap.get(param.getField()).getValue());
                }
                param.setId(queryParams.size() + 1);
                queryParams.add(param);
            } else {
                param.setId(bodyParams.size() + 1);
                bodyParams.add(param);
            }
        }

        final Set<String> queryParamSet = queryParams.stream().map(ApiParam::getField).collect(Collectors.toSet());
        for (ApiReqParam value : queryReqParamMap.values()) {
            if (queryParamSet.contains(value.getName())) {
                continue;
            }
            final ApiParam apiParam = ApiReqParam.convertToApiParam(value)
                .setQueryParam(true).setId(queryParams.size() + 1);
            queryParams.add(apiParam);
        }

        final Set<String> pathParamSet = pathParams.stream().map(ApiParam::getField).collect(Collectors.toSet());
        for (ApiReqParam value : pathReqParamMap.values()) {
            if (pathParamSet.contains(value.getName())) {
                continue;
            }
            final ApiParam apiParam = ApiReqParam.convertToApiParam(value)
                .setPathParam(true).setId(pathParams.size() + 1);
            pathParams.add(apiParam);
        }

        return ApiMethodReqParam.builder()
            .setRequestParams(bodyParams)
            .setPathParams(pathParams)
            .setQueryParams(queryParams);
    }

}
