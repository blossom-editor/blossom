package com.power.doc.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.power.common.model.EnumDictionary;
import com.power.common.util.CollectionUtil;
import com.power.common.util.OkHttp3Util;
import com.power.common.util.StringUtil;
import com.power.doc.constants.DocGlobalConstants;
import com.power.doc.constants.TornaConstants;
import com.power.doc.model.*;
import com.power.doc.model.rpc.RpcApiDependency;
import com.power.doc.model.torna.*;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;

import java.util.*;

import static com.power.doc.constants.DocGlobalConstants.ARRAY;
import static com.power.doc.constants.DocGlobalConstants.OBJECT;
import static com.power.doc.constants.TornaConstants.ENUM_PUSH;
import static com.power.doc.constants.TornaConstants.PUSH;

/**
 * @author xingzi 2021/4/28 16:15
 **/
public class TornaUtil {

    public static void pushToTorna(TornaApi tornaApi, ApiConfig apiConfig, JavaProjectBuilder builder) {
        //Build push document information
        Map<String, String> requestJson = TornaConstants.buildParams(PUSH, new Gson().toJson(tornaApi), apiConfig);
        //Push dictionary information
        Map<String, Object> dicMap = new HashMap<>(2);
        List<TornaDic> docDicts = TornaUtil.buildTornaDic(DocUtil.buildDictionary(apiConfig, builder));
        if (CollectionUtil.isNotEmpty(docDicts)) {
            dicMap.put("enums", docDicts);
            Map<String, String> dicRequestJson = TornaConstants.buildParams(ENUM_PUSH, new Gson().toJson(dicMap), apiConfig);
            String dicResponseMsg = OkHttp3Util.syncPostJson(apiConfig.getOpenUrl(), new Gson().toJson(dicRequestJson));
            TornaUtil.printDebugInfo(apiConfig, dicResponseMsg, dicRequestJson, ENUM_PUSH);
        }
        //Get the response result
        String responseMsg = OkHttp3Util.syncPostJson(apiConfig.getOpenUrl(), new Gson().toJson(requestJson));
        //Print the log of pushing documents to Torna
        TornaUtil.printDebugInfo(apiConfig, responseMsg, requestJson, PUSH);
    }

    public static boolean setDebugEnv(ApiConfig apiConfig, TornaApi tornaApi) {
        boolean hasDebugEnv = StringUtils.isNotBlank(apiConfig.getDebugEnvName())
                &&
                StringUtils.isNotBlank(apiConfig.getDebugEnvUrl());
        //Set up the test environment
        List<DebugEnv> debugEnvs = new ArrayList<>();
        if (hasDebugEnv) {
            DebugEnv debugEnv = new DebugEnv();
            debugEnv.setName(apiConfig.getDebugEnvName());
            debugEnv.setUrl(apiConfig.getDebugEnvUrl());
            debugEnvs.add(debugEnv);
        }
        tornaApi.setDebugEnvs(debugEnvs);
        return hasDebugEnv;
    }

    public static void printDebugInfo(ApiConfig apiConfig, String responseMsg, Map<String, String> requestJson, String category) {
        if (apiConfig.isTornaDebug()) {
            String sb = "Configuration information : \n" +
                    "OpenUrl: " +
                    apiConfig.getOpenUrl() +
                    "\n" +
                    "appToken: " +
                    apiConfig.getAppToken() +
                    "\n";
            System.out.println(sb);
            try {
                JsonElement element = JsonParser.parseString(responseMsg);
                TornaRequestInfo info = new TornaRequestInfo()
                        .of()
                        .setCategory(category)
                        .setCode(element.getAsJsonObject().get(TornaConstants.CODE).getAsString())
                        .setMessage(element.getAsJsonObject().get(TornaConstants.MESSAGE).getAsString())
                        .setRequestInfo(requestJson)
                        .setResponseInfo(responseMsg);
                System.out.println(info.buildInfo());
            } catch (Exception e) {
                //Ex : Nginx Error,Tomcat Error
                System.out.println("Response Error : \n" + responseMsg);
            }
        }
    }

    /**
     * build apis
     *
     * @param apiMethodDocs apiMethodDocs
     * @param hasDebugEnv   has debug environment
     * @return List of Api
     */
    public static List<Apis> buildApis(List<ApiMethodDoc> apiMethodDocs, boolean hasDebugEnv) {
        //Parameter list
        List<Apis> apis = new ArrayList<>();
        Apis methodApi;
        //Iterative classification interface
        for (ApiMethodDoc apiMethodDoc : apiMethodDocs) {
            methodApi = new Apis();
            methodApi.setIsFolder(TornaConstants.NO);
            methodApi.setName(apiMethodDoc.getDesc());
            methodApi.setUrl(hasDebugEnv ? subFirstUrlOrPath(apiMethodDoc.getPath()) : subFirstUrlOrPath(apiMethodDoc.getUrl()));
            methodApi.setHttpMethod(apiMethodDoc.getType());
            methodApi.setContentType(apiMethodDoc.getContentType());
            methodApi.setDescription(apiMethodDoc.getDetail());
            methodApi.setIsShow(TornaConstants.YES);
            methodApi.setAuthor(apiMethodDoc.getAuthor());
            methodApi.setOrderIndex(apiMethodDoc.getOrder());

            methodApi.setHeaderParams(buildHerder(apiMethodDoc.getRequestHeaders()));
            methodApi.setResponseParams(buildParams(apiMethodDoc.getResponseParams()));
            methodApi.setIsRequestArray(apiMethodDoc.getIsRequestArray());
            methodApi.setIsResponseArray(apiMethodDoc.getIsResponseArray());
            methodApi.setRequestArrayType(apiMethodDoc.getRequestArrayType());
            methodApi.setResponseArrayType(apiMethodDoc.getResponseArrayType());
            methodApi.setDeprecated(apiMethodDoc.isDeprecated() ? "Deprecated" : null);
            //Path
            if (CollectionUtil.isNotEmpty(apiMethodDoc.getPathParams())) {
                methodApi.setPathParams(buildParams(apiMethodDoc.getPathParams()));
            }

            if (CollectionUtil.isNotEmpty(apiMethodDoc.getQueryParams())
                    && DocGlobalConstants.FILE_CONTENT_TYPE.equals(apiMethodDoc.getContentType())) {
                // file upload
                methodApi.setRequestParams(buildParams(apiMethodDoc.getQueryParams()));
            } else if (CollectionUtil.isNotEmpty(apiMethodDoc.getQueryParams())) {
                methodApi.setQueryParams(buildParams(apiMethodDoc.getQueryParams()));
            }
            //Json
            if (CollectionUtil.isNotEmpty(apiMethodDoc.getRequestParams())) {
                methodApi.setRequestParams(buildParams(apiMethodDoc.getRequestParams()));
            }
            apis.add(methodApi);
        }
        return apis;
    }

    /**
     * build apis
     *
     * @param apiMethodDocs apiMethodDocs
     * @return List of Api
     */
    public static List<Apis> buildDubboApis(List<RpcJavaMethod> apiMethodDocs) {
        //Parameter list
        List<Apis> apis = new ArrayList<>();
        Apis methodApi;
        //Iterative classification interface
        for (RpcJavaMethod apiMethodDoc : apiMethodDocs) {
            methodApi = new Apis();
            methodApi.setIsFolder(TornaConstants.NO);
            methodApi.setName(apiMethodDoc.getDesc());
            methodApi.setDescription(apiMethodDoc.getDetail());
            methodApi.setIsShow(TornaConstants.YES);
            methodApi.setAuthor(apiMethodDoc.getAuthor());
            methodApi.setUrl(apiMethodDoc.getMethodDefinition());
            methodApi.setResponseParams(buildParams(apiMethodDoc.getResponseParams()));
            methodApi.setOrderIndex(apiMethodDoc.getOrder());
            methodApi.setDeprecated(apiMethodDoc.isDeprecated() ? "Deprecated" : null);
            //Json
            if (CollectionUtil.isNotEmpty(apiMethodDoc.getRequestParams())) {
                methodApi.setRequestParams(buildParams(apiMethodDoc.getRequestParams()));
            }
            apis.add(methodApi);
        }
        return apis;
    }

    /**
     * build request header
     *
     * @param apiReqParams Request header parameter list
     * @return List of HttpParam
     */
    public static List<HttpParam> buildHerder(List<ApiReqParam> apiReqParams) {
        HttpParam httpParam;
        List<HttpParam> headers = new ArrayList<>();
        for (ApiReqParam header : apiReqParams) {
            httpParam = new HttpParam();
            httpParam.setName(header.getName());
            httpParam.setRequired(header.isRequired() ? TornaConstants.YES : TornaConstants.NO);
            httpParam.setExample(StringUtil.removeQuotes(header.getValue()));
            if (StringUtil.isNotEmpty(header.getSince()) && !DocGlobalConstants.DEFAULT_VERSION.equals(header.getSince())) {
                httpParam.setDescription(header.getDesc() + "@since " + header.getSince());
            } else {
                httpParam.setDescription(header.getDesc());
            }
            headers.add(httpParam);
        }
        return headers;
    }

    /**
     * build  request response params
     *
     * @param apiParams Param list
     * @return List of HttpParam
     */
    public static List<HttpParam> buildParams(List<ApiParam> apiParams) {
        HttpParam httpParam;
        List<HttpParam> bodies = new ArrayList<>();
        for (ApiParam apiParam : apiParams) {
            httpParam = new HttpParam();
            httpParam.setName(apiParam.getField());
            httpParam.setOrderIndex(apiParam.getId());
            httpParam.setMaxLength(apiParam.getMaxLength());
            String type = apiParam.getType();
            if (Objects.equals(type, DocGlobalConstants.PARAM_TYPE_FILE) && apiParam.isHasItems()) {
                type = TornaConstants.PARAM_TYPE_FILE_ARRAY;
            }
            httpParam.setType(type);
            httpParam.setRequired(apiParam.isRequired() ? TornaConstants.YES : TornaConstants.NO);
            httpParam.setExample(StringUtil.removeQuotes(apiParam.getValue()));
            if (StringUtil.isNotEmpty(apiParam.getVersion()) && !DocGlobalConstants.DEFAULT_VERSION.equals(apiParam.getVersion())) {
                httpParam.setDescription(DocUtil.replaceNewLineToHtmlBr(apiParam.getDesc()) + "@since " + apiParam.getVersion());
            } else {
                httpParam.setDescription(DocUtil.replaceNewLineToHtmlBr(apiParam.getDesc()));
            }
            httpParam.setEnumInfo(apiParam.getEnumInfo());
            if (apiParam.getChildren() != null) {
                httpParam.setChildren(buildParams(apiParam.getChildren()));
            }
            bodies.add(httpParam);
        }
        return bodies;
    }

    public static String buildDependencies(List<RpcApiDependency> dependencies) {
        StringBuilder s = new StringBuilder();
        if (CollectionUtil.isNotEmpty(dependencies)) {
            for (RpcApiDependency r : dependencies) {
                s.append(r.toString()).append("\n\n");
            }
        }
        return s.toString();
    }

    public static List<CommonErrorCode> buildErrorCode(ApiConfig config, JavaProjectBuilder javaProjectBuilder) {
        List<CommonErrorCode> commonErrorCodes = new ArrayList<>();
        CommonErrorCode commonErrorCode;
        List<ApiErrorCode> errorCodes = DocUtil.errorCodeDictToList(config, javaProjectBuilder);
        if (CollectionUtil.isNotEmpty(errorCodes)) {
            for (EnumDictionary code : errorCodes) {
                commonErrorCode = new CommonErrorCode();
                commonErrorCode.setCode(code.getValue());
                // commonErrorCode.setSolution(code.getDesc());
                commonErrorCode.setMsg(DocUtil.replaceNewLineToHtmlBr(code.getDesc()));
                commonErrorCodes.add(commonErrorCode);
            }
        }
        return commonErrorCodes;
    }

    public static List<TornaDic> buildTornaDic(List<ApiDocDict> apiDocDicts) {
        List<TornaDic> dics = new ArrayList<>();
        TornaDic tornaDic;
        if (CollectionUtil.isNotEmpty(apiDocDicts)) {
            for (ApiDocDict doc : apiDocDicts) {
                tornaDic = new TornaDic();
                tornaDic.setName(doc.getTitle())
                        .setDescription(DocUtil.replaceNewLineToHtmlBr(doc.getDescription()))
                        .setItems(buildTornaDicItems(doc.getDataDictList()));
                dics.add(tornaDic);
            }
        }
        return dics;
    }

    private static List<HttpParam> buildTornaDicItems(List<DataDict> dataDicts) {
        List<HttpParam> apis = new ArrayList<>();
        HttpParam api;
        if (CollectionUtil.isNotEmpty(dataDicts)) {
            for (EnumDictionary d : dataDicts) {
                api = new HttpParam();
                api.setName(d.getName());
                api.setType(d.getType());
                api.setValue(d.getValue());
                api.setDescription(d.getDesc());
                apis.add(api);
            }
        }
        return apis;
    }

    /**
     * 设置请求参数是否为数组
     *
     * @param apiMethodDoc 请求参数
     */
    public static void setTornaArrayTags(JavaMethod method, ApiMethodDoc apiMethodDoc) {
        String returnTypeName = method.getReturnType().getCanonicalName();
        apiMethodDoc.setIsRequestArray(0);
        apiMethodDoc.setIsResponseArray(0);
        boolean respArray = JavaClassValidateUtil.isCollection(returnTypeName) || JavaClassValidateUtil.isArray(returnTypeName);
        //response
        if (respArray) {
            apiMethodDoc.setIsResponseArray(1);
            String className = getType(method.getReturnType().getGenericCanonicalName());
            String arrayType = JavaClassValidateUtil.isPrimitive(className) ? className : OBJECT;
            apiMethodDoc.setResponseArrayType(arrayType);
        }
        //request
        if (CollectionUtil.isNotEmpty(method.getParameters())) {
            for (JavaParameter param : method.getParameters()) {
                String typeName = param.getType().getCanonicalName();
                boolean reqArray = JavaClassValidateUtil.isCollection(typeName) || JavaClassValidateUtil.isArray(typeName);
                if (reqArray) {
                    apiMethodDoc.setIsRequestArray(1);
                    String className = getType(param.getType().getGenericCanonicalName());
                    String arrayType = JavaClassValidateUtil.isPrimitive(className) ? className : OBJECT;
                    apiMethodDoc.setRequestArrayType(arrayType);
                    break;
                }
            }
        }

    }

    private static String getArrayType(Map<String, Object> schemaMap) {
        String arrayType = null;
        if (Objects.nonNull(schemaMap) && Objects.equals(ARRAY, schemaMap.get("type"))) {
            Map<String, Object> innerSchemeMap = (Map<String, Object>) schemaMap.get("items");
            if (Objects.nonNull(innerSchemeMap)) {
                String type = (String) innerSchemeMap.get("type");
                if (StringUtil.isNotEmpty(type)) {
                    String className = getType(type);
                    arrayType = JavaClassValidateUtil.isPrimitive(className) ? className : OBJECT;
                }
            }
        }
        return arrayType;
    }

    private static String getType(String typeName) {
        String gicType;
        //get generic type
        if (typeName.contains("<")) {
            gicType = typeName.substring(typeName.indexOf("<") + 1, typeName.lastIndexOf(">"));
        } else {
            gicType = typeName;
        }
        if (gicType.contains("[")) {
            gicType = gicType.substring(0, gicType.indexOf("["));
        }
        return gicType.substring(gicType.lastIndexOf(".") + 1).toLowerCase();
    }

    private static String subFirstUrlOrPath(String url) {
        if (StringUtil.isEmpty(url)) {
            return StringUtil.EMPTY;
        }
        if (!url.contains(DocGlobalConstants.MULTI_URL_SEPARATOR)) {
            return url;
        }
        String[] split = StringUtil.split(url, DocGlobalConstants.MULTI_URL_SEPARATOR);
        return split[0];
    }


}
