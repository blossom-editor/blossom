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

import com.power.common.model.EnumDictionary;
import com.power.common.util.CollectionUtil;
import com.power.common.util.StringUtil;
import com.power.doc.builder.ProjectDocConfigBuilder;
import com.power.doc.constants.DocAnnotationConstants;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.DocTags;
import com.power.doc.constants.ValidatorAnnotations;
import com.power.doc.extension.json.PropertyNameHelper;
import com.power.doc.extension.json.PropertyNamingStrategies;
import com.power.doc.model.*;
import com.power.doc.utils.*;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.power.doc.constants.DocGlobalConstants.*;

/**
 * @author yu 2019/12/21.
 */
public class ParamsBuildHelper extends BaseHelper {

    public static List<ApiParam> buildParams(String className, String pre, int level, String isRequired, boolean isResp
            , Map<String, String> registryClasses, ProjectDocConfigBuilder projectBuilder, Set<String> groupClasses
            , int pid, boolean jsonRequest, AtomicInteger atomicInteger) {
        Map<String, String> genericMap = new HashMap<>(10);

        if (StringUtil.isEmpty(className)) {
            throw new RuntimeException("Class name can't be null or empty.");
        }

        ApiConfig apiConfig = projectBuilder.getApiConfig();
        int nextLevel = level + 1;

        // Check circular reference
        List<ApiParam> paramList = new ArrayList<>();
        if (level > apiConfig.getRecursionLimit()) {
            return paramList;
        }
        if (registryClasses.containsKey(className) && level > registryClasses.size()) {
            return paramList;
        }
        boolean skipTransientField = apiConfig.isSkipTransientField();
        boolean isShowJavaType = projectBuilder.getApiConfig().getShowJavaType();
        boolean requestFieldToUnderline = projectBuilder.getApiConfig().isRequestFieldToUnderline();
        boolean responseFieldToUnderline = projectBuilder.getApiConfig().isResponseFieldToUnderline();
        boolean displayActualType = projectBuilder.getApiConfig().isDisplayActualType();
        // Registry class
        registryClasses.put(className, className);
        String simpleName = DocClassUtil.getSimpleName(className);
        String[] globGicName = DocClassUtil.getSimpleGicName(className);
        JavaClass cls = projectBuilder.getClassByName(simpleName);
        if (Objects.isNull(globGicName) || globGicName.length < 1) {
            // obtain generics from parent class
            JavaClass superJavaClass = cls != null ? cls.getSuperJavaClass() : null;
            if (superJavaClass != null && !"Object".equals(superJavaClass.getSimpleName())) {
                globGicName = DocClassUtil.getSimpleGicName(superJavaClass.getGenericFullyQualifiedName());
            }
        }
        PropertyNamingStrategies.NamingBase fieldNameConvert = null;
        if (Objects.nonNull(cls)) {
            List<JavaAnnotation> clsAnnotation = cls.getAnnotations();
            fieldNameConvert = PropertyNameHelper.translate(clsAnnotation);
        }
        JavaClassUtil.genericParamMap(genericMap, cls, globGicName);
        List<DocJavaField> fields = JavaClassUtil.getFields(cls, 0, new LinkedHashMap<>());
        if (JavaClassValidateUtil.isPrimitive(simpleName)) {
            String processedType = isShowJavaType ? simpleName : DocClassUtil.processTypeNameForParams(simpleName.toLowerCase());
            paramList.addAll(primitiveReturnRespComment(processedType, atomicInteger, pid));
        } else if (JavaClassValidateUtil.isCollection(simpleName) || JavaClassValidateUtil.isArray(simpleName)) {
            if (!JavaClassValidateUtil.isCollection(globGicName[0])) {
                String gNameTemp = globGicName[0];
                String gName = JavaClassValidateUtil.isArray(gNameTemp) ? gNameTemp.substring(0, gNameTemp.indexOf("[")) : globGicName[0];
                if (JavaClassValidateUtil.isPrimitive(gName)) {
                    String processedType = isShowJavaType ? simpleName : DocClassUtil.processTypeNameForParams(gName);
                    ApiParam param = ApiParam.of()
                            .setId(atomicOrDefault(atomicInteger, pid + 1))
                            .setField(pre + " -")
                            .setType("array[" + processedType + "]")
                            .setPid(pid)
                            .setDesc("array of " + processedType)
                            .setVersion(DocGlobalConstants.DEFAULT_VERSION)
                            .setRequired(Boolean.parseBoolean(isRequired));
                    paramList.add(param);
                } else {
                    if (JavaClassValidateUtil.isArray(gNameTemp)) {
                        gNameTemp = gNameTemp.substring(0, gNameTemp.indexOf("["));
                    }
                    paramList.addAll(buildParams(gNameTemp, pre, nextLevel, isRequired, isResp
                            , registryClasses, projectBuilder, groupClasses, pid, jsonRequest, atomicInteger));
                }
            }
        } else if (JavaClassValidateUtil.isMap(simpleName)) {
            paramList.addAll(buildMapParam(globGicName, pre, level, isRequired, isResp,
                    registryClasses, projectBuilder, groupClasses, pid, jsonRequest, nextLevel, atomicInteger));
        } else if (DocGlobalConstants.JAVA_OBJECT_FULLY.equals(className)) {
            ApiParam param = ApiParam.of()
                    .setClassName(className)
                    .setId(atomicOrDefault(atomicInteger, pid + 1))
                    .setField(pre + "any object")
                    .setType("object")
                    .setPid(pid)
                    .setDesc(DocGlobalConstants.ANY_OBJECT_MSG)
                    .setVersion(DocGlobalConstants.DEFAULT_VERSION)
                    .setRequired(Boolean.parseBoolean(isRequired));
            paramList.add(param);
        } else if (JavaClassValidateUtil.isReactor(simpleName)) {
            if (globGicName.length > 0) {
                paramList.addAll(buildParams(globGicName[0], pre, nextLevel, isRequired, isResp
                        , registryClasses, projectBuilder, groupClasses, pid, jsonRequest, atomicInteger));
            }
        } else {
            Map<String, String> ignoreFields = JavaClassUtil.getClassJsonIgnoreFields(cls);

            out:
            for (DocJavaField docField : fields) {
                JavaField field = docField.getJavaField();
                String maxLength = JavaFieldUtil.getParamMaxLength(field.getAnnotations());
                StringBuilder comment = new StringBuilder();
                comment.append(docField.getComment());
                if (field.isTransient() && skipTransientField) {
                    continue;
                }
                String fieldName = docField.getFieldName();
                if (Objects.nonNull(fieldNameConvert)) {
                    fieldName = fieldNameConvert.translate(fieldName);
                }
                if (ignoreFields.containsKey(fieldName)) {
                    continue;
                }

                String subTypeName = docField.getFullyQualifiedName();
                if ((responseFieldToUnderline && isResp) || (requestFieldToUnderline && !isResp)) {
                    fieldName = StringUtil.camelToUnderline(fieldName);
                }
                String typeSimpleName = field.getType().getSimpleName();
                String fieldGicName = docField.getGenericCanonicalName();
                List<JavaAnnotation> javaAnnotations = docField.getAnnotations();

                Map<String, String> tagsMap = DocUtil.getFieldTagsValue(field, docField);
                //since tag value
                String since = DocGlobalConstants.DEFAULT_VERSION;

                if (tagsMap.containsKey(DocTags.IGNORE)) {
                    continue;
                } else if (tagsMap.containsKey(DocTags.SINCE)) {
                    since = tagsMap.get(DocTags.SINCE);
                }

                boolean strRequired = false;
                int annotationCounter = 0;
                CustomField.Key key = CustomField.Key.create(docField.getDeclaringClassName(), fieldName);

                CustomField customResponseField = CustomField.nameEquals(key, projectBuilder.getCustomRespFieldMap());
                CustomField customRequestField = CustomField.nameEquals(key, projectBuilder.getCustomReqFieldMap());
                if (customResponseField != null && JavaClassUtil.isTargetChildClass(simpleName, customResponseField.getOwnerClassName())
                        && (customResponseField.isIgnore()) && isResp) {
                    continue;
                }
                if (customRequestField != null && JavaClassUtil.isTargetChildClass(simpleName, customRequestField.getOwnerClassName())
                        && (customRequestField.isIgnore()) && !isResp) {
                    continue;
                }
                for (JavaAnnotation annotation : javaAnnotations) {
                    if (JavaClassValidateUtil.isIgnoreFieldJson(annotation, isResp)) {
                        continue out;
                    }
                    String simpleAnnotationName = annotation.getType().getValue();
                    if (DocAnnotationConstants.SHORT_JSON_FIELD.equals(simpleAnnotationName)) {
                        if (null != annotation.getProperty(DocAnnotationConstants.NAME_PROP)) {
                            fieldName = StringUtil.removeQuotes(annotation.getProperty(DocAnnotationConstants.NAME_PROP).toString());
                        }
                    } else if (DocAnnotationConstants.SHORT_JSON_PROPERTY.equals(simpleAnnotationName)) {
                        if (null != annotation.getProperty(DocAnnotationConstants.VALUE_PROP)) {
                            fieldName = StringUtil.removeQuotes(annotation.getProperty(DocAnnotationConstants.VALUE_PROP).toString());
                        }
                    } else if (ValidatorAnnotations.NULL.equals(simpleAnnotationName) && !isResp) {
                        Set<String> groupClassList = JavaClassUtil.getParamGroupJavaClass(annotation);
                        for (String javaClass : groupClassList) {
                            if (groupClasses.contains(javaClass)) {
                                continue out;
                            }
                        }
                    } else if (JavaClassValidateUtil.isJSR303Required(simpleAnnotationName) && !isResp) {

                        annotationCounter++;
                        boolean hasGroup = false;
                        Set<String> groupClassList = JavaClassUtil.getParamGroupJavaClass(annotation);
                        for (String javaClass : groupClassList) {
                            if (groupClasses.contains(javaClass)) {
                                hasGroup = true;
                                break;
                            }
                        }
                        if (hasGroup) {
                            strRequired = true;
                        } else if (CollectionUtil.isEmpty(groupClasses)) {
                            strRequired = true;
                        }
                    }
                }
                comment.append(JavaFieldUtil.getJsrComment(javaAnnotations));
                String fieldValue = BaseHelper.getFieldValueFromMock(subTypeName, tagsMap, typeSimpleName);
                if (annotationCounter < 1) {
                    if (tagsMap.containsKey(DocTags.REQUIRED)) {
                        strRequired = true;
                    }
                }

                // cover response value
                if (Objects.nonNull(customResponseField) && isResp && Objects.nonNull(customResponseField.getValue())
                        && JavaClassUtil.isTargetChildClass(simpleName, customResponseField.getOwnerClassName())) {
                    fieldValue = String.valueOf(customResponseField.getValue());
                }
                // cover request value
                if (Objects.nonNull(customRequestField) && !isResp && Objects.nonNull(customRequestField.getValue())
                        && JavaClassUtil.isTargetChildClass(simpleName, customRequestField.getOwnerClassName())) {
                    fieldValue = String.valueOf(customRequestField.getValue());
                }
                //cover required
                if (customRequestField != null && !isResp && JavaClassUtil.isTargetChildClass(simpleName, customRequestField.getOwnerClassName())
                        && customRequestField.isRequire()) {
                    strRequired = true;
                }
                //cover comment
                if (null != customRequestField && StringUtil.isNotEmpty(customRequestField.getDesc())
                        && JavaClassUtil.isTargetChildClass(simpleName, customRequestField.getOwnerClassName()) && !isResp) {
                    comment = new StringBuilder(customRequestField.getDesc());
                }
                if (null != customResponseField && StringUtil.isNotEmpty(customResponseField.getDesc())
                        && JavaClassUtil.isTargetChildClass(simpleName, customResponseField.getOwnerClassName()) && isResp) {
                    comment = new StringBuilder(customResponseField.getDesc());
                }
                //cover fieldName
                if (null != customRequestField && StringUtil.isNotEmpty(customRequestField.getReplaceName())
                        && JavaClassUtil.isTargetChildClass(simpleName, customRequestField.getOwnerClassName()) && !isResp) {
                    fieldName = customRequestField.getReplaceName();
                }
                if (null != customResponseField && StringUtil.isNotEmpty(customResponseField.getReplaceName())
                        && JavaClassUtil.isTargetChildClass(simpleName, customResponseField.getOwnerClassName()) && isResp) {
                    fieldName = customResponseField.getReplaceName();
                }
                // file
                if (JavaClassValidateUtil.isFile(fieldGicName)) {
                    ApiParam param = ApiParam.of().setField(pre + fieldName).setType("file")
                            .setClassName(className)
                            .setPid(pid)
                            .setId(atomicOrDefault(atomicInteger, paramList.size() + pid + 1))
                            .setMaxLength(maxLength)
                            .setDesc(comment.toString())
                            .setRequired(Boolean.parseBoolean(isRequired))
                            .setVersion(since);
                    if (fieldGicName.contains("[]") || fieldGicName.endsWith(">")) {
                        param.setType(DocGlobalConstants.PARAM_TYPE_FILE);
                        param.setDesc(comment.append("(array of file)").toString());
                        param.setHasItems(true);
                    }
                    paramList.add(param);
                    continue;
                }
                if (JavaClassValidateUtil.isPrimitive(subTypeName)) {
                    if (StringUtil.isEmpty(fieldValue)) {
                        fieldValue = DocUtil.getValByTypeAndFieldName(subTypeName, field.getName());
                    }
                    ApiParam param = ApiParam.of().setClassName(className).setField(pre + fieldName);
                    param.setPid(pid).setMaxLength(maxLength).setValue(fieldValue);
                    param.setId(atomicOrDefault(atomicInteger, paramList.size() + param.getPid() + 1));
                    String processedType = isShowJavaType ? subTypeName : DocClassUtil.processTypeNameForParams(subTypeName.toLowerCase());
                    param.setType(processedType);
                    // handle param
                    commonHandleParam(paramList, param, isRequired, comment.toString(), since, strRequired);

                    JavaClass enumClass = ParamUtil.handleSeeEnum(param, field, projectBuilder, jsonRequest, tagsMap);
                    if (Objects.nonNull(enumClass)) {
                        String enumClassComment = EMPTY;
                        if (StringUtil.isNotEmpty(enumClass.getComment())) {
                            enumClassComment = enumClass.getComment();
                        }
                        comment = new StringBuilder(StringUtils.isEmpty(comment.toString()) ? enumClassComment : comment.toString());
                        String enumComment = handleEnumComment(enumClass, projectBuilder);
                        param.setDesc(comment + enumComment);
                    }
                } else {
                    String appendComment = "";
                    if (displayActualType) {
                        if (globGicName.length > 0) {
                            String gicName = genericMap.get(subTypeName) != null ? genericMap.get(subTypeName) : globGicName[0];
                            if (!simpleName.equals(gicName)) {
                                appendComment = " (类: " + JavaClassUtil.getClassSimpleName(gicName) + ")";
                            }
                        }
                        if (Objects.nonNull(docField.getActualJavaType())) {
                            appendComment = " (类: " + JavaClassUtil.getClassSimpleName(docField.getActualJavaType()) + ")";
                        }
                    }

                    StringBuilder preBuilder = new StringBuilder();
                    for (int j = 0; j < level; j++) {
                        preBuilder.append(DocGlobalConstants.FIELD_SPACE);
                    }
                    preBuilder.append("└─ ");
                    int fieldPid;
                    ApiParam param = ApiParam.of().setField(pre + fieldName).setClassName(className).setPid(pid).setMaxLength(maxLength);
                    param.setId(atomicOrDefault(atomicInteger, paramList.size() + param.getPid() + 1));
                    String processedType;
                    if (typeSimpleName.length() == 1) {
                        String gicName = DocGlobalConstants.JAVA_OBJECT_FULLY;
                        if (Objects.nonNull(genericMap.get(typeSimpleName))) {
                            gicName = genericMap.get(subTypeName);
                        } else {
                            if (globGicName.length > 0) {
                                gicName = globGicName[0];
                            }
                        }
                        if (JavaClassValidateUtil.isPrimitive(gicName)) {
                            processedType = DocClassUtil.processTypeNameForParams(gicName);
                        } else {
                            processedType = DocClassUtil.processTypeNameForParams(typeSimpleName.toLowerCase());
                        }
                    } else {
                        processedType = isShowJavaType ? typeSimpleName : DocClassUtil.processTypeNameForParams(typeSimpleName.toLowerCase());
                    }
                    param.setType(processedType);
                    JavaClass javaClass = field.getType();
                    if (javaClass.isEnum()) {
                        comment.append(handleEnumComment(javaClass, projectBuilder));
                        ParamUtil.handleSeeEnum(param, field, projectBuilder, jsonRequest, tagsMap);
                        // hand Param
                        commonHandleParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
                    } else if (JavaClassValidateUtil.isCollection(subTypeName) || JavaClassValidateUtil.isArray(subTypeName)) {
                        if (isShowJavaType) {
                            // rpc
                            param.setType(subTypeName);
                        } else {
                            param.setType("array");
                        }
                        if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
                            param.setValue(fieldValue);
                        }
                        if (globGicName.length > 0 && "java.util.List".equals(fieldGicName)) {
                            // no generic, just object
                            fieldGicName = fieldGicName + "<" + DocGlobalConstants.JAVA_OBJECT_FULLY + ">";
                        }
                        if (JavaClassValidateUtil.isArray(subTypeName)) {
                            fieldGicName = fieldGicName.substring(0, fieldGicName.lastIndexOf("["));
                            fieldGicName = "java.util.List<" + fieldGicName + ">";
                        }
                        String[] gNameArr = DocClassUtil.getSimpleGicName(fieldGicName);
                        if (gNameArr.length == 0) {
                            continue;
                        }
                        if (gNameArr.length > 0) {
                            String gName = DocClassUtil.getSimpleGicName(fieldGicName)[0];
                            JavaClass javaClass1 = projectBuilder.getJavaProjectBuilder().getClassByName(gName);
                            comment.append(handleEnumComment(javaClass1, projectBuilder));
                        }
                        String gName = gNameArr[0];
                        if (JavaClassValidateUtil.isPrimitive(gName)) {
                            String builder = DocUtil.jsonValueByType(gName) + "," + DocUtil.jsonValueByType(gName);

                            if (StringUtil.isEmpty(fieldValue)) {
                                param.setValue(DocUtil.handleJsonStr(builder));
                            } else {
                                param.setValue(fieldValue);
                            }
                            commonHandleParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
                        } else {
                            commonHandleParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
                            fieldPid = Optional.ofNullable(atomicInteger).isPresent() ? param.getId() : paramList.size() + pid;
                            if (!simpleName.equals(gName)) {
                                JavaClass arraySubClass = projectBuilder.getJavaProjectBuilder().getClassByName(gName);
                                if (arraySubClass.isEnum()) {
                                    Object value = JavaClassUtil.getEnumValue(arraySubClass, Boolean.FALSE);
                                    param.setValue("[\"" + value + "\"]")
                                            .setEnumInfo(JavaClassUtil.getEnumInfo(arraySubClass, projectBuilder))
                                            .setEnumValues(JavaClassUtil.getEnumValues(arraySubClass));
                                } else if (gName.length() == 1) {
                                    // handle generic
                                    int len = globGicName.length;
                                    if (len < 1) {
                                        continue;
                                    }
                                    String gicName = genericMap.get(gName) != null ? genericMap.get(gName) : globGicName[0];

                                    if (!JavaClassValidateUtil.isPrimitive(gicName) && !simpleName.equals(gicName)) {
                                        paramList.addAll(buildParams(gicName, preBuilder.toString(), nextLevel, isRequired
                                                , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest, atomicInteger));
                                    }
                                } else {
                                    paramList.addAll(buildParams(gName, preBuilder.toString(), nextLevel, isRequired
                                            , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest, atomicInteger));
                                }
                            } else {
                                param.setSelfReferenceLoop(true);
                            }
                        }

                    } else if (JavaClassValidateUtil.isMap(subTypeName)) {
                        if (tagsMap.containsKey(DocTags.MOCK) && StringUtil.isNotEmpty(tagsMap.get(DocTags.MOCK))) {
                            param.setType("map");
                            param.setValue(fieldValue);
                        }
                        commonHandleParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
                        fieldPid = Optional.ofNullable(atomicInteger).isPresent() ? param.getId() : paramList.size() + pid;
                        String valType = DocClassUtil.getMapKeyValueType(fieldGicName).length == 0 ? fieldGicName
                                : DocClassUtil.getMapKeyValueType(fieldGicName)[1];
                        if (JavaClassValidateUtil.isMap(fieldGicName) || JAVA_OBJECT_FULLY.equals(valType)) {
                            ApiParam param1 = ApiParam.of()
                                    .setField(preBuilder.toString() + "any object")
                                    .setId(atomicOrDefault(atomicInteger, fieldPid + 1)).setPid(fieldPid)
                                    .setClassName(className)
                                    .setMaxLength(maxLength)
                                    .setType("object")
                                    .setDesc(DocGlobalConstants.ANY_OBJECT_MSG)
                                    .setVersion(DocGlobalConstants.DEFAULT_VERSION);
                            paramList.add(param1);
                            continue;
                        }
                        if (!JavaClassValidateUtil.isPrimitive(valType)) {
                            if (valType.length() == 1) {
                                String gicName = genericMap.get(valType);
                                if (!JavaClassValidateUtil.isPrimitive(gicName) && !simpleName.equals(gicName)) {
                                    paramList.addAll(buildParams(gicName, preBuilder.toString(), nextLevel, isRequired
                                            , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest, atomicInteger));
                                }
                            } else {
                                paramList.addAll(buildParams(valType, preBuilder.toString(), nextLevel, isRequired
                                        , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest, atomicInteger));
                            }
                        }
                    } else if (DocGlobalConstants.JAVA_OBJECT_FULLY.equals(subTypeName)) {
                        if (StringUtil.isEmpty(param.getDesc())) {
                            param.setDesc(DocGlobalConstants.ANY_OBJECT_MSG);
                        }
                        commonHandleParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
                    } else if (subTypeName.length() == 1) {
                        commonHandleParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
                        fieldPid = Optional.ofNullable(atomicInteger).isPresent() ? param.getId() : paramList.size() + pid;
                        // handle java generic or object
                        if (!simpleName.equals(className)) {
                            if (globGicName.length > 0) {
                                String gicName = genericMap.get(subTypeName) != null ? genericMap.get(subTypeName) : globGicName[0];
                                String simple = DocClassUtil.getSimpleName(gicName);
                                // set type array
                                if (JavaClassValidateUtil.isArray(gicName)) {
                                    param.setType(ARRAY);
                                }
                                if (JavaClassValidateUtil.isPrimitive(simple)) {
                                    //do nothing
                                } else if (gicName.contains("<")) {
                                    if (JavaClassValidateUtil.isCollection(simple)) {
                                        param.setType(ARRAY);
                                        String gName = DocClassUtil.getSimpleGicName(gicName)[0];
                                        if (!JavaClassValidateUtil.isPrimitive(gName)) {
                                            paramList.addAll(buildParams(gName, preBuilder.toString(), nextLevel, isRequired
                                                    , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest, atomicInteger));
                                        }
                                    } else {
                                        paramList.addAll(buildParams(gicName, preBuilder.toString(), nextLevel, isRequired
                                                , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest, atomicInteger));
                                    }
                                } else {
                                    paramList.addAll(buildParams(gicName, preBuilder.toString(), nextLevel, isRequired
                                            , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest, atomicInteger));
                                }
                            } else {
                                paramList.addAll(buildParams(subTypeName, preBuilder.toString(), nextLevel, isRequired
                                        , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest, atomicInteger));
                            }
                        }
                    } else if (simpleName.equals(subTypeName)) {
                        // reference self
                        ApiParam param1 = ApiParam.of()
                                .setField(pre + fieldName)
                                .setPid(pid)
                                .setId(atomicOrDefault(atomicInteger, paramList.size() + pid + 1))
                                .setClassName(subTypeName)
                                .setMaxLength(maxLength)
                                .setType("object")
                                .setDesc(comment.append(" $ref... self").toString())
                                .setVersion(DocGlobalConstants.DEFAULT_VERSION);
                        paramList.add(param1);
                    } else {
                        commonHandleParam(paramList, param, isRequired, comment + appendComment, since, strRequired);
                        fieldGicName = DocUtil.formatFieldTypeGicName(genericMap, globGicName, fieldGicName);
                        fieldPid = Optional.ofNullable(atomicInteger).isPresent() ? param.getId() : paramList.size() + pid;
                        paramList.addAll(buildParams(fieldGicName, preBuilder.toString(), nextLevel, isRequired
                                , isResp, registryClasses, projectBuilder, groupClasses, fieldPid, jsonRequest, atomicInteger));

                    }
                }
            }//end field
        }
        return paramList;
    }

    private static List<ApiParam> buildMapParam(String[] globGicName, String pre, int level, String isRequired, boolean isResp,
                                                Map<String, String> registryClasses,
                                                ProjectDocConfigBuilder projectBuilder, Set<String> groupClasses, int pid, boolean jsonRequest,
                                                int nextLevel, AtomicInteger atomicInteger) {
        if (globGicName.length != 2) {
            return Collections.emptyList();
        }

        // mock map key param
        String mapKeySimpleName = DocClassUtil.getSimpleName(globGicName[0]);
        String valueSimpleName = DocClassUtil.getSimpleName(globGicName[1]);

        List<ApiParam> paramList = new ArrayList<>();
        if (JavaClassValidateUtil.isPrimitive(mapKeySimpleName)) {
            boolean isShowJavaType = projectBuilder.getApiConfig().getShowJavaType();
            String valueSimpleNameType = isShowJavaType ? valueSimpleName : DocClassUtil.processTypeNameForParams(valueSimpleName.toLowerCase());
            ApiParam apiParam = ApiParam.of().setField(pre + "mapKey")
                    .setType(valueSimpleNameType)
                    .setClassName(valueSimpleName)
                    .setDesc(Optional.ofNullable(projectBuilder.getClassByName(valueSimpleName)).map(JavaClass::getComment).orElse("A map key."))
                    .setVersion(DEFAULT_VERSION)
                    .setPid(pid)
                    .setId(atomicOrDefault(atomicInteger, ++pid));
            paramList.add(apiParam);
        }
        // build param when map value is not primitive
        if (JavaClassValidateUtil.isPrimitive(valueSimpleName)) {
            return paramList;
        }
        StringBuilder preBuilder = new StringBuilder();
        for (int j = 0; j < level; j++) {
            preBuilder.append(DocGlobalConstants.FIELD_SPACE);
        }
        preBuilder.append("└─ ");
        paramList.addAll(buildParams(globGicName[1], preBuilder.toString(), ++nextLevel, isRequired, isResp
                , registryClasses, projectBuilder, groupClasses, pid, jsonRequest, atomicInteger));
        return paramList;
    }

    public static String dictionaryListComment(List<EnumDictionary> enumDataDict) {
        return enumDataDict.stream().map(apiDataDictionary ->
                apiDataDictionary.getName() + "-(\"" + apiDataDictionary.getValue() + "\",\"" + apiDataDictionary.getDesc() + "\")"
        ).collect(Collectors.joining(","));
    }

    public static List<ApiParam> primitiveReturnRespComment(String typeName, AtomicInteger atomicInteger, int pid) {
        String comments = "Return " + typeName + ".";
        ApiParam apiParam = ApiParam.of().setClassName(typeName)
                .setId(atomicOrDefault(atomicInteger, pid + 1))
                .setField("-")
                .setPid(pid)
                .setType(typeName)
                .setDesc(comments)
                .setVersion(DocGlobalConstants.DEFAULT_VERSION);

        List<ApiParam> paramList = new ArrayList<>();
        paramList.add(apiParam);
        return paramList;
    }

    private static void commonHandleParam(List<ApiParam> paramList, ApiParam param, String isRequired
            , String comment, String since, boolean strRequired) {
        if (StringUtil.isEmpty(isRequired)) {
            param.setDesc(comment).setVersion(since);
        } else {
            param.setDesc(comment).setVersion(since).setRequired(strRequired);
        }
        paramList.add(param);
    }

    private static String handleEnumComment(JavaClass javaClass, ProjectDocConfigBuilder projectBuilder) {
        String comment = "";
        if (!javaClass.isEnum()) {
            return comment;
        }
        String enumComments = javaClass.getComment();
        if (Boolean.TRUE.equals(projectBuilder.getApiConfig().getInlineEnum())) {
            ApiDataDictionary dataDictionary = projectBuilder.getApiConfig().getDataDictionary(javaClass.getCanonicalName());
            if (Objects.isNull(dataDictionary)) {
                comment = comment + "<br/>[Enum values:<br/>" + JavaClassUtil.getEnumParams(javaClass) + "]";
            } else {
                Class enumClass = dataDictionary.getEnumClass();
                if (enumClass.isInterface()) {
                    ClassLoader classLoader = projectBuilder.getApiConfig().getClassLoader();
                    try {
                        enumClass = classLoader.loadClass(javaClass.getFullyQualifiedName());
                    } catch (ClassNotFoundException e) {
                        return comment;
                    }
                }
                comment = comment + "<br/>[Enum:" + dictionaryListComment(dataDictionary.getEnumDataDict(enumClass)) + "]";
            }
        } else {
            if (StringUtil.isNotEmpty(enumComments)) {
                comment = comment + "<br/>(See: " + enumComments + ")";
            }
            comment = StringUtil.removeQuotes(comment);
        }
        return comment;
    }

    private static int atomicOrDefault(AtomicInteger atomicInteger, int defaultVal) {
        if (null != atomicInteger) {
            return atomicInteger.incrementAndGet();
        }
        return defaultVal;
    }
}
