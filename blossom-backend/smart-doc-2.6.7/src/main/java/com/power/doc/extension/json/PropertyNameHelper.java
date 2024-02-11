package com.power.doc.extension.json;

import java.util.List;

import com.power.common.util.StringUtil;
import com.power.doc.constants.DocAnnotationConstants;
import com.thoughtworks.qdox.model.JavaAnnotation;

/**
 * @author xingzi
 * Date 2022/9/17 13:32
 */
public class PropertyNameHelper {

    public static final String JACKSON_LOWER_CAMEL_CASE = "lowercamel";
    public static final String JACKSON_UPPER_CAMEL_CASE = "uppercamel";
    public static final String JACKSON_SNAKE_CASE = "snake";
    public static final String JACKSON_UPPER_SNAKE_CASE = "uppersnake";
    public static final String JACKSON_LOWER_CASE = "lower";
    public static final String JACKSON_KEBAB_CASE = "kebab";
    public static final String JACKSON_LOWER_DOT_CASE = "lowerdot";


    private PropertyNameHelper() {
    }

    public static PropertyNamingStrategies.NamingBase translate(List<JavaAnnotation> javaAnnotations) {
        for (JavaAnnotation annotation : javaAnnotations) {
            String simpleAnnotationName = annotation.getType().getValue();
            //jackson
            if (DocAnnotationConstants.JSON_NAMING.equalsIgnoreCase(simpleAnnotationName)) {
                String value = annotation.getProperty("value").getParameterValue().toString().toLowerCase();
                return jackSonTranslate(value);
            }

        }
        return null;
    }

    private static PropertyNamingStrategies.NamingBase jackSonTranslate(String annotationValue) {
        if (StringUtil.isEmpty(annotationValue)) {
            return null;
        }
        if (annotationValue.contains(JACKSON_LOWER_CAMEL_CASE)) {
            return PropertyNamingStrategies.LOWER_CAMEL_CASE;
        }
        if (annotationValue.contains(JACKSON_UPPER_CAMEL_CASE)) {
            return PropertyNamingStrategies.UPPER_CAMEL_CASE;
        }
        if (annotationValue.contains(JACKSON_SNAKE_CASE)) {
            return PropertyNamingStrategies.SNAKE_CASE;
        }
        if (annotationValue.contains(JACKSON_UPPER_SNAKE_CASE)) {
            return PropertyNamingStrategies.UPPER_SNAKE_CASE;
        }
        if (annotationValue.contains(JACKSON_LOWER_CASE)) {
            return PropertyNamingStrategies.LOWER_CASE;
        }
        if (annotationValue.contains(JACKSON_KEBAB_CASE)) {
            return PropertyNamingStrategies.KEBAB_CASE;
        }
        if (annotationValue.contains(JACKSON_LOWER_DOT_CASE)) {
            return PropertyNamingStrategies.LOWER_DOT_CASE;
        }
        return null;
    }
}
