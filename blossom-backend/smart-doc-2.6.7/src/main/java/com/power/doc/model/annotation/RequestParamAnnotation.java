package com.power.doc.model.annotation;

/**
 * @author yu3.sun on 2022/10/1
 */
public class RequestParamAnnotation {

    private String annotationName;

    private String annotationFullyName;

    private String defaultValueProp;

    private String requiredProp;

    public static RequestParamAnnotation builder() {
        return new RequestParamAnnotation();
    }

    public String getAnnotationName() {
        return annotationName;
    }

    public RequestParamAnnotation setAnnotationName(String annotationName) {
        this.annotationName = annotationName;
        return this;
    }

    public String getAnnotationFullyName() {
        return annotationFullyName;
    }

    public RequestParamAnnotation setAnnotationFullyName(String annotationFullyName) {
        this.annotationFullyName = annotationFullyName;
        return this;
    }

    public String getDefaultValueProp() {
        return defaultValueProp;
    }

    public RequestParamAnnotation setDefaultValueProp(String defaultValueProp) {
        this.defaultValueProp = defaultValueProp;
        return this;
    }

    public String getRequiredProp() {
        return requiredProp;
    }

    public RequestParamAnnotation setRequiredProp(String requiredProp) {
        this.requiredProp = requiredProp;
        return this;
    }
}
