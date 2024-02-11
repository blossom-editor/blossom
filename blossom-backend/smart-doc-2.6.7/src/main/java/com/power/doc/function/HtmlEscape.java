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
package com.power.doc.function;

import com.power.doc.utils.DocUtil;

import org.beetl.core.Context;
import org.beetl.core.Function;

/**
 * beetl template function
 * @author yu 2021/6/26.
 */
public class HtmlEscape implements Function {

    @Override
    public String call(Object[] paras, Context ctx) {
        String str = String.valueOf(paras[0]).replaceAll("&", "&amp;");
        str = str.replaceAll("\"", "&quot;");
        str = str.replaceAll("<p>", "").replaceAll("</p>", " ");
        return DocUtil.replaceNewLineToHtmlBr(DocUtil.getEscapeAndCleanComment(str));
    }
}
