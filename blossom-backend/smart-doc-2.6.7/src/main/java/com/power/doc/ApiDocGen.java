package com.power.doc;

import cn.hutool.core.date.DateUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.ds.DSFactory;
import cn.hutool.setting.Setting;
import com.blossom.backend.base.auth.enums.GrantTypeEnum;
import com.blossom.backend.base.auth.exception.AuthRCode;
import com.blossom.backend.base.user.UserTypeEnum;
import com.blossom.backend.server.doc.DocTypeEnum;
import com.blossom.backend.server.folder.FolderTypeEnum;
import com.blossom.common.base.enums.YesNo;
import com.blossom.common.base.pojo.RCode;
import com.power.doc.builder.HtmlApiDocBuilder;
import com.power.doc.model.*;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Smart-Doc 文档生成工具
 * <p><a href="https://smart-doc-group.github.io/#/zh-cn/diy/highlight">官方文档地址</a>
 * <p>
 * <p> 使用 Smart-Doc 时重写了 html 页面样式与, 使之更符合中文阅读习惯。并重写了字典读取, 可以手动添加非枚举类型的字典信息。
 * <p> 在使用前需要先编译 smart-doc 项目, 使用如下命令:
 * <pre>mvn clean install -Dmaven.test.skip=true -Dfile.encoding=UTF-8 -Dmaven.javadoc.skip=true </pre>
 */
@Slf4j
@SuppressWarnings("all")
public class ApiDocGen {

    private static String IP_DEVE = "http://127.0.0.1";
    private static String VERSION = "v1.9.0";

    private static final List<String> sourceCodePath = new ArrayList<String>() {{
        this.add("blossom-backend/common/common-base");
        this.add("blossom-backend/common/common-cache");
        this.add("blossom-backend/common/common-db");
        this.add("blossom-backend/common/common-iaas");
        this.add("blossom-backend/backend/src/main/java/com/blossom/backend");
        this.add("blossom-backend/expand-sentinel/expand-sentinel-metric");
    }};

    private static final boolean genDictFromDB = true;
    private static final String dictDbSetting = "dict-db.setting";
    private static final String dictQuerySQL = "select param_name,param_desc from base_sys_param";

    public static void main(String[] args) throws SQLException {
        long start = System.currentTimeMillis();
        genDoc();
        System.out.println(SEPARATOR);
        System.out.println("接口文档生成结束, 用时: " + (System.currentTimeMillis() - start) + " ms");
    }

    /**
     * 生成接口文档
     */
    public static void genDoc() throws SQLException {
        ApiConfig config = new ApiConfig();
        config.setProjectName(PROJECT_NAME);
        config.setOutPath("doc/backend-api");
        config.setServerUrl(IP_DEVE);
        config.setStyle("darcula"); // 另一个代码块样式 solarized-light
        config.setAllInOne(true);
        // 创建一个类似swagger的可调试接口的文档页面, 仅在AllInOne模式中起作用。
        config.setCreateDebugPage(false);
        // 请求示例
        config.setRequestExample(true);
        // 设置为 true 会将枚举详情展示到参数表中, 默认关闭
        config.setInlineEnum(true);
        // 是否显示接口作者名称
        config.setShowAuthor(false);
        // 配置true会在注释栏自动显示泛型的真实类型短类名
        config.setDisplayActualType(true);
        // 文件名
        config.setAllInOneDocFileName("index.html");
        // 源码扫描地址
        config.setSourceCodePaths(sourceCodePath());
        // api分组
        config.setGroups(groups());
        // 数据字典
        config.setDataDictionaries(dataDict());
        // 动态数据字典
        config.setDataDictionariesDynamic(dataDictDynamic());
        // 错误码列表
        config.setErrorCodeDictionaries(errorCodes());
        // 版本更新日志
        config.setRevisionLogs(revisions());

        System.out.println(SEPARATOR);
        System.out.println("正在生成 Html 接口文档...");
        HtmlApiDocBuilder.buildApiDoc(config);
    }

    /**
     * 版本说明
     */
    private static List<RevisionLog> revisions() {
        List<RevisionLog> visions = new ArrayList<>();
        RevisionLog apiUrl = new RevisionLog();
        apiUrl.setVersion("1.访问地址");
        apiUrl.setRemarks("开发环境: " + IP_DEVE);
        visions.add(apiUrl);

        RevisionLog authHowToUse = new RevisionLog();
        authHowToUse.setVersion("2.Token使用");
        authHowToUse.setRemarks(
                "使用 /login 接口登录后, 将会返回 Token 信息。" +
                        "\n遵循标准 Bearer 使用规范, 需将 Token 传入请求头 \"Authorization\" 字段中, 并在前拼接 \"Bearer \"。" +
                        "\n例如：\"Authorization\" : \"Bearer ad047609fcde4ebbb358a6a4b0f38f43\"" +
                        "\n可参阅：https://learning.postman.com/docs/sending-requests/authorization/#bearer-token。"
        );
        visions.add(authHowToUse);


        RevisionLog standards = new RevisionLog();
        standards.setVersion("3.接口规范");
        standards.setRemarks(
                "为了提高沟通效率, 降低思考成本, API一律符合如下规范." +
                        "\n1. 接口统一返回 Http 响应码 200, 业务响应码 [20000] 为正常, 业务响应码 [AUTH-40101] 为未授权。" +
                        "\n2. 接口只包含 Get,Post 请求, Get 请求为查询, Post 请求为增删改。" +
                        "\n3. 文档是使用 [OP] 标签的接口为开放接口, 该接口可以在未授权的情况下调用。" +
                        "\n4. 基础的[增删改查]API路径为：" +
                        "\n  a. /list : 列表, 不是所有业务都有该接口, 通常是确保业务没有大量数据, 如菜单列表" +
                        "\n  b. /page : 分页列表" +
                        "\n  c. /info : 详情, 绝大部分详情只能通过业务ID查询" +
                        "\n  d. /save : 新增修改接口合并, 会根据是否传入对应业务ID来判断, 适用于新增修改字段基本相同的业务" +
                        "\n  f. /add  : 新增, 传入ID无效" +
                        "\n  g. /upd  : 修改, 需要传入ID" +
                        "\n  h. /del  : 删除" +
                        "\n  i. 其他业务请求请参照具体API"
        );
        visions.add(standards);

        RevisionLog docHelp = new RevisionLog();
        docHelp.setVersion("4.文档说明");
        docHelp.setRemarks(
                "本接口文档的使用事项" +
                        "\n1. [OP] : 接口名称中包含[OP] 即说明该接口为开放接口, 可以在非登录情况下调用。" +
                        "\n2. [TBC]: 接口名称中包含[TBC]即说明该接口具体业务需求尚不明确, 需要进一步等待确认。"
        );
        visions.add(docHelp);
        return visions;
    }

    /**
     * 指明文件路径
     */
    private static List<SourceCodePath> sourceCodePath() {
        List<SourceCodePath> results = new ArrayList<>();
        for (String s : sourceCodePath) {
            SourceCodePath path = new SourceCodePath();
            path.setPath(s);
            results.add(path);
        }
        return results;
    }

    /**
     * 增加接口分组
     */
    private static List<ApiGroup> groups() {
        List<ApiGroup> results = new ArrayList<>();

        ApiGroup common = new ApiGroup();
        common.setName("通用功能");
        common.setApis("com.blossom.backend.base.*");
        results.add(common);

        ApiGroup biz = new ApiGroup();
        biz.setName("业务功能");
        biz.setApis("com.blossom.backend.server.*");
        results.add(biz);

        ApiGroup tp = new ApiGroup();
        tp.setName("三方接口");
        tp.setApis("com.blossom.backend.thirdparty.*");
        results.add(tp);

        ApiGroup sentinel = new ApiGroup();
        sentinel.setName("拓展功能");
        sentinel.setApis("com.blossom.expand.sentinel.metric.*");
        results.add(sentinel);

        return results;
    }

    /**
     * 增加数据字典, 最好在字典中增加说明字段. 如{@link GrantTypeEnum#getDesc()}
     */
    private static List<ApiDataDictionary> dataDict() {
        List<ApiDataDictionary> results = new ArrayList<>();

        ApiDataDictionary dictGrantType = new ApiDataDictionary();
        dictGrantType.setTitle("登录方式 [GrantTypeEnum]");
        dictGrantType.setEnumClass(GrantTypeEnum.class);
        dictGrantType.setCodeField("type");
        dictGrantType.setDescField("desc");
        results.add(dictGrantType);

        ApiDataDictionary yesNo = new ApiDataDictionary();
        yesNo.setTitle("是否,真假字典 [YesNo]");
        yesNo.setEnumClass(YesNo.class);
        yesNo.setCodeField("value");
        yesNo.setDescField("name");
        results.add(yesNo);

        ApiDataDictionary userType = new ApiDataDictionary();
        userType.setTitle("用户类型 [UserTypeEnum]");
        userType.setEnumClass(UserTypeEnum.class);
        userType.setCodeField("type");
        userType.setDescField("desc");
        results.add(userType);

        ApiDataDictionary docType = new ApiDataDictionary();
        docType.setTitle("文档类型 [DocTypeEnum]");
        docType.setEnumClass(DocTypeEnum.class);
        docType.setCodeField("type");
        docType.setDescField("desc");
        results.add(docType);

        ApiDataDictionary folderType = new ApiDataDictionary();
        folderType.setTitle("文件夹类型 [FolderTypeEnum]");
        folderType.setEnumClass(FolderTypeEnum.class);
        folderType.setCodeField("type");
        folderType.setDescField("desc");
        results.add(folderType);

//        ApiDataDictionary backupType = new ApiDataDictionary();
//        backupType.setTitle("备份类型 [BackupTypeEnum]");
//        backupType.setEnumClass(BackupTypeEnum.class);
//        results.add(backupType);

        return results;
    }

    /**
     * 配置在数据库中的动态字典。
     */
    private static List<ApiDocDict> dataDictDynamic() throws SQLException {
        List<ApiDocDict> doctDocList = new ArrayList<>();

        if (genDictFromDB) {
            long start = System.currentTimeMillis();
            System.out.println("正在查询数据字典...");
            DataSource ds = DSFactory.create(setting).getDataSource();

            List<Entity> list = Db.use(ds).query(dictQuerySQL);
            List<DataDict> dictList = new ArrayList<>();

            ApiDocDict params = new ApiDocDict();
            params.setOrder(100);
            params.setTitle("系统参数表配置内容");

            List<DataDict> paramList = new ArrayList<>();
            for (Entity entity : list) {
                DataDict param = new DataDict();
                param.setValue(entity.getStr("param_name"));
                param.setDesc(entity.getStr("param_desc"));
                paramList.add(param);
            }
            params.setDataDictList(paramList);
            doctDocList.add(params);

            System.out.println("查询数据字典完成, 用时: " + (System.currentTimeMillis() - start) + " ms");
        }

        return doctDocList;
    }

    /**
     * 接口响应码字典, 最好在字典中增加说明字段. 如{@link AuthRCode#getDesc()}
     */
    private static List<ApiErrorCodeDictionary> errorCodes() {
        List<ApiErrorCodeDictionary> results = new ArrayList<>();

        ApiErrorCodeDictionary dict1 = new ApiErrorCodeDictionary();
        dict1.setEnumClass(RCode.class);
        dict1.setCodeField("code");
        dict1.setDescField("msg");
        results.add(dict1);

        ApiErrorCodeDictionary dict2 = new ApiErrorCodeDictionary();
        dict2.setEnumClass(AuthRCode.class);
        dict2.setCodeField("code");
        dict2.setDescField("desc");
        results.add(dict2);

        return results;
    }

    private static String SEPARATOR = "===============================================================================";
    private static String PROJECT_NAME = "";
    private static Setting setting;

    /**
     * 设置默认日志级别
     */
    static {
        System.out.println(SEPARATOR);
        PROJECT_NAME = "《Blossom 接口文档》\n　文档版本:" + VERSION + "\n　文档时间:" + DateUtil.now();
        System.out.println(String.format("准备生成接口文档\n项目:%s \n", PROJECT_NAME));

        System.out.println("环境地址:");
        System.out.println(" - DEV : " + "127.0.0.1:9999");

        System.out.println(SEPARATOR);

        System.out.println(String.format("数据字典是否从数据库中获取: [%s]", genDictFromDB));
        if (genDictFromDB) {
            setting = new Setting(dictDbSetting);
            System.out.println("DataBase: " + setting.getStr("url"));
            System.out.println("Dict SQL: " + dictQuerySQL);
        }
    }
}
