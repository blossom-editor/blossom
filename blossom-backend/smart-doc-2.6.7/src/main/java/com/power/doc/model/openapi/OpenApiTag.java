package com.power.doc.model.openapi;

import java.util.Objects;

/**
 * open api tag
 *
 * @author <a href="mailto:cqmike0315@gmail.com">chenqi</a>
 * @version 1.0
 */
public class OpenApiTag {

    /**
     * the tag name
     */
    private String name;

    /**
     * the tag description
     */
    private String description;

    public OpenApiTag() {
    }

    public OpenApiTag(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static OpenApiTag of(String name, String description) {
        return new OpenApiTag(name, description);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpenApiTag that = (OpenApiTag) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getDescription(), that.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription());
    }
}
