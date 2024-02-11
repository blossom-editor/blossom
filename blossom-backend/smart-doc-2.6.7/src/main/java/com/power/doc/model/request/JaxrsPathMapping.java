package com.power.doc.model.request;

/**
 * @author yu 2021/8/28.
 */
public class JaxrsPathMapping extends RequestMapping {

    /**
     * url
     */
    private String url;

    /**
     * path
     */
    private String shortUrl;
    /**
     * methodType
     */
    private String methodType;
    /**
     * media type
     */
    private String mediaType;

    /**
     * method deprecated
     */
    private boolean deprecated;

    public static JaxrsPathMapping builder() {
        return new JaxrsPathMapping();
    }

    public String getUrl() {
        return url;
    }

    public JaxrsPathMapping setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public JaxrsPathMapping setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
        return this;
    }

    public String getMethodType() {
        return methodType;
    }

    public JaxrsPathMapping setMethodType(String methodType) {
        this.methodType = methodType;
        return this;
    }

    public String getMediaType() {
        return mediaType;
    }

    public JaxrsPathMapping setMediaType(String mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public JaxrsPathMapping setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
        return this;
    }
}
