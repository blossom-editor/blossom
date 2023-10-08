package com.power.doc.model.annotation;

import java.util.Arrays;
import java.util.List;

/**
 * @author yu3.sun on 2022/10/1
 */
public class MappingAnnotation {

    private String annotationName;

    private String annotationFullyName;

    private List<String> pathProps;

    private String producesProp;


    private String consumesProp;

    private String methodProp;

    private String methodType;

    private String paramsProp;

    private List<String> scope;


    public static MappingAnnotation builder() {
        return new MappingAnnotation();
    }

    public String getAnnotationName() {
        return annotationName;
    }

    public MappingAnnotation setAnnotationName(String annotationName) {
        this.annotationName = annotationName;
        return this;
    }

    public List<String> getPathProps() {
        return pathProps;
    }

    public MappingAnnotation setPathProps(String... pathProp) {
        this.pathProps = Arrays.asList(pathProp);
        return this;
    }

    public String getAnnotationFullyName() {
        return annotationFullyName;
    }

    public MappingAnnotation setAnnotationFullyName(String annotationFullyName) {
        this.annotationFullyName = annotationFullyName;
        return this;
    }

    public String getConsumesProp() {
        return consumesProp;
    }

    public MappingAnnotation setConsumesProp(String consumesProp) {
        this.consumesProp = consumesProp;
        return this;
    }

    public String getProducesProp() {
        return producesProp;
    }

    public MappingAnnotation setProducesProp(String producesProp) {
        this.producesProp = producesProp;
        return this;
    }

    public String getMethodProp() {
        return methodProp;
    }

    public MappingAnnotation setMethodProp(String methodProp) {
        this.methodProp = methodProp;
        return this;
    }

    public String getMethodType() {
        return methodType;
    }

    public MappingAnnotation setMethodType(String methodType) {
        this.methodType = methodType;
        return this;
    }

    public String getParamsProp() {
        return paramsProp;
    }

    public MappingAnnotation setParamsProp(String paramsProp) {
        this.paramsProp = paramsProp;
        return this;
    }

    public List<String> getScope() {
        return scope;
    }

    public MappingAnnotation setScope(String... scope) {
        this.scope = Arrays.asList(scope);
        return this;
    }

}
