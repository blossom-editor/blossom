package com.power.doc.model.annotation;

/**
 * @author yu3.sun on 2022/10/1
 */
public class EntryAnnotation {

    private String annotationName;

    private String annotationFullyName;

    public static EntryAnnotation builder() {
        return new EntryAnnotation();
    }

    public String getAnnotationName() {
        return annotationName;
    }

    public EntryAnnotation setAnnotationName(String annotationName) {
        this.annotationName = annotationName;
        return this;
    }

    public String getAnnotationFullyName() {
        return annotationFullyName;
    }

    public EntryAnnotation setAnnotationFullyName(String annotationFullyName) {
        this.annotationFullyName = annotationFullyName;
        return this;
    }
}
