# 一、项目介绍

私密信息：如`Git`账号等不在此列。
---

# 二、系统基本信息

本项目为Web项目，除`SpringBoot`使用`2.x`最新版本外，其余不做强制版本要求，只需兼容`JDK8`即可。中间件版本跟随需求变更。

`SpringBoot`版本说明请查阅：https://spring.io/projects/spring-boot#support。

简略的注意事项：
1. 接口文档使用`SmartDoc`，不要引入其他三方接口文档框架，如`Swagger`等。用法请查看第【4.1】项。
2. 接口调用需授权时，请查看第【4.2】项的系统登录。
3. 项目的模块说明请查看第【三】项。
4. 需遵循第【五】项中说明的开发规范。
5. 本项目依赖其他开源项目，本身为闭源项目。并遵循各类框架的开源协议。

---

# 三、模块说明

```
root
 ├─ aodc              相关文档
 ├─ backend-admin     后台应用，当不需要多应用部署时，默认使用该应用对外提供所有服务，该应用只写 Controller 层。
 ├─ backend-service   业务代码 Service 层与持久层 Mapper。
 |  ├─ service-base   提供基础的用户登录，RBAC，文件上传，系统字典，参数等功能，通常所有 Service 模块都依赖该模块。
 |  ├─ service-biz    业务代码，实现业务需求的模块。
 |  └─ service-flow   工作流引擎，如果系统需要使用工作流，那么通常所有 Service 模块都依赖该模块。
 |
 ├─ common            项目公共模块
 |  ├─ common-base    基础对象（如公共响应 R.class），工具类，异常捕获，动态日志级别，Caffeine 等。
 |  ├─ common-cache   缓存封装，Redis 封装，Redis Cache 封装。
 |  ├─ common-db      数据库模块，使用 mybatis plus，dynamic-datasource，慢SQL监控。
 |  ├─ common-es      ElasticSearch 功能封装。
 |  ├─ common-iaas    云厂商功能的封装，如对象存储，SMS短信服务，IM即时消息等。
 |  └─ common-wechat  微信相关功能的封装。
 | 
 ├─ expand-sentinel   对 Alibaba Sentinel 的封装，拓展了一些功能，实现了一些本地流量查询接口。
 ├─ expand-tracker    自研的链路追踪核心模块, 源自 tracker-core, 仅提供本地日志记录和日志中插入 Trace 的功能。
 └─ smart-doc         本地接口文档，在 Smart-Doc 上修改了样式，使之更符合开发阅读习惯。
```

---

# 四、使用说明

---

## 4.1 SmartDoc 接口文档使用说明

本系统接口文档依赖于`smart-doc`，并修改了`smart-doc`的文档样式，所以需要本地编译`smart-doc`才可使用。<a href="https://smart-doc-group.github.io/#/zh-cn/diy/highlight">官方文档地址</a>

<u>**第一步：**</u>在 smart-doc 目录执行如下命令。
```
mvn clean install -Dmaven.test.skip=true -Dfile.encoding=UTF-8 -Dmaven.javadoc.skip=true
```

<u>**第二步：**</u>搜索 `SmartDocConfig.class` 类，并执行该类生成接口文档。

---

## 4.2 系统登录

登录请访问`/<context-path>/login`

请求体:
```json
{
  // 授权客户端的ID, 见配置文件 prohect.auth.clients.client-id
  "clientId": "web",
  // 授权客户端的登录方式, 见配置文件 prohect.auth.clients.grant-type
  "grantType":"password",
  "username": "1",
  "password": "123"
}
```
登录返回的 token 信息需要放在请求头的 `Authorization` 中, 并以 `Bearer `开头, 例如

```
Authorization:Bearer ac27fd57303d4bddb0229c3b6d71b611
```

> token 的用法遵循 Bearer Token 规范, 可见：https://learning.postman.com/docs/sending-requests/authorization/#bearer-token
--- 

# 五、代码规范

## 5.1 API接口规范
1. **查询请求一律使用`GET`，对数据有变更时一律使用`POST`**。
2. `GET`请求如果传入参数`>=5`个时，需使用`@ModelAttribute`接收对象。
3. `GET`不要传入请求体。
4. URL中**不要包含大写字母，不要包含下划线`_`，不要使用驼峰，分割字符串需使用`-`**。
5. API不需要严格按照`Restful`设计，但仍需要遵循一些规则。
    1. 尽量不要包含动词，如查询用户：不要使用`GET /getUser`，而是使用`GET /user`
    2. 为了协调多种开发习惯，不使用`put`请求，不使用`delete`请求，不使用`head`请求等等。
    3. 基础增删改查路径为：列表`list`，分页`page`，详情`info`，新增修改`save`，删除`del`
6. 接口统一返回`Http`响应码`200`，除非强制要求按规范返回响应码。
7. 自定义响应码至少5位数，响应码可以是英文+数字的组合，但数字的前三位必须是规范的`Http`响应码。
    1. 【正例】：`40001`，`AUTH-40101`，`40402`，`50003`。
    2. 【反例】：使用`40400`表示用户未授权。使用`30000`表示服务器处理异常。
8. 对请求参数或请求体有加签需求时，签名一律放在`Header`中。
9. 授权信息一律放在请求头的`Authorization`字段中。

## 5.2 框架规范

### 5.2.1 `Spring`
1. `common`包，`expand`包下的对象要求使用`spring.factories`自动装配。
2. `backend-service`包下的对象要求使用`ServiceXXXScan`指定扫描路径，如`com.blossom.service.base.ServiceBaseScan`。
3. 没有多实现类需求时，不需要创建接口，如各类业务`Service`，多数在整个项目周期内都不会有多个实现类，增加接口徒增工作量。但多个实现类时有必要使用接口（例如：`common.iaas.OSManager.class`）
4. 功能为单独模块时，需使用`spring.factories`自动装配，不要在`SpringBootApplication`中指定扫描路径。
5. 不优先使用`spring-security`作为授权框，该框架过于复杂，不同人使用时差异过大，除非有非常非常严格的代码审查与安全扫描。
6. 不强制要求使用`validator`做参数校验，内部调用时，`service`内仍然会做必填等校验。
7. 在保证逻辑清晰且实现唯一的情况下，为了防止相同逻辑在多处实现，允许循环依赖，但仍然需要劲量避免循环依赖。
8. 优先使用`@Autowired`注入对象，不使用`@Resource`。JDK11中移除了`javax.annotation`包，需要手动加入才可使用`@Resource`。
9. 使用自定义配置时，不建议使用`@Value`注解，而是统一使用`ConfigurationProperties`创建配置对象类。为了统一维护以及方便查找。
10. 优先使用`Springboot`默认适配框架。
    1. 使用`jackson`作为`json`序列化工具，不要使用`fastjson`，`gson`或其他工具。
    2. 使用`logback`作为日志工具类。
    3. 使用`hikari`作为数据库连接池。

### 5.2.2 `MyBatis`
1. 使用驼峰命名转换`mybatis.configuration.map-underscore-to-camel-case: true`，`mapper.xml`文件中不要使用`resultMap`。
2. `Mapper.java`接口使用`@Mapper`注解，扫描时通过`@MapperScan(annotationClass = Mapper.class)`查找，不要使用路径的方式配置。这样是为了将`Mapper.java`文件于业务service等放在同一目录下。
3. 涉及到多表查询，复杂查询，使用xml显式编写SQL语句，不要使用`MyBatis Plus`，原则上只有单表简单查询时允许使用`MyBatis Plus`。
4. `Entity`类中不需要使用`@TableField`标明数据库字段。
5. `Entity`类中包含非数据库字段时，需要使用`@TableField(exist = false)`标明该字段非数据库字段。

### 5.2.3 其他
1. 使用`hutool`作为工具类，不要使用`common-lang3`，不要使用`guava`，不要使用`spring`自带工具类。
2. 本地缓存工具类使用`caffeine`或基于Java自己实现，不要使用`hutool`(存在性能问题)，不要使用`guava`。


## 5.3 代码规范
1. 本项目代码根据业务归类。
   1. 【正例】：用户`user`相关的`service,mapper,pojo`等都在`com.xx.user`路径下，不要根据类型分类。
   2. 【反例】：所有业务的`Service`类都在同一个路径`com.xx.service`下。
   3. 由于可能存在多个应用，所以`Controller`在应用所在的目录下，而不根据业务归类。
2. 请求实体类使用`XxxReq.class`
3. 响应实体类使用`XxxRes.class`
4. 数据库实体类使用`XxxEntity.class`
5. 非必要情况下不需要使用DTO，多数情况下只是多了一层无必要的转换。
    1. 若要求多应用服务，且共用模块，可以直接使用Entity来代替DTO。
    2. 若需要使用 Dubbo 等RPC框，则需要对外提供DTO来作为接口参数。     
6. 所有实体类需要继承`AbstractPOJO.class`，以便使用`to()`方法转换对象。
7. 实体类中所有对象使用封装类型，尤其注意 Boolean 类型的字段，非封装类型生成的 `getter` 方法为 `is` 。
8. 由于接口文档依赖于注释，所以`Controller`，实体类的注释必须填写。
9.  配置文件需要写明注释。
