package com.power.doc.util;

import java.util.ArrayList;
import java.util.List;

import com.power.doc.utils.DocUrlUtil;

import org.junit.jupiter.api.Test;

public class DocUrlUtilTest {

    @Test
    public void getMvcUrls() {
        String baseUrl = "/[/testMultiPathOne/{path}/test, /testMultiPathTwo/{path}/test]";
        List<String> urls = new ArrayList<>();
        urls.add("[/{path2}/abc2");
        urls.add(" /{path2}/abc3]");
        String baseServer = "http://{{host}}:{{port}}";

        System.out.println(DocUrlUtil.getMvcUrls(baseServer, baseUrl, urls));

    }
}