package com.power.doc.model.annotation;

/**
 * @author yu3.sun on 2022/10/1
 */
public class PathVariableAnnotation {

    private String annotationName;

    private String annotationFullyName;

    private String defaultValueProp;

    private String requiredProp;


    public static PathVariableAnnotation builder() {
        return new PathVariableAnnotation();
    }


    public String getAnnotationName() {
        return annotationName;
    }

    public PathVariableAnnotation setAnnotationName(String annotationName) {
        this.annotationName = annotationName;
        return this;
    }

    public String getAnnotationFullyName() {
        return annotationFullyName;
    }

    public PathVariableAnnotation setAnnotationFullyName(String annotationFullyName) {
        this.annotationFullyName = annotationFullyName;
        return this;
    }

    public String getDefaultValueProp() {
        return defaultValueProp;
    }

    public PathVariableAnnotation setDefaultValueProp(String defaultValueProp) {
        this.defaultValueProp = defaultValueProp;
        return this;
    }

    public String getRequiredProp() {
        return requiredProp;
    }

    public PathVariableAnnotation setRequiredProp(String requiredProp) {
        this.requiredProp = requiredProp;
        return this;
    }
}
