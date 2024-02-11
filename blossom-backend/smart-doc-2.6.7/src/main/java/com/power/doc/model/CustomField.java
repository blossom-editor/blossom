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
package com.power.doc.model;

import java.util.Map;
import java.util.Objects;

/**
 * @author xingzi
 **/
public class CustomField {

    /**
     * field name
     */
    private String name;
    /**
     * field description
     */
    private String desc;
    /**
     * owner class
     */
    private String ownerClassName;
    /**
     * default value
     */
    private Object value;
    /**
     * required
     */
    private boolean require;

    private boolean ignore;

    private String replaceName;

    public static CustomField builder() {
        return new CustomField();
    }

    public static CustomField nameEquals(Key key, Map<Key, CustomField> customFieldMap) {
        for (Map.Entry<Key, CustomField> c : customFieldMap.entrySet()) {
            if (key.equals(c.getKey())) {
                return c.getValue();
            }
        }
        return null;
    }

    public String getReplaceName() {
        return replaceName;
    }

    public CustomField setReplaceName(String replaceName) {
        this.replaceName = replaceName;
        return this;
    }

    public boolean isRequire() {
        return require;
    }

    public CustomField setRequire(boolean require) {
        this.require = require;
        return this;
    }

    public String getName() {
        return name;
    }

    public CustomField setName(String name) {
        this.name = name;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public CustomField setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public String getOwnerClassName() {
        return ownerClassName;
    }

    public CustomField setOwnerClassName(String ownerClassName) {
        this.ownerClassName = ownerClassName;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public CustomField setValue(Object value) {
        this.value = value;
        return this;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public CustomField setIgnore(boolean ignore) {
        this.ignore = ignore;
        return this;
    }

    public static final class Key {

        /**
         * owner class
         */
        private String ownerClassName;

        /**
         * field name
         */
        private String name;

        private Key(String ownerClassName, String name) {
            this.ownerClassName = ownerClassName;
            this.name = name;
        }

        public static Key create(String className, String fieldName) {
            return new Key(className, fieldName);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return Objects.equals(ownerClassName, key.ownerClassName) && Objects.equals(name, key.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ownerClassName, name);
        }
    }
}
