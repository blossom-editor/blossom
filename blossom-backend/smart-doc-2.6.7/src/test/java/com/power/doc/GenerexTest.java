package com.power.doc;

import com.mifmif.common.regex.Generex;

import org.junit.jupiter.api.Test;

/**
 * @author yu3.sun on 2022/10/15
 */
public class GenerexTest {

    @Test
    public void testGenerateValue() {
        Generex generex = new Generex("[a-zA-Z0-9]{3}");
        // Generate random String
        String randomStr = generex.random();
        System.out.println(randomStr);

    }
}
