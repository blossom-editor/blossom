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
package com.power.doc.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.power.common.util.CollectionUtil;
import com.power.common.util.FileUtil;
import com.power.common.util.StringUtil;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.Methods;
import com.power.doc.factory.BuildTemplateFactory;
import com.power.doc.helper.JavaProjectBuilderHelper;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDoc;
import com.power.doc.model.ApiMethodDoc;
import com.power.doc.model.ApiParam;
import com.power.doc.model.ApiReqParam;
import com.power.doc.model.FormData;
import com.power.doc.model.postman.InfoBean;
import com.power.doc.model.postman.ItemBean;
import com.power.doc.model.postman.RequestItem;
import com.power.doc.model.postman.UrlBean;
import com.power.doc.model.postman.request.ParamBean;
import com.power.doc.model.postman.request.RequestBean;
import com.power.doc.model.postman.request.body.BodyBean;
import com.power.doc.model.postman.request.header.HeaderBean;
import com.power.doc.template.IDocBuildTemplate;
import com.power.doc.utils.DocPathUtil;
import com.power.doc.utils.JsonUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;


/**
 * @author yu 2019/11/21.
 */
public class PostmanJsonBuilder {

    private static final String MSG = "Interface name is not set.";

    /**
     * build postman json
     *
     * @param config Smart-doc ApiConfig
     */
    public static void buildPostmanCollection(ApiConfig config) {
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config, false);
        JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
        postManCreate(config, configBuilder);
    }

    /**
     * Only for smart-doc maven plugin and gradle plugin.
     *
     * @param config         ApiConfig Object
     * @param projectBuilder QDOX avaProjectBuilder
     */
    public static void buildPostmanCollection(ApiConfig config, JavaProjectBuilder projectBuilder) {
        DocBuilderTemplate builderTemplate = new DocBuilderTemplate();
        builderTemplate.checkAndInit(config, false);
        if (StringUtil.isNotEmpty(config.getServerEnv())) {
            config.setServerUrl(config.getServerEnv());
        }
        config.setParamsDataToTree(false);
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, projectBuilder);
        postManCreate(config, configBuilder);
    }

    /**
     * Build the first layer of Postman Item
     *
     * @param apiDoc Documentation for each Controller
     * @return First layer of Postman Item
     */
    private static ItemBean buildItemBean(ApiDoc apiDoc) {
        ItemBean itemBean = new ItemBean();
        itemBean.setName(StringUtil.isEmpty(apiDoc.getDesc()) ? MSG : apiDoc.getDesc());
        List<ItemBean> itemBeans = new ArrayList<>();
        List<ApiMethodDoc> apiMethodDocs = apiDoc.getList();
        apiMethodDocs.forEach(
            apiMethodDoc -> {
                ItemBean itemBean1 = buildItem(apiMethodDoc);
                itemBeans.add(itemBean1);
            }
        );
        itemBean.setItem(itemBeans);
        return itemBean;
    }

    /**
     * Build the second layer of Postman item
     *
     * @param apiMethodDoc Documentation for each method
     * @return The second layer of Postman item
     */
    private static ItemBean buildItem(ApiMethodDoc apiMethodDoc) {
        ItemBean item = new ItemBean();
        RequestBean requestBean = new RequestBean();

        item.setName(StringUtil.isEmpty(apiMethodDoc.getDesc()) ? MSG : apiMethodDoc.getDesc());
        item.setDescription(apiMethodDoc.getDetail());

        requestBean.setDescription(apiMethodDoc.getDesc());
        requestBean.setMethod(apiMethodDoc.getType());
        requestBean.setHeader(buildHeaderBeanList(apiMethodDoc));

        requestBean.setBody(buildBodyBean(apiMethodDoc));
        requestBean.setUrl(buildUrlBean(apiMethodDoc));

        item.setRequest(requestBean);
        return item;

    }

    private static UrlBean buildUrlBean(ApiMethodDoc apiMethodDoc) {
        UrlBean urlBean = new UrlBean(apiMethodDoc.getServerUrl());
        String url = Optional.ofNullable(apiMethodDoc.getRequestExample().getUrl()).orElse(apiMethodDoc.getUrl());
        urlBean.setRaw(DocPathUtil.toPostmanPath(url));
        String shortUrl = DocPathUtil.toPostmanPath(apiMethodDoc.getPath());
        String[] paths = shortUrl.split("/");
        List<String> pathList = new ArrayList<>();
        String serverPath = CollectionUtil.isNotEmpty(urlBean.getPath()) ? urlBean.getPath().get(0) : "";
        // Add server path
        if (CollectionUtil.isNotEmpty(urlBean.getPath()) && !shortUrl.contains(serverPath)) {
            String[] serverPaths = serverPath.split("/");
            pathList.addAll(Arrays.asList(serverPaths));
        }
        // Add mapping path
        for (String str : paths) {
            if (StringUtil.isNotEmpty(str)) {
                pathList.add(str);
            }
        }
        if (shortUrl.endsWith("/")) {
            pathList.add("");
        }

        urlBean.setPath(pathList);

        List<ParamBean> queryParams = new ArrayList<>();
        if (!apiMethodDoc.getType().equals(Methods.POST.getValue()) ||
            apiMethodDoc.getContentType().contains(DocGlobalConstants.JSON_CONTENT_TYPE)) {
            for (ApiParam apiParam : apiMethodDoc.getQueryParams()) {
                ParamBean queryParam = new ParamBean();
                queryParam.setDescription(apiParam.getDesc());
                queryParam.setKey(apiParam.getField());
                queryParam.setValue(apiParam.getValue());
                queryParams.add(queryParam);
            }
        }
        urlBean.setQuery(queryParams);

        List<ParamBean> variables = new ArrayList<>();
        for (ApiParam apiParam : apiMethodDoc.getPathParams()) {
            ParamBean queryParam = new ParamBean();
            queryParam.setDescription(apiParam.getDesc());
            queryParam.setKey(apiParam.getField());
            queryParam.setValue(apiParam.getValue());
            variables.add(queryParam);
        }
        urlBean.setVariable(variables);
        return urlBean;
    }

    /**
     * Build payload
     *
     * @return Body payload
     */
    private static BodyBean buildBodyBean(ApiMethodDoc apiMethodDoc) {
        BodyBean bodyBean;
        if (apiMethodDoc.getContentType().contains(DocGlobalConstants.JSON_CONTENT_TYPE)) {
            bodyBean = new BodyBean(Boolean.FALSE);// Json request
            bodyBean.setMode(DocGlobalConstants.POSTMAN_MODE_RAW);
            if (apiMethodDoc.getRequestExample() != null) {
                bodyBean.setRaw(apiMethodDoc.getRequestExample().getJsonBody());
            }
        } else {
            if (apiMethodDoc.getType().equals(Methods.POST.getValue())) {
                bodyBean = new BodyBean(Boolean.TRUE); //Formdata
                bodyBean.setMode(DocGlobalConstants.POSTMAN_MODE_FORMDATA);
                if (CollectionUtil.isNotEmpty(apiMethodDoc.getRequestExample().getFormDataList())) {
                    bodyBean.setFormdata(apiMethodDoc.getRequestExample().getFormDataList());
                } else {
                    // if method is post and formdata list size is 0, convert query param to formdata
                    List<ApiParam> queryParams = apiMethodDoc.getQueryParams();
                    List<FormData> formDataList = new ArrayList<>(queryParams.size());
                    for (ApiParam apiParam : queryParams) {
                        FormData formData = new FormData();
                        formData.setDescription(apiParam.getDesc());
                        formData.setKey(apiParam.getField());
                        formData.setType(apiParam.getType());
                        formData.setValue(apiParam.getValue());
                        formDataList.add(formData);
                    }
                    bodyBean.setFormdata(formDataList);
                }
            } else {
                bodyBean = new BodyBean(Boolean.FALSE);
            }
        }
        return bodyBean;
    }

    /**
     * Build header
     *
     * @return List of header
     */
    private static List<HeaderBean> buildHeaderBeanList(ApiMethodDoc apiMethodDoc) {
        List<HeaderBean> headerBeans = new ArrayList<>();
        List<ApiReqParam> headers = apiMethodDoc.getRequestHeaders();
        headers.forEach(
            apiReqHeader -> {
                HeaderBean headerBean = new HeaderBean();
                headerBean.setKey(apiReqHeader.getName());
                headerBean.setName(apiReqHeader.getName());
                headerBean.setValue(apiReqHeader.getValue());
                headerBean.setDisabled(!apiReqHeader.isRequired());
                headerBean.setDescription(apiReqHeader.getDesc());
                headerBeans.add(headerBean);
            }
        );

        return headerBeans;
    }

    private static void postManCreate(ApiConfig config, ProjectDocConfigBuilder configBuilder) {
        IDocBuildTemplate<ApiDoc> docBuildTemplate = BuildTemplateFactory.getDocBuildTemplate(config.getFramework());
        List<ApiDoc> apiDocList = docBuildTemplate.getApiData(configBuilder);
        RequestItem requestItem = new RequestItem();
        requestItem.setInfo(new InfoBean(config.getProjectName()));
        List<ItemBean> itemBeans = new ArrayList<>();
        apiDocList.forEach(
            apiDoc -> {
                ItemBean itemBean = buildItemBean(apiDoc);
                itemBeans.add(itemBean);
            }
        );
        requestItem.setItem(itemBeans);
        String filePath = config.getOutPath();
        filePath = filePath + DocGlobalConstants.POSTMAN_JSON;
        String data = JsonUtil.toPrettyJson(requestItem);
        FileUtil.nioWriteFile(data, filePath);
    }

}
