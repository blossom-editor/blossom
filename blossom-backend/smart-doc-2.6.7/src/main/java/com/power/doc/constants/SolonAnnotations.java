package com.power.doc.constants;

/**
 * @author noear 2022/2/19 created
 */
public interface SolonAnnotations {

    String REQUEST_MAPPING = "Mapping";
    String REQUEST_MAPPING_FULLY = "org.noear.solon.annotation.Mapping";

    String GET_MAPPING = "Get";
    String GET_MAPPING_FULLY = "org.noear.solon.annotation.Get";

    String POST_MAPPING = "Post";
    String POST_MAPPING_FULLY = "org.noear.solon.annotation.Post";
    String PUT_MAPPING = "Put";
    String PUT_MAPPING_FULLY = "org.noear.solon.annotation.Put";
    String PATCH_MAPPING = "Patch";
    String PATCH_MAPPING_FULLY = "org.noear.solon.annotation.Patch";
    String DELETE_MAPPING = "Delete";
    String DELETE_MAPPING_FULLY = "org.noear.solon.annotation.Delete";

    String REQUEST_PARAM = "Param";
    String REQUEST_PARAM_FULL = "org.noear.solon.annotation.Param";

    String PATH_VAR = "PathVar";
    String PATH_VAR_FULL = "org.noear.solon.annotation.PathVar";

    String REQUEST_HERDER = "Header";
    String REQUEST_HERDER_FULL = "org.noear.solon.annotation.Header";

    String REQUEST_BODY = "Body";
    String REQUEST_BODY_FULLY = "org.noear.solon.annotation.Body";

    String CONTROLLER = "Controller";
    String CONTROLLER_FULL = "org.noear.solon.annotation.Controller";

    String COMPONENT = "Component";
    String COMPONENT_FULL = "org.noear.solon.annotation.Component";

    String REMOTING = "Remoting";
    String REMOTING_FULL = "org.noear.solon.annotation.Remoting";

    String MODE_AND_VIEW_FULLY = "org.noear.solon.core.handle.ModelAndView";

}
