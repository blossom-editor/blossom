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

import com.mifmif.common.regex.Generex;
import com.power.common.util.*;
import com.power.doc.constants.*;
import com.power.doc.extension.dict.DictionaryValuesResolver;
import com.power.doc.model.*;
import com.power.doc.model.request.RequestMapping;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.*;
import com.thoughtworks.qdox.model.expression.Add;
import com.thoughtworks.qdox.model.expression.AnnotationValue;
import com.thoughtworks.qdox.model.expression.Expression;
import com.thoughtworks.qdox.model.expression.FieldRef;
import net.datafaker.Faker;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.power.doc.constants.DocGlobalConstants.JSON_CONTENT_TYPE;
import static com.power.doc.constants.DocGlobalConstants.NO_COMMENTS_FOUND;
import static com.power.doc.model.SystemPlaceholders.*;

/**
 * Description:
 * DocUtil
 *
 * @author yu 2018/06/11.
 */
public class DocUtil {

    private static Faker faker = new Faker(new Locale("en-US"));
    private static Faker enFaker = new Faker(new Locale("en-US"));

    private static Map<String, String> fieldValue = new LinkedHashMap<>();

    static {
        fieldValue.put("uuid-string", UUID.randomUUID().toString());
        fieldValue.put("traceid-string", UUID.randomUUID().toString());
        fieldValue.put("id-string", String.valueOf(RandomUtil.randomInt(1, 200)));
        fieldValue.put("ids-string", String.valueOf(RandomUtil.randomInt(1, 200)));
        fieldValue.put("nickname-string", enFaker.name().username());
        fieldValue.put("hostname-string", faker.internet().ipV4Address());
        fieldValue.put("name-string", faker.name().username());
        fieldValue.put("author-string", faker.book().author());
        fieldValue.put("url-string", faker.internet().url());
        fieldValue.put("username-string", faker.name().username());
        fieldValue.put("index-int", "1");
        fieldValue.put("index-integer", "1");
        fieldValue.put("page-int", "1");
        fieldValue.put("page-integer", "1");
        fieldValue.put("age-int", String.valueOf(RandomUtil.randomInt(0, 70)));
        fieldValue.put("age-integer", String.valueOf(RandomUtil.randomInt(0, 70)));
        fieldValue.put("email-string", faker.internet().emailAddress());
        fieldValue.put("domain-string", faker.internet().domainName());
        fieldValue.put("phone-string", faker.phoneNumber().cellPhone());
        fieldValue.put("mobile-string", faker.phoneNumber().cellPhone());
        fieldValue.put("telephone-string", faker.phoneNumber().phoneNumber());
        fieldValue.put("address-string", faker.address().fullAddress().replace(",", "，"));
        fieldValue.put("ip-string", faker.internet().ipV4Address());
        fieldValue.put("ipv4-string", faker.internet().ipV4Address());
        fieldValue.put("ipv6-string", faker.internet().ipV6Address());
        fieldValue.put("company-string", faker.company().name());
        fieldValue.put("timestamp-long", String.valueOf(System.currentTimeMillis()));
        fieldValue.put("timestamp-string", DateTimeUtil.dateToStr(new Date(), DateTimeUtil.DATE_FORMAT_SECOND));
        fieldValue.put("time-long", String.valueOf(System.currentTimeMillis()));
        fieldValue.put("time-string", DateTimeUtil.dateToStr(new Date(), DateTimeUtil.DATE_FORMAT_SECOND));
        fieldValue.put("birthday-string", DateTimeUtil.dateToStr(new Date(), DateTimeUtil.DATE_FORMAT_DAY));
        fieldValue.put("birthday-long", String.valueOf(System.currentTimeMillis()));
        fieldValue.put("code-string", String.valueOf(RandomUtil.randomInt(100, 99999)));
        fieldValue.put("message-string", "success,fail".split(",")[RandomUtil.randomInt(0, 1)]);
        fieldValue.put("date-string", DateTimeUtil.dateToStr(new Date(), DateTimeUtil.DATE_FORMAT_DAY));
        fieldValue.put("date-date", DateTimeUtil.dateToStr(new Date(), DateTimeUtil.DATE_FORMAT_DAY));
        fieldValue.put("begintime-date", DateTimeUtil.dateToStr(new Date(), DateTimeUtil.DATE_FORMAT_DAY));
        fieldValue.put("endtime-date", DateTimeUtil.dateToStr(new Date(), DateTimeUtil.DATE_FORMAT_DAY));
        fieldValue.put("time-localtime", LocalDateTime.now().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        fieldValue.put("state-int", String.valueOf(RandomUtil.randomInt(0, 10)));
        fieldValue.put("state-integer", String.valueOf(RandomUtil.randomInt(0, 10)));
        fieldValue.put("flag-int", String.valueOf(RandomUtil.randomInt(0, 10)));
        fieldValue.put("flag-integer", String.valueOf(RandomUtil.randomInt(0, 10)));
        fieldValue.put("flag-boolean", "true");
        fieldValue.put("flag-Boolean", "false");
        fieldValue.put("idcard-string", IDCardUtil.getIdCard());
        fieldValue.put("sex-int", String.valueOf(RandomUtil.randomInt(0, 2)));
        fieldValue.put("sex-integer", String.valueOf(RandomUtil.randomInt(0, 2)));
        fieldValue.put("gender-int", String.valueOf(RandomUtil.randomInt(0, 2)));
        fieldValue.put("gender-integer", String.valueOf(RandomUtil.randomInt(0, 2)));
        fieldValue.put("limit-int", "10");
        fieldValue.put("limit-integer", "10");
        fieldValue.put("size-int", "10");
        fieldValue.put("size-integer", "10");

        fieldValue.put("offset-int", "1");
        fieldValue.put("offset-integer", "1");
        fieldValue.put("offset-long", "1");
        fieldValue.put("version-string", enFaker.app().version());
    }

    /**
     * Generate a random value based on java type name.
     *
     * @param typeName field type name
     * @return random value
     */
    public static String jsonValueByType(String typeName) {
        String type = typeName.contains(".") ? typeName.substring(typeName.lastIndexOf(".") + 1) : typeName;
        String value = RandomUtil.randomValueByType(type);
        if (javaPrimaryType(type)) {
            return value;
        } else if ("Void".equals(type)) {
            return "null";
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("\"").append(value).append("\"");
            return builder.toString();
        }
    }


    /**
     * Generate random field values based on field field names and type.
     *
     * @param typeName  field type name
     * @param filedName field name
     * @return random value
     */
    public static String getValByTypeAndFieldName(String typeName, String filedName) {
        boolean isArray = true;
        String type = typeName.contains("java.lang") ? typeName.substring(typeName.lastIndexOf(".") + 1) : typeName;
        String key = filedName.toLowerCase() + "-" + type.toLowerCase();
        StringBuilder value = null;
        if (!type.contains("[")) {
            isArray = false;
        }
        for (Map.Entry<String, String> entry : fieldValue.entrySet()) {
            if (key.contains(entry.getKey())) {
                value = new StringBuilder(entry.getValue());
                if (isArray) {
                    for (int i = 0; i < 2; i++) {
                        value.append(",").append(entry.getValue());
                    }
                }
                break;
            }
        }
        if (Objects.isNull(value)) {
            return jsonValueByType(typeName);
        } else {
            if (javaPrimaryType(type)) {
                return value.toString();
            } else {
                return handleJsonStr(value.toString());
            }
        }
    }

    /**
     * 移除字符串的双引号
     *
     * @param type0                 类型
     * @param filedName             字段名称
     * @param removeDoubleQuotation 移除标志
     * @return String
     */
    public static String getValByTypeAndFieldName(String type0, String filedName, boolean removeDoubleQuotation) {
        if (removeDoubleQuotation) {
            return getValByTypeAndFieldName(type0, filedName).replace("\"", "");
        } else {
            return getValByTypeAndFieldName(type0, filedName);
        }
    }


    /**
     * match controller package
     *
     * @param packageFilters package filter
     * @param controllerName controller name
     * @return boolean
     */
    public static boolean isMatch(String packageFilters, String controllerName) {
        if (StringUtil.isEmpty(packageFilters)) {
            return false;
        }
        String[] patterns = packageFilters.split(",");
        for (String str : patterns) {
            if (str.contains("*")) {
                Pattern pattern = Pattern.compile(str);
                if (pattern.matcher(controllerName).matches()) {
                    return true;
                }
            } else if (controllerName.startsWith(str)) {
                return true;
            }
        }
        return false;
    }


    /**
     * An interpreter for strings with named placeholders.
     *
     * @param str    string to format
     * @param values to replace
     * @return formatted string
     */
    public static String formatAndRemove(String str, Map<String, String> values) {
        if (hasSystemProperties(str)) {
            str = DocUtil.delPropertiesUrl(str, new HashSet<>());
        }
        if (str.contains(":")) {
            String[] strArr = str.split("/");
            for (int i = 0; i < strArr.length; i++) {
                String pathParam = strArr[i];
                if (pathParam.contains(":")) {
                    int length = pathParam.length();
                    String reg = pathParam.substring(pathParam.indexOf(":") + 1, length - 1);
                    Generex generex = new Generex(reg);
                    // Generate random String
                    String randomStr = generex.random();
                    String key = pathParam.substring(1, pathParam.indexOf(":"));
                    values.computeIfPresent(key, (k, v) -> v = randomStr);
                    strArr[i] = pathParam.substring(0, pathParam.indexOf(":")) + "}";
                }
            }
            str = StringUtils.join("/", Arrays.asList(strArr));
        }
        StringBuilder builder = new StringBuilder(str);
        Set<Map.Entry<String, String>> entries = values.entrySet();
        Iterator<Map.Entry<String, String>> iteratorMap = entries.iterator();
        while (iteratorMap.hasNext()) {
            Map.Entry<String, String> next = iteratorMap.next();
            int start;
            String pattern = "{" + next.getKey() + "}";
            String value = next.getValue();
            // values.remove(next.getKey());
            // Replace every occurence of {key} with value
            while ((start = builder.indexOf(pattern)) != -1) {
                builder.replace(start, start + pattern.length(), value);
            }
            iteratorMap.remove();
            values.remove(next.getKey());

        }
        return builder.toString();
    }

    /**
     * // /detail/{id:[a-zA-Z0-9]{3}}/{name:[a-zA-Z0-9]{3}}
     * remove pattern
     *
     * @param str path
     * @return String
     */
    public static String formatPathUrl(String str) {
        if (hasSystemProperties(str)) {
            str = DocUtil.delPropertiesUrl(str, new HashSet<>());
        }
        if (!str.contains(":")) {
            return str;
        }
        String[] strArr = str.split("/");
        for (int i = 0; i < strArr.length; i++) {
            String pathParam = strArr[i];
            if (pathParam.startsWith("http:") || pathParam.startsWith("https:")) {
                continue;
            }
            if (pathParam.startsWith("{") && pathParam.contains(":")) {
                strArr[i] = pathParam.substring(0, pathParam.indexOf(":")) + "}";
            }
        }
        str = StringUtils.join("/", Arrays.asList(strArr));
        return str;
    }

    /**
     * handle spring mvc method
     *
     * @param method method name
     * @return String
     */
    public static String handleHttpMethod(String method) {
        switch (method) {
            case "RequestMethod.GET": //for spring
            case "MethodType.GET": //for solon
                return "GET";
            case "RequestMethod.POST":
            case "MethodType.POST":
                return "POST";
            case "RequestMethod.PUT":
            case "MethodType.PUT":
                return "PUT";
            case "RequestMethod.DELETE":
            case "MethodType.DELETE":
                return "DELETE";
            case "RequestMethod.PATCH":
            case "MethodType.PATCH":
                return "PATCH";
            default:
                return "GET";
        }
    }

    /**
     * handle spring mvc mapping value
     *
     * @param annotation JavaAnnotation
     * @return String
     */
    public static String handleMappingValue(JavaAnnotation annotation) {
        String url = getRequestMappingUrl(annotation);
        if (StringUtil.isEmpty(url)) {
            return "/";
        } else {
            return StringUtil.trimBlank(url);
        }
    }


    /**
     * Split url
     *
     * @param url URL to be divided
     * @return list of url
     */
    public static List<String> split(String url) {
        char[] chars = url.toCharArray();
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        Stack<Character> stack = new Stack<>();
        for (char s : chars) {
            if ('{' == s) {
                stack.push(s);
            }
            if ('}' == s) {
                if (stack.isEmpty()) {
                    throw new RuntimeException("Invalid mapping pattern detected: " + url);
                } else {
                    stack.pop();
                }
            }
            if (',' == s && stack.isEmpty()) {
                result.add(sb.toString());
                sb.delete(0, sb.length());
                continue;
            }
            sb.append(s);
        }
        result.add(sb.toString());
        return result;
    }

    /**
     * obtain params comments
     *
     * @param javaMethod JavaMethod
     * @param tagName    java comments tag
     * @param className  class name
     * @return Map
     */
    public static Map<String, String> getCommentsByTag(final JavaMethod javaMethod, final String tagName, final String className) {
        List<DocletTag> paramTags = javaMethod.getTagsByName(tagName);
        Map<String, String> paramTagMap = new HashMap<>();
        for (DocletTag docletTag : paramTags) {
            String value = docletTag.getValue();
            if (StringUtil.isEmpty(value) && StringUtil.isNotEmpty(className)) {
                throw new RuntimeException("ERROR: #" + javaMethod.getName()
                        + "() - bad @" + tagName + " javadoc from " + javaMethod.getDeclaringClass()
                        .getCanonicalName() + ", This is an invalid comment.");
            }
            if (DocTags.PARAM.equals(tagName)) {
                String pName = value;
                String pValue = DocGlobalConstants.NO_COMMENTS_FOUND;
                int idx = value.indexOf(" ");
                //existed \n
                if (idx > -1) {
                    pName = value.substring(0, idx);
                    pValue = value.substring(idx + 1);
                }
                if ("|".equals(StringUtil.trim(pValue)) && StringUtil.isNotEmpty(className)) {
                    throw new RuntimeException("ERROR: An invalid comment was written [@" + tagName + " |]," +
                            "Please @see " + javaMethod.getDeclaringClass().getCanonicalName() + "." + javaMethod.getName() + "()");
                }
                paramTagMap.put(pName, pValue);
            } else {
                paramTagMap.put(value, DocGlobalConstants.EMPTY);
            }
        }
        return paramTagMap;
    }

    /**
     * obtain java doc tags comments,like apiNote
     *
     * @param javaMethod JavaMethod
     * @param tagName    java comments tag
     * @param className  class name
     * @return Map
     */
    public static String getNormalTagComments(final JavaMethod javaMethod, final String tagName, final String className) {
        Map<String, String> map = getCommentsByTag(javaMethod, tagName, className);
        return getFirstKeyAndValue(map);
    }

    /**
     * Get field tags
     *
     * @param field        JavaField
     * @param docJavaField DocJavaField
     * @return map
     */
    public static Map<String, String> getFieldTagsValue(final JavaField field, DocJavaField docJavaField) {
        List<DocletTag> paramTags = field.getTags();
        if (CollectionUtil.isEmpty(paramTags) && Objects.nonNull(docJavaField)) {
            paramTags = docJavaField.getDocletTags();
        }
        return paramTags.stream().collect(Collectors.toMap(DocletTag::getName, DocletTag::getValue,
                (key1, key2) -> key1 + "," + key2));
    }

    /**
     * Get the first element of a map.
     *
     * @param map map
     * @return String
     */
    public static String getFirstKeyAndValue(Map<String, String> map) {
        String value = null;
        if (map != null && map.size() > 0) {
            Map.Entry<String, String> entry = map.entrySet().iterator().next();
            if (entry != null) {
                if (DocGlobalConstants.NO_COMMENTS_FOUND.equals(entry.getValue())) {
                    value = entry.getKey();
                } else {
                    value = entry.getKey() + entry.getValue();
                }
//                value = replaceNewLineToHtmlBr(value);
            }
        }
        return value;
    }

    /**
     * Use md5 generate id number
     *
     * @param value value
     * @return String
     */
    public static String generateId(String value) {
        if (StringUtil.isEmpty(value)) {
            return null;
        }
        String valueId = DigestUtils.md5Hex(value);
        int length = valueId.length();
        if (valueId.length() < 32) {
            return valueId;
        } else {
            return valueId.substring(length - 32, length);
        }
    }

    public static String replaceNewLineToHtmlBr(String content) {
        if (StringUtil.isNotEmpty(content)) {
            return content.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
        }
        return StringUtils.EMPTY;
    }

    public static String handleJsonStr(String content) {
        StringBuilder builder = new StringBuilder();
        builder.append("\"").append(content).append("\"");
        return builder.toString();
    }

    public static Map<String, String> formDataToMap(List<FormData> formDataList) {
        Map<String, String> formDataMap = new IdentityHashMap<>();
        for (FormData formData : formDataList) {
            if ("file".equals(formData.getType())) {
                continue;
            }
            if (formData.getKey().contains("[]")) {
                String key = formData.getKey().substring(0, formData.getKey().indexOf("["));
                formDataMap.put(key, formData.getValue() + "&" + key + "=" + formData.getValue());
                continue;
            }
            formDataMap.put(formData.getKey(), formData.getValue());
        }
        return formDataMap;
    }


    public static boolean javaPrimaryType(String type) {
        switch (type) {
            case "Integer":
            case "int":
            case "Long":
            case "long":
            case "Double":
            case "double":
            case "Float":
            case "Number":
            case "float":
            case "Boolean":
            case "boolean":
            case "Short":
            case "short":
            case "BigDecimal":
            case "BigInteger":
            case "Byte":
            case "Character":
            case "character":
                return true;
            default:
                return false;
        }
    }

    public static String javaTypeToOpenApiTypeConvert(String javaTypeName) {
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
            case "date":
            case "java.util.uuid":
            case "uuid":
            case "enum":
            case "java.util.date":
            case "localdatetime":
            case "java.time.localdatetime":
            case "java.time.year":
            case "java.time.localtime":
            case "java.time.yearmonth":
            case "java.time.monthday":
            case "java.time.localdate":
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
            case "java.util.byte":
            case "byte":
            case "java.lang.integer":
            case "integer":
            case "int":
            case "short":
            case "java.lang.short":
            case "int32":
                return "integer";
            case "double":
            case "java.lang.long":
            case "long":
            case "java.lang.float":
            case "float":
            case "bigdecimal":
            case "int64":
            case "biginteger":
                return "number";
            case "java.lang.boolean":
            case "boolean":
                return "boolean";
            case "multipartfile":
            case "file":
                return "file";
            default:
                return "object";
        }
    }

    /**
     * Gets escape and clean comment.
     *
     * @param comment the comment
     * @return the escape and clean comment
     */
    public static String getEscapeAndCleanComment(String comment) {
        if (StringUtil.isEmpty(comment)) {
            return "";
        }
        return comment.replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
    }

    /**
     * Get the url from 'value' or 'path' attribute
     *
     * @param annotation RequestMapping GetMapping PostMapping etc.
     * @return the url
     */
    public static String getRequestMappingUrl(JavaAnnotation annotation) {
        return getPathUrl(annotation, DocAnnotationConstants.VALUE_PROP, DocAnnotationConstants.NAME_PROP, DocAnnotationConstants.PATH_PROP);
    }

    /**
     * Get mapping url from Annotation
     *
     * @param annotation JavaAnnotation
     * @param props      annotation properties
     * @return the path
     */
    public static String getPathUrl(JavaAnnotation annotation, String... props) {
        for (String prop : props) {
            AnnotationValue annotationValue = annotation.getProperty(prop);
            if (Objects.nonNull(annotationValue)) {
                Object url = resolveAnnotationValue(annotationValue);
                if (Objects.nonNull(url)) {
                    return url.toString();
                }
            }
        }
        return StringUtil.EMPTY;
    }

    /**
     * resolve the string of {@link Add} which has {@link FieldRef}(to be exact is {@link FieldRef}) children,
     * the value of {@link FieldRef} will be resolved with the real value of it if it is the static final member of any other class
     *
     * @param annotationValue annotationValue
     * @return annotation value
     */
    public static String resolveAnnotationValue(AnnotationValue annotationValue) {
        if (annotationValue instanceof Add) {
            Add add = (Add) annotationValue;
            String leftValue = resolveAnnotationValue(add.getLeft());
            String rightValue = resolveAnnotationValue(add.getRight());
            return StringUtil.removeQuotes(leftValue + rightValue);
        } else {
            if (annotationValue instanceof FieldRef) {
                FieldRef fieldRef = (FieldRef) annotationValue;
                JavaField javaField = fieldRef.getField();
                if (javaField != null) {
                    return StringUtil.removeQuotes(javaField.getInitializationExpression());
                }
            }
            return Optional.ofNullable(annotationValue).map(Expression::getParameterValue).map(Object::toString).orElse(StringUtil.EMPTY);
        }
    }


    /**
     * handle spring mvc RequestHeader value
     *
     * @param annotation JavaAnnotation
     * @return String
     */
    public static String handleRequestHeaderValue(JavaAnnotation annotation) {
        String header = getRequestHeaderValue(annotation);
        if (StringUtil.isEmpty(header)) {
            return header;
        }
        return StringUtil.removeDoubleQuotes(StringUtil.trimBlank(header));

    }

    /**
     * Obtain constant from @RequestHeader annotation
     *
     * @param annotation RequestMapping GetMapping PostMapping etc.
     * @return The constant value
     */
    public static String getRequestHeaderValue(JavaAnnotation annotation) {
        AnnotationValue annotationValue = annotation.getProperty(DocAnnotationConstants.VALUE_PROP);
        return resolveAnnotationValue(annotationValue);
    }

    public static List<ApiErrorCode> errorCodeDictToList(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
        if (CollectionUtil.isNotEmpty(config.getErrorCodes())) {
            return config.getErrorCodes();
        }
        List<ApiErrorCodeDictionary> errorCodeDictionaries = config.getErrorCodeDictionaries();
        if (CollectionUtil.isEmpty(errorCodeDictionaries)) {
            return new ArrayList<>(0);
        } else {
            ClassLoader classLoader = config.getClassLoader();
            Set<ApiErrorCode> errorCodeList = new LinkedHashSet<>();
            try {
                for (ApiErrorCodeDictionary dictionary : errorCodeDictionaries) {
                    Class<?> clzz = dictionary.getEnumClass();
                    if (Objects.isNull(clzz)) {
                        if (StringUtil.isEmpty(dictionary.getEnumClassName())) {
                            throw new RuntimeException("Enum class name can't be null.");
                        }
                        clzz = classLoader.loadClass(dictionary.getEnumClassName());
                    }

                    Class<?> valuesResolverClass = null;
                    if (StringUtil.isNotEmpty(dictionary.getValuesResolverClass())) {
                        valuesResolverClass = classLoader.loadClass(dictionary.getValuesResolverClass());
                    }
                    if (null != valuesResolverClass && DictionaryValuesResolver.class.isAssignableFrom(valuesResolverClass)) {
                        DictionaryValuesResolver resolver = (DictionaryValuesResolver) DocClassUtil.newInstance(valuesResolverClass);
                        // add two method results
                        errorCodeList.addAll(resolver.resolve());
                        errorCodeList.addAll(resolver.resolve(clzz));
                    } else if (clzz.isInterface()) {
                        Set<Class<? extends Enum>> enumImplementSet = dictionary.getEnumImplementSet();
                        if (CollectionUtil.isEmpty(enumImplementSet)) {
                            continue;
                        }

                        for (Class<? extends Enum> enumClass : enumImplementSet) {
                            JavaClass interfaceClass = javaProjectBuilder.getClassByName(enumClass.getCanonicalName());
                            if (Objects.nonNull(interfaceClass.getTagByName(DocTags.IGNORE))) {
                                continue;
                            }
                            List<ApiErrorCode> enumDictionaryList = EnumUtil.getEnumInformation(enumClass, dictionary.getCodeField(),
                                    dictionary.getDescField());
                            errorCodeList.addAll(enumDictionaryList);
                        }

                    } else {
                        JavaClass javaClass = javaProjectBuilder.getClassByName(clzz.getCanonicalName());
                        if (Objects.nonNull(javaClass.getTagByName(DocTags.IGNORE))) {
                            continue;
                        }
                        List<ApiErrorCode> enumDictionaryList = EnumUtil.getEnumInformation(clzz, dictionary.getCodeField(),
                                dictionary.getDescField());
                        errorCodeList.addAll(enumDictionaryList);
                    }

                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return new ArrayList<>(errorCodeList);
        }
    }

    /**
     * Build dictionary
     *
     * @param config             api config
     * @param javaProjectBuilder JavaProjectBuilder
     * @return list of ApiDocDict
     */
    public static List<ApiDocDict> buildDictionary(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
        List<ApiDataDictionary> apiDataDictionaryList = config.getDataDictionaries();
        List<ApiDocDict> dictDynamic = config.getDataDictionariesDynamic();
        if (CollectionUtil.isEmpty(apiDataDictionaryList) && CollectionUtil.isEmpty(dictDynamic)) {
            return new ArrayList<>(0);
        }
        List<ApiDocDict> apiDocDictList = new ArrayList<>();
        try {

            ClassLoader classLoader = config.getClassLoader();
            int order = 0;
            for (ApiDataDictionary apiDataDictionary : apiDataDictionaryList) {
                order++;
                Class<?> clazz = apiDataDictionary.getEnumClass();
                if (Objects.isNull(clazz)) {
                    if (StringUtil.isEmpty(apiDataDictionary.getEnumClassName())) {
                        throw new RuntimeException("Enum class name can't be null.");
                    }
                    clazz = classLoader.loadClass(apiDataDictionary.getEnumClassName());
                }

                if (clazz.isInterface()) {
                    Set<Class<? extends Enum>> enumImplementSet = apiDataDictionary.getEnumImplementSet();
                    if (CollectionUtil.isEmpty(enumImplementSet)) {
                        continue;
                    }

                    for (Class<? extends Enum> enumClass : enumImplementSet) {
                        JavaClass javaClass = javaProjectBuilder.getClassByName(enumClass.getCanonicalName());
                        if (Objects.nonNull(javaClass.getTagByName(DocTags.IGNORE))) {
                            continue;
                        }
                        DocletTag apiNoteTag = javaClass.getTagByName(DocTags.API_NOTE);
                        ApiDocDict apiDocDict = new ApiDocDict();
                        apiDocDict.setOrder(order++);
                        apiDocDict.setTitle(javaClass.getComment());
                        apiDocDict.setDescription(
                                DocUtil.getEscapeAndCleanComment(Optional.ofNullable(apiNoteTag).map(DocletTag::getValue).orElse(StringUtil.EMPTY)));
                        List<DataDict> enumDictionaryList = EnumUtil.getEnumInformation(enumClass, apiDataDictionary.getCodeField(),
                                apiDataDictionary.getDescField());
                        apiDocDict.setDataDictList(enumDictionaryList);
                        apiDocDictList.add(apiDocDict);
                    }

                } else {
                    ApiDocDict apiDocDict = new ApiDocDict();
                    apiDocDict.setOrder(order);
                    apiDocDict.setTitle(apiDataDictionary.getTitle());
                    JavaClass javaClass = javaProjectBuilder.getClassByName(clazz.getCanonicalName());
                    if (Objects.nonNull(javaClass.getTagByName(DocTags.IGNORE))) {
                        continue;
                    }
                    DocletTag apiNoteTag = javaClass.getTagByName(DocTags.API_NOTE);
                    apiDocDict.setDescription(
                            DocUtil.getEscapeAndCleanComment(Optional.ofNullable(apiNoteTag).map(DocletTag::getValue).orElse(StringUtil.EMPTY)));
                    if (apiDataDictionary.getTitle() == null) {
                        apiDocDict.setTitle(javaClass.getComment());
                    }
                    List<DataDict> enumDictionaryList = EnumUtil.getEnumInformation(clazz, apiDataDictionary.getCodeField(),
                            apiDataDictionary.getDescField());
                    if (!clazz.isEnum()) {
                        throw new RuntimeException(clazz.getCanonicalName() + " is not an enum class.");
                    }
                    apiDocDict.setDataDictList(enumDictionaryList);
                    apiDocDictList.add(apiDocDict);
                }

            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        apiDocDictList.addAll(dictDynamic);
        return apiDocDictList;
    }

    /**
     * Format  field Type
     *
     * @param genericMap   genericMap
     * @param globGicName  globGicName array
     * @param fieldGicName fieldGicName
     * @return string
     */
    public static String formatFieldTypeGicName(Map<String, String> genericMap, String[] globGicName, String fieldGicName) {
        String gicName = "";
        String[] gNameArr = DocClassUtil.getSimpleGicName(fieldGicName);
        for (String g : gNameArr) {
            if (g.length() == 1) {
                if (Objects.nonNull(genericMap.get(g))) {
                    gicName = genericMap.get(g);
                }
                if (StringUtil.isNotEmpty(gicName)) {
                    fieldGicName = fieldGicName.replace(g, gicName);
                }
            }
        }
        return fieldGicName;
    }

    public static String handleConstants(Map<String, String> constantsMap, String value) {
        Object constantsValue = constantsMap.get(value);
        if (Objects.nonNull(constantsValue)) {
            return constantsValue.toString();
        }
        return value;
    }

    public static String handleContentType(String mediaType, JavaAnnotation annotation, String annotationName) {
        if (JakartaJaxrsAnnotations.JAX_PRODUCES_FULLY.equals(annotationName)
                || JAXRSAnnotations.JAX_PRODUCES_FULLY.equals(annotationName)) {
            String annotationValue = StringUtil.removeQuotes(DocUtil.getRequestHeaderValue(annotation));
            if ("MediaType.APPLICATION_JSON".equals(annotationValue) || "application/json".equals(annotationValue)
                    || "MediaType.TEXT_PLAIN".equals(annotationValue) || "text/plain".equals(annotationValue)) {
                mediaType = JSON_CONTENT_TYPE;
            }
        }
        return mediaType;
    }

    public static boolean filterPath(RequestMapping requestMapping, ApiReqParam apiReqHeader) {
        if (StringUtil.isEmpty(apiReqHeader.getPathPatterns())
                && StringUtil.isEmpty(apiReqHeader.getExcludePathPatterns())) {
            return true;
        }
        return DocPathUtil.matches(requestMapping.getShortUrl(), apiReqHeader.getPathPatterns()
                , apiReqHeader.getExcludePathPatterns());

    }

    public static String paramCommentResolve(String comment) {
        if (StringUtil.isEmpty(comment)) {
            comment = NO_COMMENTS_FOUND;
        } else {
            if (comment.contains("|")) {
                comment = comment.substring(0, comment.indexOf("|"));
            }
        }
        return comment;
    }

    /**
     * del ${server.port:/error}
     *
     * @param value               url
     * @param visitedPlaceholders cycle
     * @return url deleted
     */
    public static String delPropertiesUrl(String value, Set<String> visitedPlaceholders) {
        int startIndex = value.indexOf(PLACEHOLDER_PREFIX);
        if (startIndex == -1) {
            return value;
        }
        StringBuilder result = new StringBuilder(value);
        while (startIndex != -1) {
            int endIndex = findPlaceholderEndIndex(result, startIndex);
            if (endIndex != -1) {
                String placeholder = result.substring(startIndex + PLACEHOLDER_PREFIX.length(), endIndex);
                String originalPlaceholder = placeholder;
                if (visitedPlaceholders == null) {
                    visitedPlaceholders = new HashSet<>(4);
                }
                if (!visitedPlaceholders.add(originalPlaceholder)) {
                    throw new IllegalArgumentException(
                            "Circular placeholder reference '" + originalPlaceholder + "' in property definitions");
                }
                // Recursive invocation, parsing placeholders contained in the placeholder key.
                placeholder = delPropertiesUrl(placeholder, visitedPlaceholders);
                String propVal = SystemPlaceholders.replaceSystemProperties(placeholder);
                if (propVal == null) {
                    int separatorIndex = placeholder.indexOf(":");
                    if (separatorIndex != -1) {
                        String actualPlaceholder = placeholder.substring(0, separatorIndex);
                        String defaultValue = placeholder.substring(separatorIndex + ":".length());
                        propVal = SystemPlaceholders.replaceSystemProperties(actualPlaceholder);
                        if (propVal == null) {
                            propVal = defaultValue;
                        }
                    }
                }
                if (propVal != null) {
                    propVal = delPropertiesUrl(propVal, visitedPlaceholders);
                    result.replace(startIndex - 1, endIndex + PLACEHOLDER_PREFIX.length() - 1, propVal);
                    startIndex = result.indexOf(PLACEHOLDER_PREFIX, startIndex + propVal.length());
                } else {
                    // Proceed with unprocessed value.
                    startIndex = result.indexOf(PLACEHOLDER_PREFIX, endIndex + PLACEHOLDER_PREFIX.length());
                }

                visitedPlaceholders.remove(originalPlaceholder);
            } else {
                startIndex = -1;
            }
        }
        return result.toString();
    }

    private static int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
        int index = startIndex + PLACEHOLDER_PREFIX.length();
        int withinNestedPlaceholder = 0;
        while (index < buf.length()) {
            if (substringMatch(buf, index, PLACEHOLDER_SUFFIX)) {
                if (withinNestedPlaceholder > 0) {
                    withinNestedPlaceholder--;
                    index = index + "}".length();
                } else {
                    return index;
                }
            } else if (substringMatch(buf, index, SIMPLE_PREFIX)) {
                withinNestedPlaceholder++;
                index = index + SIMPLE_PREFIX.length();
            } else {
                index++;
            }
        }
        return -1;
    }

    public static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
        if (index + substring.length() > str.length()) {
            return false;
        }
        for (int i = 0; i < substring.length(); i++) {
            if (str.charAt(index + i) != substring.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}
