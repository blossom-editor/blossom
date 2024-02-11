package com.power.doc.util;

import java.util.HashMap;
import java.util.Map;

import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.DocLanguage;
import com.power.doc.enums.IEnum;
import com.power.doc.enums.OrderEnum;
import com.power.doc.utils.DocUtil;

import org.junit.jupiter.api.Test;

/**
 * @author yu 2018/12/10.
 */
public class DocUtilTest {

    @Test
    public void test() {
        System.setProperty(DocGlobalConstants.DOC_LANGUAGE, DocLanguage.CHINESE.getCode());
        String str = DocUtil.getValByTypeAndFieldName("string", "hostName");
        System.out.println(str);
    }

    /* @Test*/
    public void testFormatAndRemove() {
        System.setProperty(DocGlobalConstants.DOC_LANGUAGE, DocLanguage.CHINESE.getCode());
        Map<String, String> params = new HashMap<>();
        params.put("name", "dd");
        params.put("age", "0");

        String url2 = "/user/getUserById/{name}/{age}";
        String me = DocUtil.formatAndRemove(url2, params);

        System.out.println(params.size());
        System.out.println(me);
    }

    @Test
    public void testGetInterfacesEnum() throws ClassNotFoundException {
        System.out.println(IEnum.class.isAssignableFrom(OrderEnum.class));
    }

    @Test
    public void testIsMatch() {
        System.setProperty(DocGlobalConstants.DOC_LANGUAGE, DocLanguage.CHINESE.getCode());
        String pattern = "com.aaa.*.controller";
        String controllerName = "com.aaa.cc.controlle";

        System.out.println(DocUtil.isMatch(pattern, controllerName));
    }

    @Test
    public void  testFormatPathUrl() {
        System.setProperty(DocGlobalConstants.DOC_LANGUAGE, DocLanguage.CHINESE.getCode());
        String url = "http://localhost:8080/detail/{id:[a-zA-Z0-9]{3}}/{name:[a-zA-Z0-9]{3}}";
        System.out.println(DocUtil.formatPathUrl(url));
    }
}
