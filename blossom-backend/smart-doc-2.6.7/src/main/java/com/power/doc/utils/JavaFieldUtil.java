/*
 * smart-doc
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

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.power.common.util.StringUtil;
import com.power.doc.constants.DocAnnotationConstants;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.DocValidatorAnnotationEnum;
import com.power.doc.model.CustomField;
import com.power.doc.model.DocJavaField;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.expression.AnnotationValue;

/**
 * @author yu 2019/12/21.
 */
public class JavaFieldUtil {

    /**
     * @param fields list of fields
     * @return boolean
     */
    public static boolean checkGenerics(List<DocJavaField> fields) {
        for (DocJavaField field : fields) {
            if (field.getJavaField().getType().getFullyQualifiedName().length() == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param data0          data0
     * @param typeSimpleName typeName
     * @param customField    config field
     */
    public static void buildCustomField(StringBuilder data0, String typeSimpleName, CustomField customField) {
        Object val = customField.getValue();
        if (null != val) {
            if (DocUtil.javaPrimaryType(typeSimpleName)) {
                data0.append(val).append(",");
            } else {
                data0.append(DocUtil.handleJsonStr(String.valueOf(val))).append(",");
            }
        }
    }

    /**
     * @param paramsComments 参数列表
     * @param paramName      参数名称
     * @param typeName       参数数据类型
     * @param simpleTypeName 参数简单数据类型
     * @return mock value
     */
    public static String createMockValue(Map<String, String> paramsComments, String paramName, String typeName, String simpleTypeName) {
        String mockValue = "";
        if (JavaClassValidateUtil.isPrimitive(typeName)) {
            mockValue = paramsComments.get(paramName);
            if (Objects.nonNull(mockValue) && mockValue.contains("|")) {
                mockValue = mockValue.substring(mockValue.lastIndexOf("|") + 1);
            } else {
                mockValue = "";
            }
            if (StringUtil.isEmpty(mockValue)) {
                mockValue = DocUtil.getValByTypeAndFieldName(simpleTypeName, paramName, Boolean.TRUE);
            }
        }
        return mockValue;
    }

    /**
     * @param annotations annotation
     * @return max length
     */
    public static String getParamMaxLength(List<JavaAnnotation> annotations) {
        String maxLength = "";
        for (JavaAnnotation annotation : annotations) {
            String simpleAnnotationName = annotation.getType().getValue();
            AnnotationValue annotationValue = null;
            if (DocAnnotationConstants.MAX.equalsIgnoreCase(simpleAnnotationName)) {
                annotationValue = annotation.getProperty(DocAnnotationConstants.VALUE_PROP);
            }
            if (DocAnnotationConstants.SIZE.equalsIgnoreCase(simpleAnnotationName)) {
                annotationValue = annotation.getProperty(DocAnnotationConstants.MAX);
            }
            if (DocAnnotationConstants.LENGTH.equalsIgnoreCase(simpleAnnotationName)) {
                annotationValue = annotation.getProperty(DocAnnotationConstants.MAX);
            }
            if (!Objects.isNull(annotationValue)) {
                maxLength = annotationValue.toString();
            }
        }
        return maxLength;
    }

    /**
     * getJsr303Comment
     *
     * @param annotations annotations
     * @return Jsr comments
     */
    public static String getJsrComment(List<JavaAnnotation> annotations) {
        StringBuilder sb = new StringBuilder();
        for (JavaAnnotation annotation : annotations) {
            Map<String, AnnotationValue> values = annotation.getPropertyMap();
            String name = annotation.getType().getValue();
            if (DocValidatorAnnotationEnum.listValidatorAnnotations().contains(name)) {
                for (Map.Entry<String, AnnotationValue> m : values.entrySet()) {
                    String value = DocUtil.resolveAnnotationValue(m.getValue());
                    if (DocAnnotationConstants.REGEXP.equals(m.getKey())) {
                        sb.append(m.getKey()).append(": ").append(StringUtil.removeDoubleQuotes(value))
                            .append("; ");
                    }
                    if (DocAnnotationConstants.MAX.equals(m.getKey())) {
                        sb.append(m.getKey()).append(": ").append(StringUtil.removeDoubleQuotes(value))
                            .append("; ");
                    }
                    if (DocAnnotationConstants.LENGTH.equals(m.getKey())) {
                        sb.append(m.getKey()).append(": ").append(StringUtil.removeDoubleQuotes(value))
                            .append("; ");
                    }
                    if (DocAnnotationConstants.SIZE.equals(m.getKey())) {
                        sb.append(m.getKey()).append(": ").append(StringUtil.removeDoubleQuotes(value))
                            .append("; ");
                    }
                }
            }
        }
        if (sb.length() < 1) {
            return DocGlobalConstants.EMPTY;
        }
        StringBuilder finalSb = new StringBuilder();
        finalSb.append("\nValidate[").append(sb).append("]");
        return finalSb.toString();
    }

}
