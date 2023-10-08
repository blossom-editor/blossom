package com.power.doc.model.annotation;

/**
 * @author yu3.sun on 2022/10/1
 */
public class HeaderAnnotation {

    private String annotationName;

    private String valueProp;

    private String defaultValueProp;

    private String requiredProp;


    public static HeaderAnnotation builder() {
        return new HeaderAnnotation();
    }

    public String getAnnotationName() {
        return annotationName;
    }

    public HeaderAnnotation setAnnotationName(String annotationName) {
        this.annotationName = annotationName;
        return this;
    }

    public String getValueProp() {
        return valueProp;
    }

    public HeaderAnnotation setValueProp(String valueProp) {
        this.valueProp = valueProp;
        return this;
    }

    public String getDefaultValueProp() {
        return defaultValueProp;
    }

    public HeaderAnnotation setDefaultValueProp(String defaultValueProp) {
        this.defaultValueProp = defaultValueProp;
        return this;
    }

    public String getRequiredProp() {
        return requiredProp;
    }

    public HeaderAnnotation setRequiredProp(String requiredProp) {
        this.requiredProp = requiredProp;
        return this;
    }
}
