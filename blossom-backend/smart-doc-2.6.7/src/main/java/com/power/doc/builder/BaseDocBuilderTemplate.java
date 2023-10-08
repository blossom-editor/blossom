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

import com.power.common.util.DateTimeUtil;
import com.power.common.util.StringUtil;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.DocLanguage;
import com.power.doc.constants.FrameworkEnum;
import com.power.doc.constants.TemplateVariable;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.RevisionLog;
import com.power.doc.utils.StringUtils;
import org.beetl.core.Resource;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.power.doc.constants.DocGlobalConstants.CSS_CDN;
import static com.power.doc.constants.DocGlobalConstants.CSS_CDN_CH;

/**
 * @author yu 2020/5/16.
 */
public class BaseDocBuilderTemplate {

    public static long NOW = System.currentTimeMillis();

    public static void copyJarFile(String source, String target) {
        ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader("/template/");
        Resource resource = resourceLoader.getResource(source);
        try (FileWriter fileWriter = new FileWriter(target, false);
            Reader reader = resource.openReader()) {
            char[] c = new char[1024 * 1024];
            int temp;
            int len = 0;
            while ((temp = reader.read()) != -1) {
                c[len] = (char) temp;
                len++;
            }
            reader.close();
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(c, 0, len);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * check condition and init
     *
     * @param config       Api config
     * @param checkOutPath check out path
     */
    public void checkAndInit(ApiConfig config, boolean checkOutPath) {
        this.checkAndInitForGetApiData(config);
        if (StringUtil.isEmpty(config.getOutPath()) && !checkOutPath) {
            throw new RuntimeException("doc output path can't be null or empty");
        }
    }

    /**
     * check condition and init for get Data
     *
     * @param config Api config
     */
    public void checkAndInitForGetApiData(ApiConfig config) {
        if (Objects.isNull(config)) {
            throw new NullPointerException("ApiConfig can't be null");
        }
        if (Objects.nonNull(config.getLanguage())) {
            System.setProperty(DocGlobalConstants.DOC_LANGUAGE, config.getLanguage().getCode());
        } else {
            //default is chinese
            config.setLanguage(DocLanguage.CHINESE);
            System.setProperty(DocGlobalConstants.DOC_LANGUAGE, DocLanguage.CHINESE.getCode());
        }
        if (Objects.isNull(config.getRevisionLogs())) {
            String strTime = DateTimeUtil.long2Str(NOW, DateTimeUtil.DATE_FORMAT_SECOND);
            config.setRevisionLogs(
                RevisionLog.builder()
                    .setRevisionTime(strTime)
                    .setAuthor("@" + System.getProperty("user.name"))
                    .setVersion("v" + strTime)
                    .setRemarks("Created by smart-doc")
                    .setStatus("auto")
            );
        }
        if (StringUtil.isEmpty(config.getFramework())) {
            config.setFramework(FrameworkEnum.SPRING.getFramework());
        }
        if (StringUtil.isEmpty(config.getAuthor())) {
            config.setAuthor(System.getProperty("user.name"));
        }
        if (Objects.isNull(config.getReplace())) {
            config.setReplace(Boolean.TRUE);
        }
    }

    public Map<String, String> setDirectoryLanguageVariable(ApiConfig config, Template mapper) {
        Map<String, String> titleMap = new HashMap<>();
        if (Objects.nonNull(config.getLanguage())) {
            if (DocLanguage.CHINESE.code.equals(config.getLanguage().getCode())) {
                mapper.binding(TemplateVariable.ERROR_LIST_TITLE.getVariable(), DocGlobalConstants.ERROR_CODE_LIST_CN_TITLE);
                mapper.binding(TemplateVariable.DICT_LIST_TITLE.getVariable(), DocGlobalConstants.DICT_CN_TITLE);
                titleMap.put(TemplateVariable.ERROR_LIST_TITLE.getVariable(), DocGlobalConstants.ERROR_CODE_LIST_CN_TITLE);
                titleMap.put(TemplateVariable.DICT_LIST_TITLE.getVariable(), DocGlobalConstants.DICT_CN_TITLE);
            } else {
                mapper.binding(TemplateVariable.ERROR_LIST_TITLE.getVariable(), DocGlobalConstants.ERROR_CODE_LIST_EN_TITLE);
                mapper.binding(TemplateVariable.DICT_LIST_TITLE.getVariable(), DocGlobalConstants.DICT_EN_TITLE);
                titleMap.put(TemplateVariable.ERROR_LIST_TITLE.getVariable(), DocGlobalConstants.ERROR_CODE_LIST_EN_TITLE);
                titleMap.put(TemplateVariable.DICT_LIST_TITLE.getVariable(), DocGlobalConstants.DICT_EN_TITLE);
            }
        } else {
            mapper.binding(TemplateVariable.ERROR_LIST_TITLE.getVariable(), DocGlobalConstants.ERROR_CODE_LIST_CN_TITLE);
            mapper.binding(TemplateVariable.DICT_LIST_TITLE.getVariable(), DocGlobalConstants.DICT_CN_TITLE);
            titleMap.put(TemplateVariable.ERROR_LIST_TITLE.getVariable(), DocGlobalConstants.ERROR_CODE_LIST_CN_TITLE);
            titleMap.put(TemplateVariable.DICT_LIST_TITLE.getVariable(), DocGlobalConstants.DICT_CN_TITLE);
        }
        return titleMap;
    }

    public void setCssCDN(ApiConfig config, Template template) {
        if (DocLanguage.CHINESE.equals(config.getLanguage())) {
            template.binding(TemplateVariable.CSS_CND.getVariable(), CSS_CDN_CH);
        } else {
            template.binding(TemplateVariable.CSS_CND.getVariable(), CSS_CDN);
        }
    }

    public String allInOneDocName(ApiConfig apiConfig, String fileName, String suffix) {
        String allInOneName = apiConfig.getAllInOneDocFileName();
        if (StringUtils.isNotEmpty(apiConfig.getAllInOneDocFileName())) {
            if (allInOneName.endsWith(suffix)) {
                return allInOneName;
            } else {
                return allInOneName + suffix;
            }
        } else if (StringUtil.isNotEmpty(fileName) && fileName.endsWith(suffix)) {
            return fileName;
        } else {
            return fileName + suffix;
        }
    }
}
