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

import com.power.common.model.EnumDictionary;

import java.util.Objects;

/**
 * Description:
 * restful api error code
 *
 * @author yu 2018/06/25.
 */
public class ApiErrorCode extends EnumDictionary {

    @Override
    public int hashCode() {
        return Objects.hash(getValue(), getType(), getDesc(), getOrdinal(), getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ApiErrorCode)) {
            return false;
        }
        ApiErrorCode other = (ApiErrorCode) obj;
        return Objects.equals(getValue(), other.getValue()) &&
                Objects.equals(getType(), other.getType()) &&
                Objects.equals(getDesc(), other.getDesc()) &&
                Objects.equals(getOrdinal(), other.getOrdinal()) &&
                Objects.equals(getName(), other.getName());
    }
}
