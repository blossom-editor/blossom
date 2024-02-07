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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.power.common.constants.Charset;
import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.HighlightStyle;
import com.power.doc.helper.JavaProjectBuilderHelper;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiConstant;
import com.power.doc.model.ApiDataDictionary;
import com.power.doc.model.ApiErrorCodeDictionary;
import com.power.doc.model.ApiObjectReplacement;
import com.power.doc.model.BodyAdvice;
import com.power.doc.model.CustomField;
import com.power.doc.model.DocJavaField;
import com.power.doc.model.SourceCodePath;
import com.power.doc.utils.JavaClassUtil;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.directorywalker.DirectoryScanner;
import com.thoughtworks.qdox.directorywalker.SuffixFilter;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.parser.ParseException;

import static com.power.doc.constants.DocGlobalConstants.DEFAULT_SERVER_URL;

/**
 * @author yu 2019/12/21.
 */
public class ProjectDocConfigBuilder {

    private static final Logger log = Logger.getLogger(ProjectDocConfigBuilder.class.getName());

    private final JavaProjectBuilder javaProjectBuilder;

    private final Map<String, JavaClass> classFilesMap = new ConcurrentHashMap<>();

    private final Map<String, Class<? extends Enum>> enumClassMap = new ConcurrentHashMap<>();

    private final Map<CustomField.Key, CustomField> customRespFieldMap = new ConcurrentHashMap<>();

    private final Map<CustomField.Key, CustomField> customReqFieldMap = new ConcurrentHashMap<>();

    private final Map<String, String> replaceClassMap = new ConcurrentHashMap<>();

    private final Map<String, String> constantsMap = new ConcurrentHashMap<>();

    private final String serverUrl;

    private final ApiConfig apiConfig;


    public ProjectDocConfigBuilder(ApiConfig apiConfig, JavaProjectBuilder javaProjectBuilder) {
        if (null == apiConfig) {
            throw new NullPointerException("ApiConfig can't be null.");
        }
        this.apiConfig = apiConfig;
        if (Objects.isNull(javaProjectBuilder)) {
            javaProjectBuilder = JavaProjectBuilderHelper.create();
        }

        if (StringUtil.isEmpty(apiConfig.getServerUrl())) {
            this.serverUrl = DEFAULT_SERVER_URL;
        } else {
            this.serverUrl = apiConfig.getServerUrl();
        }
        this.setHighlightStyle();
        javaProjectBuilder.setEncoding(Charset.DEFAULT_CHARSET);
        this.javaProjectBuilder = javaProjectBuilder;
        try {
            this.loadJavaSource(apiConfig.getSourceCodePaths(), this.javaProjectBuilder);
        } catch (Exception e) {
            log.warning(e.getMessage());
        }
        this.initClassFilesMap();
        this.initCustomResponseFieldsMap(apiConfig);
        this.initCustomRequestFieldsMap(apiConfig);
        this.initReplaceClassMap(apiConfig);
        this.initConstants(apiConfig);
        this.initDict(apiConfig);
        this.checkBodyAdvice(apiConfig.getRequestBodyAdvice());
        this.checkBodyAdvice(apiConfig.getResponseBodyAdvice());
    }

    private void initDict(ApiConfig apiConfig) {
        if (enumClassMap.size() == 0) {
            return;
        }
        List<ApiDataDictionary> dataDictionaries = apiConfig.getDataDictionaries();
        if (Objects.isNull(dataDictionaries)) {
            dataDictionaries = new ArrayList<>();
        }

        for (ApiDataDictionary dataDictionary : dataDictionaries) {
            dataDictionary.setEnumImplementSet(getEnumImplementsByInterface(dataDictionary.getEnumClass()));
        }

        List<ApiErrorCodeDictionary> errorCodeDictionaries = apiConfig.getErrorCodeDictionaries();
        if (Objects.isNull(errorCodeDictionaries)) {
            errorCodeDictionaries = new ArrayList<>();
        }

        for (ApiErrorCodeDictionary errorCodeDictionary : errorCodeDictionaries) {
            errorCodeDictionary.setEnumImplementSet(getEnumImplementsByInterface(errorCodeDictionary.getEnumClass()));
        }
    }

    private Set<Class<? extends Enum>> getEnumImplementsByInterface(Class<?> enumClass) {
        if (!enumClass.isInterface()) {
            return Collections.emptySet();
        }
        Set<Class<? extends Enum>> set = new HashSet<>();
        enumClassMap.forEach((k, v) -> {
            if (enumClass.isAssignableFrom(v)) {
                set.add(v);
            }
        });
        return set;
    }

    public JavaClass getClassByName(String simpleName) {
        JavaClass cls = javaProjectBuilder.getClassByName(simpleName);
        List<DocJavaField> fieldList = JavaClassUtil.getFields(cls, 0, new LinkedHashMap<>());
        // handle inner class
        if (Objects.isNull(cls.getFields()) || fieldList.isEmpty()) {
            cls = classFilesMap.get(simpleName);
        } else {
            List<JavaClass> classList = cls.getNestedClasses();
            for (JavaClass javaClass : classList) {
                classFilesMap.put(javaClass.getFullyQualifiedName(), javaClass);
            }
        }
        return cls;
    }

    private void loadJavaSource(List<SourceCodePath> paths, JavaProjectBuilder builder) {
        if (CollectionUtil.isEmpty(paths)) {
            builder.addSourceTree(new File(DocGlobalConstants.PROJECT_CODE_PATH));
        } else {
            for (SourceCodePath path : paths) {
                if (null == path) {
                    continue;
                }
                String strPath = path.getPath();
                if (StringUtil.isNotEmpty(strPath)) {
                    strPath = strPath.replace("\\", "/");
                    loadJavaSource(strPath, builder);
                }
            }
        }
    }

    private void loadJavaSource(String strPath, JavaProjectBuilder builder) {
        DirectoryScanner scanner = new DirectoryScanner(new File(strPath));
        scanner.addFilter(new SuffixFilter(".java"));
        scanner.scan(currentFile -> {
            try {
                builder.addSource(currentFile);
            } catch (ParseException | IOException e) {
                log.warning(e.getMessage());
            }
        });
    }

    private void initClassFilesMap() {
        Collection<JavaClass> javaClasses = javaProjectBuilder.getClasses();
        for (JavaClass cls : javaClasses) {
            if (cls.isEnum()) {
                Class enumClass;
                ClassLoader classLoader = apiConfig.getClassLoader();
                try {
                    if (Objects.isNull(classLoader)) {
                        enumClass = Class.forName(cls.getFullyQualifiedName());
                    } else {
                        enumClass = classLoader.loadClass(cls.getFullyQualifiedName());
                    }
                    enumClassMap.put(cls.getFullyQualifiedName(), enumClass);
                } catch (ClassNotFoundException e) {
                    continue;
                }
            }
            classFilesMap.put(cls.getFullyQualifiedName(), cls);
        }
    }

    private void initCustomResponseFieldsMap(ApiConfig config) {
        if (CollectionUtil.isNotEmpty(config.getCustomResponseFields())) {
            for (CustomField field : config.getCustomResponseFields()) {
                CustomField.Key key = CustomField.Key.create(field.getOwnerClassName(), field.getName());
                customRespFieldMap.put(key, field);
            }
        }
    }

    private void initCustomRequestFieldsMap(ApiConfig config) {
        if (CollectionUtil.isNotEmpty(config.getCustomRequestFields())) {
            for (CustomField field : config.getCustomRequestFields()) {
                CustomField.Key key = CustomField.Key.create(field.getOwnerClassName(), field.getName());
                customReqFieldMap.put(key, field);
            }
        }
    }

    private void initReplaceClassMap(ApiConfig config) {
        if (CollectionUtil.isNotEmpty(config.getApiObjectReplacements())) {
            for (ApiObjectReplacement replace : config.getApiObjectReplacements()) {
                replaceClassMap.put(replace.getClassName(), replace.getReplacementClassName());
            }
        }
    }

    private void initConstants(ApiConfig config) {
        List<ApiConstant> apiConstants;
        if (CollectionUtil.isEmpty(config.getApiConstants())) {
            apiConstants = new ArrayList<>();
        } else {
            apiConstants = config.getApiConstants();
        }
        try {
            for (ApiConstant apiConstant : apiConstants) {
                Class<?> clzz = apiConstant.getConstantsClass();
                if (Objects.isNull(clzz)) {
                    if (StringUtil.isEmpty(apiConstant.getConstantsClassName())) {
                        throw new RuntimeException("Enum class name can't be null.");
                    }
                    clzz = Class.forName(apiConstant.getConstantsClassName());
                }
                constantsMap.putAll(JavaClassUtil.getFinalFieldValue(clzz));
            }
        } catch (ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void checkBodyAdvice(BodyAdvice bodyAdvice) {
        if (Objects.nonNull(bodyAdvice) && StringUtil.isNotEmpty(bodyAdvice.getClassName())) {
            if (Objects.nonNull(bodyAdvice.getWrapperClass())) {
                return;
            }
            try {
                Class.forName(bodyAdvice.getClassName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Can't find class " + bodyAdvice.getClassName() + " for ResponseBodyAdvice.");
            }
        }
    }

    private void setHighlightStyle() {
        String style = apiConfig.getStyle();
        if (DocGlobalConstants.HIGH_LIGHT_DEFAULT_STYLE.equals(style)) {
            // use local css file
            apiConfig.setHighlightStyleLink(DocGlobalConstants.HIGH_LIGHT_CSS_DEFAULT);
            return;
        }
        if (HighlightStyle.containsStyle(style)) {
            apiConfig.setHighlightStyleLink(String.format(DocGlobalConstants.HIGH_LIGHT_CSS_URL_FORMAT, style));
            return;
        }
        Random random = new Random();
        if (DocGlobalConstants.HIGH_LIGHT_CSS_RANDOM_LIGHT.equals(style)) {
            // Eliminate styles that do not match the template
            style = HighlightStyle.randomLight(random);
            if (HighlightStyle.containsStyle(style)) {
                apiConfig.setStyle(style);
                apiConfig.setHighlightStyleLink(String.format(DocGlobalConstants.HIGH_LIGHT_CSS_URL_FORMAT, style));
            } else {
                apiConfig.setStyle(null);
            }
        } else if (DocGlobalConstants.HIGH_LIGHT_CSS_RANDOM_DARK.equals(style)) {
            style = HighlightStyle.randomDark(random);
            if (DocGlobalConstants.HIGH_LIGHT_DEFAULT_STYLE.equals(style)) {
                apiConfig.setHighlightStyleLink(DocGlobalConstants.HIGH_LIGHT_CSS_DEFAULT);
            } else {
                apiConfig.setHighlightStyleLink(String.format(DocGlobalConstants.HIGH_LIGHT_CSS_URL_FORMAT, style));
            }
            apiConfig.setStyle(style);
        } else {
            // Eliminate styles that do not match the template
            apiConfig.setStyle(null);

        }
    }

    public JavaProjectBuilder getJavaProjectBuilder() {
        return javaProjectBuilder;
    }


    public Map<String, JavaClass> getClassFilesMap() {
        return classFilesMap;
    }

    public Map<CustomField.Key, CustomField> getCustomRespFieldMap() {
        return customRespFieldMap;
    }

    public Map<CustomField.Key, CustomField> getCustomReqFieldMap() {
        return customReqFieldMap;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public ApiConfig getApiConfig() {
        return apiConfig;
    }


    public Map<String, String> getReplaceClassMap() {
        return replaceClassMap;
    }

    public Map<String, Class<? extends Enum>> getEnumClassMap() {
        return enumClassMap;
    }

    public Map<String, String> getConstantsMap() {
        return constantsMap;
    }
}
