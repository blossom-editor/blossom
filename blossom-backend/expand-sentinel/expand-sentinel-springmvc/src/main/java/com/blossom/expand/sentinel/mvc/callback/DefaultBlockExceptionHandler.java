/*
 * Copyright 1999-2019 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blossom.expand.sentinel.mvc.callback;

import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.blossom.common.base.pojo.R;
import com.blossom.common.base.pojo.RCode;
import com.blossom.common.base.util.json.JsonUtil;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Default handler for the blocked request.
 *
 * @author kaizi2009
 */
public class DefaultBlockExceptionHandler implements BlockExceptionHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
        // Return 429 (Too Many Requests) by default.
        response.setStatus(200);

        StringBuffer url = request.getRequestURL();
//
//        if ("GET".equals(request.getMethod()) && StringUtil.isNotBlank(request.getQueryString())) {
//            url.append("?").append(request.getQueryString());
//        }

        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        Map<String, Object> flowRule = new HashMap<>();
        flowRule.put("flowRule", JsonUtil.toJsonNode(e.getRule()));

        R<Map<String, Object>> r = R.fault(RCode.TOO_MANY_REQUESTS.getCode(), RCode.TOO_MANY_REQUESTS.getMsg(), e.getMessage());
        r.setData(flowRule);
        r.setEx(StrUtil.isBlank(e.getMessage()) ? e.getClass().getName() : e.getMessage());

        PrintWriter out = response.getWriter();
        out.print(JsonUtil.toJson(r));
        out.flush();
        out.close();
    }

}
