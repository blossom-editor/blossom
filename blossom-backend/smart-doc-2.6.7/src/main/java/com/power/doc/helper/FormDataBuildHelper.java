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
package com.power.doc.helper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.power.common.util.RandomUtil;
import com.power.common.util.StringUtil;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.DocTags;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.CustomField;
import com.power.doc.model.DocJavaField;
import com.power.doc.model.FormData;
import com.power.doc.utils.DocClassUtil;
import com.power.doc.utils.DocUtil;
import com.power.doc.utils.JavaClassUtil;
import com.power.doc.utils.JavaClassValidateUtil;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;

/**
 * @author yu 2019/12/25.
 */
public class FormDataBuildHelper {

    /**
     * build form data
     *
     * @param className       class name
     * @param registryClasses Class container
     * @param counter         invoked counter
     * @param builder         ProjectDocConfigBuilder
     * @param pre             pre
     * @return list of FormData
     */
    public static List<FormData> getFormData(String className, Map<String, String> registryClasses, int counter
        , ProjectDocConfigBuilder builder, String pre) {

        if (StringUtil.isEmpty(className)) {
            throw new RuntimeException("Class name can't be null or empty.");
        }
        ApiConfig apiConfig = builder.getApiConfig();
        List<FormData> formDataList = new ArrayList<>();
        if (counter > apiConfig.getRecursionLimit()) {
            return formDataList;
        }
        // Check circular reference
        if (registryClasses.containsKey(className) && counter > registryClasses.size()) {
            return formDataList;
        }
        // Registry class
        registryClasses.put(className, className);
        counter++;
        boolean skipTransientField = apiConfig.isSkipTransientField();
        boolean requestFieldToUnderline = apiConfig.isRequestFieldToUnderline();
        boolean responseFieldToUnderline = apiConfig.isResponseFieldToUnderline();
        String simpleName = DocClassUtil.getSimpleName(className);
        String[] globGicName = DocClassUtil.getSimpleGicName(className);
        JavaClass cls = builder.getJavaProjectBuilder().getClassByName(simpleName);
        List<DocJavaField> fields = JavaClassUtil.getFields(cls, 0, new LinkedHashMap<>());

        if (JavaClassValidateUtil.isPrimitive(simpleName)) {
            FormData formData = new FormData();
            formData.setKey(pre);
            formData.setType("text");
            formData.setValue(StringUtil.removeQuotes(RandomUtil.randomValueByType(className)));
            formDataList.add(formData);
            return formDataList;
        }
        if (JavaClassValidateUtil.isCollection(simpleName) || JavaClassValidateUtil.isArray(simpleName)) {
            String gicName = globGicName[0];
            if (JavaClassValidateUtil.isArray(gicName)) {
                gicName = gicName.substring(0, gicName.indexOf("["));
            }
            if (JavaClassValidateUtil.isPrimitive(gicName)) {
                pre = pre.substring(0, pre.lastIndexOf("."));
            }
            formDataList.addAll(getFormData(gicName, registryClasses, counter, builder, pre + "[]"));
        }
        int n = 0;
        for (DocJavaField docField : fields) {
            JavaField field = docField.getJavaField();
            String fieldName = field.getName();
            String subTypeName = docField.getFullyQualifiedName();
            String fieldGicName = docField.getGenericCanonicalName();
            JavaClass javaClass = field.getType();
            if (field.isStatic() || "this$0".equals(fieldName) ||
                JavaClassValidateUtil.isIgnoreFieldTypes(subTypeName)) {
                continue;
            }
            if (field.isTransient() && skipTransientField) {
                continue;
            }
            if (responseFieldToUnderline || requestFieldToUnderline) {
                fieldName = StringUtil.camelToUnderline(fieldName);
            }
            Map<String, String> tagsMap = DocUtil.getFieldTagsValue(field, docField);
            if (tagsMap.containsKey(DocTags.IGNORE)) {
                continue;
            }
            String typeSimpleName = field.getType().getSimpleName();
            if (JavaClassValidateUtil.isMap(subTypeName)) {
                continue;
            }
            String comment = docField.getComment();
            if (JavaClassValidateUtil.isFile(fieldGicName)) {
                FormData formData = new FormData();
                formData.setKey(pre + fieldName);
                formData.setType("file");
                if (fieldGicName.contains("[]") || fieldGicName.endsWith(">")) {
                    comment = comment + "(array of file)";
                    formData.setType(DocGlobalConstants.PARAM_TYPE_FILE);
                }
                formData.setDescription(comment);
                formData.setValue("");
                formDataList.add(formData);
            } else if (JavaClassValidateUtil.isPrimitive(subTypeName)) {
                String fieldValue;
                if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
                    fieldValue = tagsMap.get(DocTags.MOCK);
                } else {
                    fieldValue = DocUtil.getValByTypeAndFieldName(typeSimpleName, field.getName());
                }
                CustomField customRequestField = builder.getCustomReqFieldMap().get(fieldName);
                // cover request value
                if (Objects.nonNull(customRequestField) && Objects.nonNull(customRequestField.getValue())
                    && JavaClassUtil.isTargetChildClass(simpleName, customRequestField.getOwnerClassName())) {
                    fieldValue = String.valueOf(customRequestField.getValue());
                }
                FormData formData = new FormData();
                formData.setKey(pre + fieldName);
                formData.setType("text");
                formData.setValue(StringUtil.removeQuotes(fieldValue));
                formData.setDescription(comment);
                formDataList.add(formData);
            } else if (javaClass.isEnum()) {
                Object value = JavaClassUtil.getEnumValue(javaClass, Boolean.TRUE);
                if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
                    value = tagsMap.get(DocTags.MOCK);
                }
                FormData formData = new FormData();
                formData.setKey(pre + fieldName);
                formData.setType("text");
                formData.setValue(StringUtil.removeQuotes(String.valueOf(value)));
                formData.setDescription(comment);
                formDataList.add(formData);
            } else if (JavaClassValidateUtil.isCollection(subTypeName) || JavaClassValidateUtil.isArray(subTypeName)) {
                String gNameTemp = field.getType().getGenericCanonicalName();
                String[] gNameArr = DocClassUtil.getSimpleGicName(gNameTemp);
                if (gNameArr.length == 0) {
                    continue;
                }
                String gName = DocClassUtil.getSimpleGicName(gNameTemp)[0];
                if (JavaClassValidateUtil.isPrimitive(gName)) {
                    String fieldValue;
                    if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
                        fieldValue = tagsMap.get(DocTags.MOCK);
                    } else {
                        String val = DocUtil.getValByTypeAndFieldName(gName, field.getName());
                        fieldValue = val + "," + val;
                    }
                    FormData formData = new FormData();
                    formData.setKey(pre + fieldName);
                    formData.setType("text");
                    formData.setValue(fieldValue);
                    formData.setDescription(comment);
                    formDataList.add(formData);
                } else {
                    if (!simpleName.equals(gName)) {
                        if (gName.length() == 1) {
                            int len = globGicName.length;
                            if (len > 0) {
                                String gicName = globGicName[n];
                                if (!JavaClassValidateUtil.isPrimitive(gicName) && !simpleName.equals(gicName)) {
                                    formDataList.addAll(getFormData(gicName, registryClasses, counter, builder, pre + fieldName + "[0]."));
                                }
                            }
                        } else {
                            formDataList.addAll(getFormData(gName, registryClasses, counter, builder, pre + fieldName + "[0]."));
                        }
                    }
                }
            } else if (subTypeName.length() == 1 || DocGlobalConstants.JAVA_OBJECT_FULLY.equals(subTypeName)) {
                //  For Generics,do nothing, spring mvc not support
//                if (n < globGicName.length) {
//                    String gicName = globGicName[n];
//                    formDataList.addAll(getFormData(gicName, registryClasses, counter, builder, pre + fieldName + "."));
//                }
//                n++;
            } else {
                formDataList.addAll(getFormData(fieldGicName, registryClasses, counter, builder, pre + fieldName + "."));
            }
        }
        return formDataList;
    }
}
