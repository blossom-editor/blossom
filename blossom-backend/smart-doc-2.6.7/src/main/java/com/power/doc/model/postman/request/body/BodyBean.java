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
package com.power.doc.model.postman.request.body;


import java.util.List;

import com.power.doc.model.FormData;

/**
 * @author xingzi
 */
public class BodyBean {

    private String mode;
    private String raw;
    private List<FormData> formdata;
    private BodyOptions options;

    public BodyBean(boolean isFormData) {
        if (!isFormData) {
            this.options = new BodyOptions();
        }
    }

    public List<FormData> getFormdata() {
        return formdata;
    }

    public void setFormdata(List<FormData> formdata) {
        this.formdata = formdata;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    private class BodyOptions {

        private Raw raw;

        public BodyOptions() {
            this.raw = new Raw();
        }

        private class Raw {

            private String language;

            Raw() {
                this.language = "json";
            }
        }
    }


}
