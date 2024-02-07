package com.power.doc.constants;

/**
 * JAX-RS Annotations
 *
 * @author Zxq
 * @see JakartaJaxrsAnnotations
 * @deprecated Java EE has been renamed to Jakarta EE, an upgrade is recommended.
 */
@Deprecated
public final class JAXRSAnnotations {

    /**
     * JAX-RS@DefaultValue
     */
    public static final String JAX_DEFAULT_VALUE_FULLY = "javax.ws.rs.DefaultValue";
    /**
     * JAX-RS@HeaderParam
     */
    public static final String JAX_HEADER_PARAM_FULLY = "javax.ws.rs.HeaderParam";
    /**
     * JAX-RS@PathParam
     */
    public static final String JAX_PATH_PARAM_FULLY = "javax.ws.rs.PathParam";
    /**
     * JAX-RS@PATH
     */
    public static final String JAX_PATH_FULLY = "javax.ws.rs.Path";
    /**
     * JAX-RS@Produces
     */
    public static final String JAX_PRODUCES_FULLY = "javax.ws.rs.Produces";
    /**
     * JAX-RS@Consumes
     */
    public static final String JAX_CONSUMES_FULLY = "javax.ws.rs.Consumes";
    /**
     * JAX-RS@GET
     */
    public static final String JAX_GET_FULLY = "javax.ws.rs.GET";
    /**
     * JAX-RS@POST
     */
    public static final String JAX_POST_FULLY = "javax.ws.rs.POST";
    /**
     * JAX-RS@PUT
     */
    public static final String JAX_PUT_FULLY = "javax.ws.rs.PUT";
    /**
     * JAX-RS@DELETE
     */
    public static final String JAXB_DELETE_FULLY = "javax.ws.rs.DELETE";
    /**
     * JAX-RS@PATCH
     */
    public static final String JAXB_PATCH_FULLY = "javax.ws.rs.PATCH";
    /**
     * JAX-RS@HEAD
     */
    public static final String JAXB_HEAD_FULLY = "javax.ws.rs.HEAD";

    private JAXRSAnnotations() {
        throw new IllegalStateException("Utility class");
    }

}