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
package com.power.doc.utils;

import com.power.common.util.PathUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class DocPathUtil {

    /**
     * Get the java class name
     *
     * @param parentDir parent dir
     * @param className class name
     * @return java file name
     */
    public static String javaFilePath(String parentDir, String className) {
        if (StringUtils.isEmpty(parentDir)) {
            parentDir = "java.io.tmpdir";
        }
        if (!StringUtils.endWith(parentDir, File.separator)) {
            parentDir += File.separator;
        }
        className = className.replaceAll("\\.", "\\" + File.separator);
        return parentDir + className + ".java";
    }

    /**
     * to postman path
     *
     * @param path path
     * @return String
     */
    public static String toPostmanPath(String path) {
        if (StringUtils.isNotEmpty(path)) {
            path = path.replace("{", ":");
            path = path.replace("}", "");
            return path;
        }
        return null;
    }

    /**
     * Determine a match for the given lookup path.
     *
     * @param lookupPath      the request path
     * @param includePatterns the path patterns to map (empty for matching to all paths)
     * @param excludePatterns the path patterns to exclude (empty for no specific excludes)
     * @return {@code true} if matched the request path
     */
    public static boolean matches(String lookupPath, String includePatterns, String excludePatterns) {
        List<String> includePatternList = null;
        if (StringUtils.isNotEmpty(includePatterns)) {
            includePatternList = Arrays.asList(includePatterns.split(",", 0));
        }
        List<String> excludePatternList = null;
        if (StringUtils.isNotEmpty(excludePatterns)) {
            excludePatternList = Arrays.asList(excludePatterns.split(",", 0));
        }
        return PathUtil.matches(lookupPath, includePatternList, excludePatternList);
    }
}
