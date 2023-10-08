package com.power.doc.model.annotation;

/**
 * @author yu3.sun on 2022/10/1
 */
public class RequestBodyAnnotation {

    private String annotationName;

    private String annotationFullyName;

    public static RequestBodyAnnotation builder() {
        return new RequestBodyAnnotation();
    }

    public String getAnnotationName() {
        return annotationName;
    }

    public RequestBodyAnnotation setAnnotationName(String annotationName) {
        this.annotationName = annotationName;
        return this;
    }

    public String getAnnotationFullyName() {
        return annotationFullyName;
    }

    public RequestBodyAnnotation setAnnotationFullyName(String annotationFullyName) {
        this.annotationFullyName = annotationFullyName;
        return this;
    }
}
