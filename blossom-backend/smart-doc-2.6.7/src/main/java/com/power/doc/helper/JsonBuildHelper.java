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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.power.common.util.StringUtil;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.DocAnnotationConstants;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.DocTags;
import com.power.doc.constants.ValidatorAnnotations;
import com.power.doc.model.ApiConfig;
import com.power.doc.model.ApiReturn;
import com.power.doc.model.CustomField;
import com.power.doc.model.DocJavaField;
import com.power.doc.model.DocJavaMethod;
import com.power.doc.utils.DocClassUtil;
import com.power.doc.utils.DocUtil;
import com.power.doc.utils.JavaClassUtil;
import com.power.doc.utils.JavaClassValidateUtil;
import com.power.doc.utils.JavaFieldUtil;
import com.power.doc.utils.JsonUtil;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaType;

import static com.power.doc.constants.DocTags.IGNORE_RESPONSE_BODY_ADVICE;


/**
 * @author yu 2019/12/21.
 */
public class JsonBuildHelper extends BaseHelper {

    /**
     * build return json
     *
     * @param docJavaMethod The JavaMethod object
     * @param builder       ProjectDocConfigBuilder builder
     * @return String
     */
    public static String buildReturnJson(DocJavaMethod docJavaMethod, ProjectDocConfigBuilder builder) {
        JavaMethod method = docJavaMethod.getJavaMethod();
        String responseBodyAdvice = null;
        if (Objects.nonNull(builder.getApiConfig().getResponseBodyAdvice())) {
            responseBodyAdvice = builder.getApiConfig().getResponseBodyAdvice().getClassName();
        }
        if (method.getReturns().isVoid() && Objects.isNull(responseBodyAdvice)) {
            return "Return void.";
        }
        DocletTag downloadTag = method.getTagByName(DocTags.DOWNLOAD);
        if (Objects.nonNull(downloadTag)) {
            return "File download.";
        }
        if (method.getReturns().isEnum() && Objects.isNull(responseBodyAdvice)) {
            return StringUtil.removeQuotes(String.valueOf(JavaClassUtil.getEnumValue(method.getReturns(), Boolean.FALSE)));
        }
        if (method.getReturns().isPrimitive() && Objects.isNull(responseBodyAdvice)) {
            String typeName = method.getReturnType().getCanonicalName();
            return StringUtil.removeQuotes(DocUtil.jsonValueByType(typeName));
        }
        if (DocGlobalConstants.JAVA_STRING_FULLY.equals(method.getReturnType().getGenericCanonicalName())
            && Objects.isNull(responseBodyAdvice)) {
            return "string";
        }
        String returnTypeGenericCanonicalName = method.getReturnType().getGenericCanonicalName();
        if (Objects.nonNull(responseBodyAdvice)
            && Objects.isNull(method.getTagByName(IGNORE_RESPONSE_BODY_ADVICE))) {
            if (!returnTypeGenericCanonicalName.startsWith(responseBodyAdvice)) {
                StringBuilder sb = new StringBuilder();
                sb.append(responseBodyAdvice)
                    .append("<")
                    .append(returnTypeGenericCanonicalName).append(">");
                returnTypeGenericCanonicalName = sb.toString();
            }
        }
        ApiReturn apiReturn = DocClassUtil.processReturnType(returnTypeGenericCanonicalName);
        String typeName = apiReturn.getSimpleName();
        if (JavaClassValidateUtil.isFileDownloadResource(typeName)) {
            docJavaMethod.setDownload(true);
            return "File download.";
        }
        Map<String, JavaType> actualTypesMap = docJavaMethod.getActualTypesMap();
        String returnType = apiReturn.getGenericCanonicalName();
        if (Objects.nonNull(actualTypesMap)) {
            for (Map.Entry<String, JavaType> entry : actualTypesMap.entrySet()) {
                typeName = typeName.replace(entry.getKey(), entry.getValue().getCanonicalName());
                returnType = returnType.replace(entry.getKey(), entry.getValue().getCanonicalName());
            }
        }
        if (JavaClassValidateUtil.isPrimitive(typeName)) {
            if (DocGlobalConstants.JAVA_STRING_FULLY.equals(typeName)) {
                return "string";
            }
            return StringUtil.removeQuotes(DocUtil.jsonValueByType(typeName));
        }

        return JsonUtil.toPrettyFormat(buildJson(typeName, returnType, Boolean.TRUE, 0,
            new HashMap<>(), new HashSet<>(0), builder));
    }

    /**
     * @param typeName             type name
     * @param genericCanonicalName genericCanonicalName
     * @param isResp               Response flag
     * @param counter              Recursive counter
     * @param registryClasses      class container
     * @param groupClasses         valid group class
     * @param builder              project config builder
     * @return String
     */
    public static String buildJson(String typeName, String genericCanonicalName,
        boolean isResp, int counter, Map<String, String> registryClasses, Set<String> groupClasses, ProjectDocConfigBuilder builder) {

        Map<String, String> genericMap = new HashMap<>(10);
        JavaClass javaClass = builder.getJavaProjectBuilder().getClassByName(typeName);
        ApiConfig apiConfig = builder.getApiConfig();
        if (counter > apiConfig.getRecursionLimit()) {
            return "{\"$ref\":\"...\"}";
        }
        if (registryClasses.containsKey(typeName) && counter > registryClasses.size()) {
            return "{\"$ref\":\"...\"}";
        }
        int nextLevel = counter + 1;
        registryClasses.put(typeName, typeName);
        if (JavaClassValidateUtil.isMvcIgnoreParams(typeName, builder.getApiConfig().getIgnoreRequestParams())) {
            if (DocGlobalConstants.MODE_AND_VIEW_FULLY.equals(typeName)) {
                return "Forward or redirect to a page view.";
            } else {
                return "Error restful return.";
            }
        }
        if (JavaClassValidateUtil.isPrimitive(typeName)) {
            return StringUtil.removeQuotes(DocUtil.jsonValueByType(typeName));
        }
        if (javaClass.isEnum()) {
            return StringUtil.removeQuotes(String.valueOf(JavaClassUtil.getEnumValue(javaClass, Boolean.FALSE)));
        }
        boolean skipTransientField = apiConfig.isSkipTransientField();
        StringBuilder data0 = new StringBuilder();
        JavaClass cls = builder.getClassByName(typeName);

        data0.append("{");
        String[] globGicName = DocClassUtil.getSimpleGicName(genericCanonicalName);
        if (Objects.isNull(globGicName) || globGicName.length < 1) {
            // obtain generics from parent class
            JavaClass superJavaClass = cls != null ? cls.getSuperJavaClass() : null;
            if (Objects.nonNull(superJavaClass) && !"Object".equals(superJavaClass.getSimpleName())) {
                globGicName = DocClassUtil.getSimpleGicName(superJavaClass.getGenericFullyQualifiedName());
            }
        }
        JavaClassUtil.genericParamMap(genericMap, cls, globGicName);
        StringBuilder data = new StringBuilder();
        if (JavaClassValidateUtil.isCollection(typeName) || JavaClassValidateUtil.isArray(typeName)) {
            data.append("[");
            if (globGicName.length == 0) {
                data.append("{\"object\":\"any object\"}");
                data.append("]");
                return data.toString();
            }
            String gNameTemp = globGicName[0];
            String gName = JavaClassValidateUtil.isArray(gNameTemp) ? gNameTemp.substring(0, gNameTemp.indexOf("[")) : globGicName[0];
            if (DocGlobalConstants.JAVA_OBJECT_FULLY.equals(gName)) {
                data.append("{\"waring\":\"You may use java.util.Object instead of display generics in the List\"}");
            } else if (JavaClassValidateUtil.isPrimitive(gName)) {
                data.append(DocUtil.jsonValueByType(gName)).append(",");
                data.append(DocUtil.jsonValueByType(gName));
            } else if (gName.contains("<")) {
                String simple = DocClassUtil.getSimpleName(gName);
                String json = buildJson(simple, gName, isResp, nextLevel, registryClasses, groupClasses, builder);
                data.append(json);
            } else if (JavaClassValidateUtil.isCollection(gName)) {
                data.append("\"any object\"");
            } else {
                String json = buildJson(gName, gName, isResp, nextLevel, registryClasses, groupClasses, builder);
                data.append(json);
            }
            data.append("]");
            return data.toString();
        } else if (JavaClassValidateUtil.isMap(typeName)) {
            String[] getKeyValType = DocClassUtil.getMapKeyValueType(genericCanonicalName);
            if (getKeyValType.length == 0) {
                data.append("{\"mapKey\":{}}");
                return data.toString();
            }
            if ((!DocGlobalConstants.JAVA_STRING_FULLY.equals(getKeyValType[0])) && apiConfig.isStrict()) {
                throw new RuntimeException("Map's key can only use String for json,but you use " + getKeyValType[0]);
            }
            String gicName = genericCanonicalName.substring(genericCanonicalName.indexOf(",") + 1, genericCanonicalName.lastIndexOf(">"));
            if (DocGlobalConstants.JAVA_OBJECT_FULLY.equals(gicName)) {
                data.append("{").append("\"mapKey\":")
                    .append("{\"waring\":\"You may use java.util.Object for Map value; smart-doc can't be handle.\"}")
                    .append("}");
            } else if (JavaClassValidateUtil.isPrimitive(gicName)) {
                data.append("{").append("\"mapKey1\":").append(DocUtil.jsonValueByType(gicName)).append(",");
                data.append("\"mapKey2\":").append(DocUtil.jsonValueByType(gicName)).append("}");
            } else if (gicName.contains("<")) {
                String simple = DocClassUtil.getSimpleName(gicName);
                String json = buildJson(simple, gicName, isResp, nextLevel, registryClasses, groupClasses, builder);
                data.append("{").append("\"mapKey\":").append(json).append("}");
            } else {
                data.append("{").append("\"mapKey\":")
                    .append(buildJson(gicName, genericCanonicalName, isResp, counter + 1, registryClasses, groupClasses, builder)).append("}");
            }
            return data.toString();
        } else if (DocGlobalConstants.JAVA_OBJECT_FULLY.equals(typeName)) {
            data.append("{\"object\":\" any object\"},");
            // throw new RuntimeException("Please do not return java.lang.Object directly in api interface.");
        } else if (JavaClassValidateUtil.isReactor(typeName)) {
            data.append(buildJson(globGicName[0], typeName, isResp, nextLevel, registryClasses, groupClasses, builder));
            return data.toString();
        } else {
            boolean requestFieldToUnderline = builder.getApiConfig().isRequestFieldToUnderline();
            boolean responseFieldToUnderline = builder.getApiConfig().isResponseFieldToUnderline();
            List<DocJavaField> fields = JavaClassUtil.getFields(cls, 0, new LinkedHashMap<>());
            Map<String, String> ignoreFields = JavaClassUtil.getClassJsonIgnoreFields(cls);
            out:
            for (DocJavaField docField : fields) {
                JavaField field = docField.getJavaField();
                if (field.isTransient() && skipTransientField) {
                    continue;
                }
                String fieldName = docField.getFieldName();
                if (ignoreFields.containsKey(fieldName)) {
                    continue;
                }
                String subTypeName = docField.getFullyQualifiedName();
                if ((responseFieldToUnderline && isResp) || (requestFieldToUnderline && !isResp)) {
                    fieldName = StringUtil.camelToUnderline(fieldName);
                }
                Map<String, String> tagsMap = DocUtil.getFieldTagsValue(field, docField);
                if (!isResp) {
                    if (tagsMap.containsKey(DocTags.IGNORE)) {
                        continue;
                    }
                }
                List<JavaAnnotation> annotations = docField.getAnnotations();
                for (JavaAnnotation annotation : annotations) {
                    String annotationName = annotation.getType().getValue();
                    if (ValidatorAnnotations.NULL.equals(annotationName) && !isResp) {
                        Set<String> groupClassList = JavaClassUtil.getParamGroupJavaClass(annotation);
                        for (String groupClass : groupClassList) {
                            if (groupClasses.contains(groupClass)) {
                                continue out;
                            }
                        }
                    }
                    if (JavaClassValidateUtil.isIgnoreFieldJson(annotation, isResp)) {
                        continue out;
                    }
                    if (DocAnnotationConstants.SHORT_JSON_FIELD.equals(annotationName)) {
                        if (null != annotation.getProperty(DocAnnotationConstants.NAME_PROP)) {
                            fieldName = StringUtil.removeQuotes(annotation.getProperty(DocAnnotationConstants.NAME_PROP).toString());
                        }
                    } else if (DocAnnotationConstants.SHORT_JSON_PROPERTY.equals(annotationName)) {
                        if (null != annotation.getProperty(DocAnnotationConstants.VALUE_PROP)) {
                            fieldName = StringUtil.removeQuotes(annotation.getProperty(DocAnnotationConstants.VALUE_PROP).toString());
                        }
                    }
                }
                String typeSimpleName = field.getType().getSimpleName();
                String fieldGicName = docField.getGenericCanonicalName();
                CustomField.Key key = CustomField.Key.create(docField.getDeclaringClassName(), fieldName);

                CustomField customResponseField = CustomField.nameEquals(key, builder.getCustomRespFieldMap());
                CustomField customRequestField = CustomField.nameEquals(key, builder.getCustomReqFieldMap());
                if (customRequestField != null) {
                    if (JavaClassUtil.isTargetChildClass(typeName, customRequestField.getOwnerClassName()) && (customRequestField.isIgnore())
                        && !isResp) {
                        continue;
                    } else {
                        fieldName = StringUtil.isEmpty(customRequestField.getReplaceName()) ? fieldName : customRequestField.getReplaceName();
                    }
                }
                if (customResponseField != null) {
                    if (JavaClassUtil.isTargetChildClass(typeName, customResponseField.getOwnerClassName()) && (customResponseField.isIgnore())
                        && isResp) {
                        continue;
                    } else {
                        fieldName = StringUtil.isEmpty(customResponseField.getReplaceName()) ? fieldName : customResponseField.getReplaceName();
                    }
                }
                data0.append("\"").append(fieldName).append("\":");
                String fieldValue = getFieldValueFromMock(subTypeName, tagsMap, typeSimpleName);
                if (JavaClassValidateUtil.isPrimitive(subTypeName)) {
                    int data0Length = data0.length();
                    if (StringUtil.isEmpty(fieldValue)) {
                        fieldValue = DocUtil.getValByTypeAndFieldName(typeSimpleName, field.getName());
                    }
                    if (Objects.nonNull(customRequestField) && !isResp && typeName.equals(customRequestField.getOwnerClassName())) {
                        JavaFieldUtil.buildCustomField(data0, typeSimpleName, customRequestField);
                    }
                    if (Objects.nonNull(customResponseField) && isResp && typeName.equals(customResponseField.getOwnerClassName())) {
                        JavaFieldUtil.buildCustomField(data0, typeSimpleName, customResponseField);
                    }
                    if (data0.length() == data0Length) {
                        data0.append(fieldValue).append(",");
                    }
                } else {
                    if (JavaClassValidateUtil.isCollection(subTypeName) || JavaClassValidateUtil.isArray(subTypeName)) {
                        if (StringUtil.isNotEmpty(fieldValue)) {
                            data0.append(fieldValue).append(",");
                            continue;
                        }
                        if (globGicName.length > 0 && "java.util.List".equals(fieldGicName)) {
                            fieldGicName = fieldGicName + "<T>";
                        }
                        if (JavaClassValidateUtil.isArray(subTypeName)) {
                            fieldGicName = fieldGicName.substring(0, fieldGicName.lastIndexOf("["));
                            fieldGicName = "java.util.List<" + fieldGicName + ">";
                        }
                        String gicName = DocClassUtil.getSimpleGicName(fieldGicName)[0];
                        if (DocGlobalConstants.JAVA_STRING_FULLY.equals(gicName)) {
                            data0.append("[").append(DocUtil.jsonValueByType(gicName)).append("]").append(",");
                        } else if (DocGlobalConstants.JAVA_LIST_FULLY.equals(gicName)) {
                            data0.append("[{\"object\":\"any object\"}],");
                        } else if (gicName.length() == 1) {
                            if (globGicName.length == 0) {
                                data0.append("[{\"object\":\"any object\"}],");
                                continue;
                            }
                            String gicName1 = genericMap.get(gicName) == null ? globGicName[0] : genericMap.get(gicName);
                            if (DocGlobalConstants.JAVA_STRING_FULLY.equals(gicName1)) {
                                data0.append("[").append(DocUtil.jsonValueByType(gicName1)).append("]").append(",");
                            } else {
                                if (!typeName.equals(gicName1)) {
                                    data0.append("[").append(
                                            buildJson(DocClassUtil.getSimpleName(gicName1), gicName1, isResp, nextLevel, registryClasses, groupClasses,
                                                builder))
                                        .append("]").append(",");
                                } else {
                                    data0.append("[{\"$ref\":\"..\"}]").append(",");
                                }
                            }
                        } else {
                            if (!typeName.equals(gicName)) {
                                if (JavaClassValidateUtil.isMap(gicName)) {
                                    data0.append("[{\"mapKey\":{}}],");
                                    continue;
                                }
                                JavaClass arraySubClass = builder.getJavaProjectBuilder().getClassByName(gicName);
                                if (arraySubClass.isEnum()) {
                                    Object value = JavaClassUtil.getEnumValue(arraySubClass, Boolean.FALSE);
                                    data0.append("[").append(value).append("],");
                                    continue;
                                }
                                data0.append("[").append(buildJson(gicName, fieldGicName, isResp, nextLevel, registryClasses, groupClasses, builder))
                                    .append("]").append(",");
                            } else {
                                data0.append("[{\"$ref\":\"..\"}]").append(",");
                            }
                        }
                    } else if (JavaClassValidateUtil.isMap(subTypeName)) {
                        if (StringUtil.isNotEmpty(fieldValue)) {
                            data0.append(fieldValue).append(",");
                            continue;
                        }
                        if (JavaClassValidateUtil.isMap(fieldGicName)) {
                            data0.append("{").append("\"mapKey\":{}},");
                            continue;
                        }
                        String gicName = fieldGicName.substring(fieldGicName.indexOf(",") + 1, fieldGicName.indexOf(">"));
                        if (gicName.length() == 1) {
                            String gicName1 = "";
                            if (Objects.nonNull(genericMap.get(gicName))) {
                                gicName1 = genericMap.get(gicName);
                            } else {
                                if (Objects.nonNull(globGicName) && globGicName.length > 0) {
                                    gicName1 = globGicName[0];
                                }
                            }
                            if (DocGlobalConstants.JAVA_STRING_FULLY.equals(gicName1)) {
                                data0.append("{").append("\"mapKey\":").append(DocUtil.jsonValueByType(gicName1)).append("},");
                            } else {
                                if (!typeName.equals(gicName1)) {
                                    data0.append("{").append("\"mapKey\":")
                                        .append(buildJson(DocClassUtil.getSimpleName(gicName1), gicName1, isResp, nextLevel, registryClasses,
                                            groupClasses, builder)).append("},");
                                } else {
                                    data0.append("{\"mapKey\":{}},");
                                }
                            }
                        } else {
                            data0.append("{").append("\"mapKey\":")
                                .append(buildJson(gicName, fieldGicName, isResp, nextLevel, registryClasses, groupClasses, builder))
                                .append("},");
                        }
                    } else if (subTypeName.length() == 1) {
                        if (!typeName.equals(genericCanonicalName)) {
                            String gicName = genericMap.get(subTypeName) == null ? globGicName[0] : genericMap.get(subTypeName);
                            if (JavaClassValidateUtil.isPrimitive(gicName)) {
                                data0.append(DocUtil.jsonValueByType(gicName)).append(",");
                            } else {
                                String simple = DocClassUtil.getSimpleName(gicName);
                                data0.append(buildJson(simple, gicName, isResp, nextLevel, registryClasses, groupClasses, builder)).append(",");
                            }
                        } else {
                            data0.append("{},");
                        }
                    } else if (DocGlobalConstants.JAVA_OBJECT_FULLY.equals(subTypeName)) {
                        if (StringUtil.isNotEmpty(field.getComment())) {
                            // from source code
                            data0.append("{\"object\":\"any object\"},");
                        } else {
                            data0.append("{},");
                        }
                    } else if (typeName.equals(subTypeName)) {
                        data0.append("{\"$ref\":\"...\"}").append(",");
                    } else {
                        javaClass = field.getType();
                        if (javaClass.isEnum()) {
                            // Override old value
                            if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
                                data0.append(tagsMap.get(DocTags.MOCK)).append(",");
                            } else {
                                Object value = JavaClassUtil.getEnumValue(javaClass, Boolean.FALSE);
                                data0.append(value).append(",");
                            }
                        } else {
                            fieldGicName = DocUtil.formatFieldTypeGicName(genericMap, globGicName, fieldGicName);
                            data0.append(buildJson(subTypeName, fieldGicName, isResp, nextLevel, registryClasses, groupClasses, builder)).append(",");
                        }
                    }
                }
            }
        }
        if (data0.toString().contains(",")) {
            data0.deleteCharAt(data0.lastIndexOf(","));
        }
        data0.append("}");
        return data0.toString();
    }
}
