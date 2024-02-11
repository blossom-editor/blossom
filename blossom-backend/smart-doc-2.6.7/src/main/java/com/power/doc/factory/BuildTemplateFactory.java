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
package com.power.doc.factory;

import com.power.doc.constants.FrameworkEnum;
import com.power.doc.template.IDocBuildTemplate;

/**
 * @author yu 2021/6/27.
 */
public class BuildTemplateFactory {

    /**
     * Get Doc build template
     *
     * @param framework framework name
     * @param <T>       API doc type
     * @return Implements of IDocBuildTemplate
     */
    public static <T> IDocBuildTemplate getDocBuildTemplate(String framework) {
        String className = FrameworkEnum.getClassNameByFramework(framework);
        try {
            return (IDocBuildTemplate) Class.forName(className).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                "The class=>" + className + " is not found , smart-doc currently supported framework name can only be set in [dubbo, spring].");
        }
        return null;
    }
}
