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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import javax.annotation.Nonnull;

import com.power.common.util.StringUtil;
import com.power.doc.filter.ReturnTypeProcessor;
import com.power.doc.model.ApiReturn;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * Description:
 * Doc class handle util
 *
 * @author yu 2018//14.
 */
public class DocClassUtil {


    /**
     * get class names by generic class name
     *
     * @param typeName generic class name
     * @return array of string
     */
    public static String[] getSimpleGicName(String typeName) {
        if (JavaClassValidateUtil.isCollection(typeName)) {
            typeName = typeName + "<T>";
        } else if (JavaClassValidateUtil.isArray(typeName)) {
            typeName = typeName.substring(0, typeName.lastIndexOf("["));
            typeName = "java.util.List<" + typeName + ">";
        } else if (JavaClassValidateUtil.isMap(typeName)) {
            typeName = typeName + "<String,T>";
        }
        if (typeName.contains("<")) {
            String pre = typeName.substring(0, typeName.indexOf("<"));
            if (JavaClassValidateUtil.isMap(pre)) {
                return getMapKeyValueType(typeName);
            }
            String type = typeName.substring(typeName.indexOf("<") + 1, typeName.lastIndexOf(">"));
            if (JavaClassValidateUtil.isCollection(pre)) {
                return type.split(" ");
            }
            String[] arr = type.split(",");
            return classNameFix(arr);
        } else {
            return new String[0];
        }
    }

    /**
     * Get a simple type name from a generic class name
     *
     * @param gicName Generic class name
     * @return String
     */
    public static String getSimpleName(String gicName) {
        if (gicName.contains("<")) {
            return gicName.substring(0, gicName.indexOf("<"));
        } else {
            return gicName;
        }
    }

    /**
     * Automatic repair of generic split class names
     *
     * @param arr arr of class name
     * @return array of String
     */
    private static String[] classNameFix(String[] arr) {
        List<String> classes = new ArrayList<>();
        List<Integer> indexList = new ArrayList<>();
        int globIndex = 0;
        int length = arr.length;
        for (int i = 0; i < length; i++) {
            if (classes.size() > 0) {
                int index = classes.size() - 1;
                if (!isClassName(classes.get(index))) {
                    globIndex = globIndex + 1;
                    if (globIndex < length) {
                        indexList.add(globIndex);
                        String className = classes.get(index) + "," + arr[globIndex];
                        classes.set(index, className);
                    }
                } else {
                    globIndex = globIndex + 1;
                    if (globIndex < length) {
                        if (isClassName(arr[globIndex])) {
                            indexList.add(globIndex);
                            classes.add(arr[globIndex]);
                        } else {
                            if (!indexList.contains(globIndex) && !indexList.contains(globIndex + 1)) {
                                indexList.add(globIndex);
                                classes.add(arr[globIndex] + "," + arr[globIndex + 1]);
                                globIndex = globIndex + 1;
                                indexList.add(globIndex);
                            }
                        }
                    }
                }
            } else {
                if (isClassName(arr[i])) {
                    indexList.add(i);
                    classes.add(arr[i]);
                } else {
                    if (!indexList.contains(i) && !indexList.contains(i + 1)) {
                        globIndex = i + 1;
                        classes.add(arr[i] + "," + arr[globIndex]);
                        indexList.add(i);
                        indexList.add(i + 1);
                    }
                }
            }
        }
        return classes.toArray(new String[0]);
    }

    /**
     * get map key and value type name populate into array.
     *
     * @param gName generic class name
     * @return array of string
     */
    public static String[] getMapKeyValueType(String gName) {
        if (StringUtil.isEmpty(gName)) {
            return new String[0];
        }
        if (gName.contains("<")) {
            String[] arr = new String[2];
            String key = gName.substring(gName.indexOf("<") + 1, gName.indexOf(","));
            String value = gName.substring(gName.indexOf(",") + 1, gName.lastIndexOf(">"));
            arr[0] = key;
            arr[1] = value;
            return arr;
        } else {
            return new String[0];
        }

    }

    /**
     * Convert the parameter types exported to the api document
     *
     * @param javaTypeName java simple typeName
     * @return String
     */
    public static String processTypeNameForParams(String javaTypeName) {
        if (StringUtil.isEmpty(javaTypeName)) {
            return "object";
        }
        if (javaTypeName.length() == 1) {
            return "object";
        }
        if (javaTypeName.contains("[]")) {
            return "array";
        }
        javaTypeName = javaTypeName.toLowerCase();
        switch (javaTypeName) {
            case "java.lang.string":
            case "string":
            case "char":
            case "java.util.date":
            case "date":
            case "java.util.uuid":
            case "uuid":
            case "localdatetime":
            case "java.time.localdatetime":
            case "java.time.localdate":
            case "java.time.localtime":
            case "java.time.year":
            case "java.time.yearmonth":
            case "java.time.monthday":
            case "java.time.period":
            case "localdate":
            case "offsetdatetime":
            case "localtime":
            case "timestamp":
            case "zoneddatetime":
            case "period":
            case "java.time.zoneddatetime":
            case "java.time.offsetdatetime":
            case "java.sql.timestamp":
            case "java.lang.character":
            case "character":
            case "org.bson.types.objectid":
                return "string";
            case "java.util.list":
            case "list":
            case "java.util.set":
            case "set":
            case "java.util.linkedlist":
            case "linkedlist":
            case "java.util.arraylist":
            case "arraylist":
            case "java.util.treeset":
            case "treeset":
                return "array";
            case "java.lang.byte":
            case "byte":
                return "int8";
            case "java.lang.integer":
            case "integer":
            case "int":
                return "int32";
            case "short":
            case "java.lang.short":
                return "int16";
            case "double":
            case "java.lang.double":
                return "double";
            case "java.lang.long":
            case "long":
                return "int64";
            case "java.lang.float":
            case "float":
                return "float";
            case "java.math.bigdecimal":
            case "bigdecimal":
            case "biginteger":
                return "number";
            case "java.lang.boolean":
            case "boolean":
                return "boolean";
            case "map":
                return "map";
            case "multipartfile":
                return "file";
            default:
                return "object";
        }

    }

    /**
     * process return type
     *
     * @param fullyName fully name
     * @return ApiReturn
     */
    public static ApiReturn processReturnType(String fullyName) {
        ReturnTypeProcessor processor = new ReturnTypeProcessor();
        processor.setTypeName(fullyName);
        return processor.process();
    }

    /**
     * rewrite request param
     *
     * @param typeName param type name
     * @return String
     */
    public static String rewriteRequestParam(String typeName) {
        switch (typeName) {
            case "org.springframework.data.domain.Pageable":
                return "com.power.doc.model.framework.PageableAsQueryParam";
            default:
                return typeName;
        }
    }

    private static boolean isClassName(String className) {
        className = className.replaceAll("[^<>]", "");
        Stack<Character> stack = new Stack<>();
        for (char c : className.toCharArray()) {
            if (c == '<') {
                stack.push('>');
            } else if (stack.isEmpty() || c != stack.pop()) {
                return false;
            }
        }
        return stack.isEmpty();
    }

    /**
     * Get class annotations
     *
     * @param cls JavaClass
     * @return List of JavaAnnotation
     */
    public static List<JavaAnnotation> getAnnotations(JavaClass cls) {
        JavaClass superClass = cls.getSuperJavaClass();
        List<JavaAnnotation> classAnnotations = new ArrayList<>();
        try {
            if (Objects.nonNull(superClass)) {
                classAnnotations.addAll(superClass.getAnnotations());
            }
        } catch (Throwable e) {
            throw new RuntimeException("Could not obtain annotations for class: "+cls.getFullyQualifiedName()+"\n"+e);
        }
        classAnnotations.addAll(cls.getAnnotations());
        return classAnnotations;
    }

    public static <T> T newInstance(@Nonnull Class<T> classWithNoArgsConstructor) {
        try {
            return classWithNoArgsConstructor.getConstructor().newInstance();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException e) {
            throw new IllegalArgumentException("Class must have the NoArgsConstructor");
        }
    }
}
