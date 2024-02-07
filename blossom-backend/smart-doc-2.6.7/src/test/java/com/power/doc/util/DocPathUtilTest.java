package com.power.doc.util;

import com.power.doc.utils.DocPathUtil;

import org.junit.jupiter.api.Test;

/**
 * @author yu 2021/6/27.
 */
public class DocPathUtilTest {

    @Test
    public void testMatches() {
        String pattern = "/app/page/**";
        String path = "/app/page/{pageIndex}/{pageSize}/{ag}";
        System.out.println(DocPathUtil.matches(path, null, pattern));
    }
}
