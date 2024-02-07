package com.power.doc.model.annotation;

import java.util.Map;

/**
 * @author yu3.sun on 2022/10/1
 */
public class FrameworkAnnotations {

    private Map<String, EntryAnnotation> entryAnnotations;

    private HeaderAnnotation headerAnnotation;

    private Map<String, MappingAnnotation> mappingAnnotations;

    private PathVariableAnnotation pathVariableAnnotation;

    private RequestParamAnnotation requestParamAnnotation;

    private RequestBodyAnnotation requestBodyAnnotation;

    public static FrameworkAnnotations builder() {
        return new FrameworkAnnotations();
    }

    public Map<String, EntryAnnotation> getEntryAnnotations() {
        return entryAnnotations;
    }

    public FrameworkAnnotations setEntryAnnotations(Map<String, EntryAnnotation> entryAnnotations) {
        this.entryAnnotations = entryAnnotations;
        return this;
    }

    public HeaderAnnotation getHeaderAnnotation() {
        return headerAnnotation;
    }

    public FrameworkAnnotations setHeaderAnnotation(HeaderAnnotation headerAnnotation) {
        this.headerAnnotation = headerAnnotation;
        return this;
    }

    public Map<String, MappingAnnotation> getMappingAnnotations() {
        return mappingAnnotations;
    }

    public FrameworkAnnotations setMappingAnnotations(Map<String, MappingAnnotation> mappingAnnotation) {
        this.mappingAnnotations = mappingAnnotation;
        return this;
    }

    public PathVariableAnnotation getPathVariableAnnotation() {
        return pathVariableAnnotation;
    }

    public FrameworkAnnotations setPathVariableAnnotation(PathVariableAnnotation pathVariableAnnotation) {
        this.pathVariableAnnotation = pathVariableAnnotation;
        return this;
    }

    public RequestParamAnnotation getRequestParamAnnotation() {
        return requestParamAnnotation;
    }

    public FrameworkAnnotations setRequestParamAnnotation(RequestParamAnnotation requestParamAnnotation) {
        this.requestParamAnnotation = requestParamAnnotation;
        return this;
    }

    public RequestBodyAnnotation getRequestBodyAnnotation() {
        return requestBodyAnnotation;
    }

    public FrameworkAnnotations setRequestBodyAnnotation(RequestBodyAnnotation requestBodyAnnotation) {
        this.requestBodyAnnotation = requestBodyAnnotation;
        return this;
    }
}
