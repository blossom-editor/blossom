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
package com.power.doc.builder.rpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.power.common.util.CollectionUtil;
import com.power.common.util.DateTimeUtil;
import com.power.common.util.FileUtil;
import com.power.common.util.StringUtil;
import com.power.doc.builder.BaseDocBuilderTemplate;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.FrameworkEnum;
import com.power.doc.constants.TemplateVariable;
import com.power.doc.factory.BuildTemplateFactory;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiDocDict;
import com.power.doc.model.ApiErrorCode;
import com.power.doc.model.rpc.RpcApiAllData;
import com.power.doc.model.rpc.RpcApiDoc;
import com.power.doc.template.IDocBuildTemplate;
import com.power.doc.utils.BeetlTemplateUtil;
import com.power.doc.utils.DocUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;

import org.beetl.core.Template;

import static com.power.doc.constants.DocGlobalConstants.FILE_SEPARATOR;
import static com.power.doc.constants.DocGlobalConstants.RPC_OUT_DIR;

/**
 * @author yu 2020/5/16.
 */
public class RpcDocBuilderTemplate extends BaseDocBuilderTemplate {

    private static final String DEPENDENCY_TITLE = "Add dependency";
    private static final long now = System.currentTimeMillis();

    public void checkAndInit(ApiConfig config) {
        if (StringUtil.isEmpty(config.getFramework())) {
            config.setFramework(FrameworkEnum.DUBBO.getFramework());
        }
        super.checkAndInit(config, false);
        config.setOutPath(config.getOutPath() + FILE_SEPARATOR + RPC_OUT_DIR);
    }

    /**
     * Generate api documentation for all controllers.
     *
     * @param apiDocList    list of api doc
     * @param config        api config
     * @param template      template
     * @param fileExtension file extension
     */
    public void buildApiDoc(List<RpcApiDoc> apiDocList, ApiConfig config, String template, String fileExtension) {
        FileUtil.mkdirs(config.getOutPath());
        for (RpcApiDoc rpcDoc : apiDocList) {
            Template mapper = BeetlTemplateUtil.getByName(template);
            mapper.binding(TemplateVariable.DESC.getVariable(), rpcDoc.getDesc());
            mapper.binding(TemplateVariable.NAME.getVariable(), rpcDoc.getName());
            mapper.binding(TemplateVariable.LIST.getVariable(), rpcDoc.getList());
            mapper.binding(TemplateVariable.AUTHOR.getVariable(), rpcDoc.getAuthor());
            mapper.binding(TemplateVariable.PROTOCOL.getVariable(), rpcDoc.getProtocol());
            mapper.binding(TemplateVariable.VERSION.getVariable(), rpcDoc.getVersion());
            mapper.binding(TemplateVariable.URI.getVariable(), rpcDoc.getUri());
            FileUtil.nioWriteFile(mapper.render(), config.getOutPath() + FILE_SEPARATOR + rpcDoc.getShortName() + fileExtension);
        }
    }

    /**
     * Merge all api doc into one document
     *
     * @param apiDocList         list  data of Api doc
     * @param config             api config
     * @param javaProjectBuilder JavaProjectBuilder
     * @param template           template
     * @param outPutFileName     output file
     */
    public void buildAllInOne(List<RpcApiDoc> apiDocList, ApiConfig config, JavaProjectBuilder javaProjectBuilder, String template,
        String outPutFileName) {
        String outPath = config.getOutPath();
        String strTime = DateTimeUtil.long2Str(now, DateTimeUtil.DATE_FORMAT_SECOND);
        String rpcConfig = config.getRpcConsumerConfig();
        String rpcConfigConfigContent = null;
        if (Objects.nonNull(rpcConfig)) {
            rpcConfigConfigContent = FileUtil.getFileContent(rpcConfig);
        }
        FileUtil.mkdirs(outPath);
        List<ApiErrorCode> errorCodeList = DocUtil.errorCodeDictToList(config, javaProjectBuilder);
        Template tpl = BeetlTemplateUtil.getByName(template);
        tpl.binding(TemplateVariable.API_DOC_LIST.getVariable(), apiDocList);
        tpl.binding(TemplateVariable.ERROR_CODE_LIST.getVariable(), errorCodeList);
        tpl.binding(TemplateVariable.VERSION_LIST.getVariable(), config.getRevisionLogs());
        tpl.binding(TemplateVariable.DEPENDENCY_LIST.getVariable(), config.getRpcApiDependencies());
        tpl.binding(TemplateVariable.VERSION.getVariable(), now);
        tpl.binding(TemplateVariable.CREATE_TIME.getVariable(), strTime);
        tpl.binding(TemplateVariable.PROJECT_NAME.getVariable(), config.getProjectName());
        tpl.binding(TemplateVariable.RPC_CONSUMER_CONFIG.getVariable(), rpcConfigConfigContent);
        setDirectoryLanguageVariable(config, tpl);

        List<ApiDocDict> apiDocDictList = DocUtil.buildDictionary(config, javaProjectBuilder);
        tpl.binding(TemplateVariable.DICT_LIST.getVariable(), apiDocDictList);

        int codeIndex = apiDocList.isEmpty() ? 1 : apiDocDictList.size();

        if (CollectionUtil.isNotEmpty(errorCodeList)) {
            tpl.binding(TemplateVariable.ERROR_CODE_ORDER.getVariable(), ++codeIndex);
        }

        if (CollectionUtil.isNotEmpty(apiDocDictList)) {
            tpl.binding(TemplateVariable.DICT_ORDER.getVariable(), ++codeIndex);
        }

        setCssCDN(config, tpl);
        FileUtil.nioWriteFile(tpl.render(), outPath + FILE_SEPARATOR + outPutFileName);
    }

    /**
     * Build search js
     *
     * @param apiDocList         list  data of Api doc
     * @param config             api config
     * @param javaProjectBuilder projectBuilder
     * @param template           template
     * @param outPutFileName     output file
     */
    public void buildSearchJs(List<RpcApiDoc> apiDocList, ApiConfig config, JavaProjectBuilder javaProjectBuilder
        , String template, String outPutFileName) {
        List<ApiErrorCode> errorCodeList = DocUtil.errorCodeDictToList(config, javaProjectBuilder);
        Template tpl = BeetlTemplateUtil.getByName(template);
        // directory tree
        List<RpcApiDoc> apiDocs = new ArrayList<>();
        RpcApiDoc apiDoc = new RpcApiDoc();
        apiDoc.setAlias(DEPENDENCY_TITLE);
        apiDoc.setOrder(1);
        apiDoc.setDesc(DEPENDENCY_TITLE);
        apiDoc.setList(new ArrayList<>(0));
        apiDocs.add(apiDoc);
        for (RpcApiDoc apiDoc1 : apiDocList) {
            apiDoc1.setOrder(apiDocs.size() + 1);
            apiDocs.add(apiDoc1);
        }
        Map<String, String> titleMap = setDirectoryLanguageVariable(config, tpl);
        if (CollectionUtil.isNotEmpty(errorCodeList)) {
            RpcApiDoc apiDoc1 = new RpcApiDoc();
            apiDoc1.setOrder(apiDocs.size() + 1);
            apiDoc1.setDesc(titleMap.get(TemplateVariable.ERROR_LIST_TITLE.getVariable()));
            apiDoc1.setList(new ArrayList<>(0));
            apiDocs.add(apiDoc1);
        }

        // set dict list
        List<ApiDocDict> apiDocDictList = DocUtil.buildDictionary(config, javaProjectBuilder);
        tpl.binding(TemplateVariable.DICT_LIST.getVariable(), apiDocDictList);
        tpl.binding(TemplateVariable.DIRECTORY_TREE.getVariable(), apiDocs);
        FileUtil.nioWriteFile(tpl.render(), config.getOutPath() + FILE_SEPARATOR + outPutFileName);
    }

    /**
     * build error_code adoc
     *
     * @param config             api config
     * @param template           template
     * @param outPutFileName     output file
     * @param javaProjectBuilder javaProjectBuilder
     */
    public void buildErrorCodeDoc(ApiConfig config, String template, String outPutFileName, JavaProjectBuilder javaProjectBuilder) {
        List<ApiErrorCode> errorCodeList = DocUtil.errorCodeDictToList(config, javaProjectBuilder);
        Template mapper = BeetlTemplateUtil.getByName(template);
        mapper.binding(TemplateVariable.LIST.getVariable(), errorCodeList);
        FileUtil.nioWriteFile(mapper.render(), config.getOutPath() + FILE_SEPARATOR + outPutFileName);
    }

    /**
     * get all api data
     *
     * @param config             ApiConfig
     * @param javaProjectBuilder JavaProjectBuilder
     * @return ApiAllData
     */
    public RpcApiAllData getApiData(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
        RpcApiAllData apiAllData = new RpcApiAllData();
        apiAllData.setLanguage(config.getLanguage().getCode());
        apiAllData.setProjectName(config.getProjectName());
        apiAllData.setProjectId(DocUtil.generateId(config.getProjectName()));
        apiAllData.setApiDocList(listOfApiData(config, javaProjectBuilder));
        apiAllData.setErrorCodeList(DocUtil.errorCodeDictToList(config, javaProjectBuilder));
        apiAllData.setRevisionLogs(config.getRevisionLogs());
        apiAllData.setApiDocDictList(DocUtil.buildDictionary(config, javaProjectBuilder));
        apiAllData.setDependencyList(config.getRpcApiDependencies());
        return apiAllData;
    }

    private List<RpcApiDoc> listOfApiData(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
        this.checkAndInitForGetApiData(config);
        config.setMd5EncryptedHtmlName(true);
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
        IDocBuildTemplate<RpcApiDoc> docBuildTemplate = BuildTemplateFactory.getDocBuildTemplate(config.getFramework());
        return docBuildTemplate.getApiData(configBuilder);
    }

    public List<RpcApiDoc> getRpcApiDoc(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
        config.setShowJavaType(true);
        // check
        checkAndInit(config);
        ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(config, javaProjectBuilder);
        IDocBuildTemplate<RpcApiDoc> docBuildTemplate = BuildTemplateFactory.getDocBuildTemplate(config.getFramework());
        return docBuildTemplate.getApiData(configBuilder);
    }

}
