package com.power.doc.util;

import com.power.doc.utils.JsonUtil;

import org.junit.jupiter.api.Test;

/**
 * @author yu 2021/6/27.
 */
public class JsonUtilTest {

    @Test
    public void toPrettyFormat() {
        String json = "{\"MAX_SPEED\":210,\"gender\":0,\"simpleEnum\":\"RED\",\"username\":\"梓晨.田\",\"password\":\"slujk7\",\"nickName\":\"select * from table where field = 'value'\",\"mobile\":\"17658638153\"}";
        System.out.println(JsonUtil.toPrettyFormat(json));
    }
}
