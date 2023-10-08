## smart-doc版本

### 版本号：2.6.7

- 更新日期: 2023-03-28
- 更新内容：
  1. 修复JAX-RS Produces注解使用时contentType为空的bug，[#415](https://github.com/smart-doc-group/smart-doc/issues/415)
  2. 修复不能忽略被继承类中相应字段bug。[#453](https://github.com/smart-doc-group/smart-doc/issues/453)
  3. 修复使用@see tag时的未写注释导致空指针问腿。[#451](https://github.com/smart-doc-group/smart-doc/issues/451）
  4. 修复openapi的生成bug。[#458](https://github.com/smart-doc-group/smart-doc/issues/458)
  5. 修复openapi无法归类的问题。 [#pr460](https://github.com/smart-doc-group/smart-doc/pull/460)

### 版本号：2.6.6

- 更新日期: 2023-03-08
- 更新内容：
  1. 修复集合或者数组入参推送torna数据错误，[#415](https://github.com/smart-doc-group/smart-doc/issues/415)
  2. 请求注解consumes支持。[#424](https://github.com/smart-doc-group/smart-doc/issues/424)
  3. url中path param占位符解析优化。[#447](https://github.com/smart-doc-group/smart-doc/issues/447）
 
### 版本号：2.6.5

- 更新日期: 2023-02-27
- 更新内容：
  1. 修复集合或者数组入参推送torna数据错误，[#415](https://github.com/smart-doc-group/smart-doc/issues/415)
  2. 文档泛型支持数组类型标注。[#421](https://github.com/smart-doc-group/smart-doc/issues/421)
  3. list<Primitive Type> 和list<File> 类型openapi生成错误。[#423](https://github.com/smart-doc-group/smart-doc/issues/423)
  4. 修复设置requestFieldToUnderline为true时path参数丢失，[#103](https://github.com/smart-doc-group/smart-doc/issues/103)
  5. 优化自定义字典值处理接口DictionaryValuesResolver[#pr428](https://github.com/smart-doc-group/smart-doc/pull/428)
  6. 修复HTML模版文档的描述展示[#pr438](https://github.com/smart-doc-group/smart-doc/pull/438)

### 版本号：2.6.4

- 更新日期: 2023-01-28
- 更新内容：
  1. 支持没有getter方法的枚举字典提取，[#408](https://github.com/smart-doc-group/smart-doc/issues/408)
  2. 优化插件遇到源码语法错误时的报错提示。[#402](https://github.com/smart-doc-group/smart-doc/issues/402)
  3. URL 结尾存在多余'/' 。[#396](https://github.com/smart-doc-group/smart-doc/issues/396)
  4. 支持SpringBoot 3.0和Jakarta EE 10.
  5. 二维数据推送torna类型错误，[#380](https://github.com/smart-doc-group/smart-doc/issues/380)
  6. 优化插件对常用无关文档依赖做加载优化。
  7. 支持controller继承接口中的注解[#pr392](https://github.com/smart-doc-group/smart-doc/pull/392)。

### 版本号：2.6.3

- 更新日期: 2022-12-10
- 更新内容：
  1. 单个接口推送到torna目录显示优化，[#385](https://github.com/smart-doc-group/smart-doc/pull/385)
  2. 修改timestamp类型在文档中显示错误。
  3. 修改枚举重复问题。
  4. 支持SpringBoot 3.0和Jakarta EE 10.
  5. 二维数据推送torna类型错误，[#380](https://github.com/smart-doc-group/smart-doc/issues/380)
  6. 优化插件对常用无关文档依赖做加载优化。
  7. 支持controller继承接口中的注解[#pr392](https://github.com/smart-doc-group/smart-doc/pull/392)。

### 版本号：2.6.2

- 更新日期: 2022-10-29
- 更新内容：
  1. 修复枚举渲染换行问题，[#377](https://github.com/smart-doc-group/smart-doc/issues/377)
  2. 文档url显示错误，[#379](https://github.com/smart-doc-group/smart-doc-maven-plugin/issues/379)
  3. 优化openapi文档生成

### 版本号：2.6.1

- 更新日期: 2022-10-23
- 更新内容：
  1. 修复枚举渲染和rpc文档字段类型，[#377](https://github.com/smart-doc-group/smart-doc/issues/377)
  2. markdown文档字段描述填充错误，[#378](https://github.com/smart-doc-group/smart-doc-maven-plugin/issues/378)
  3. 格式化markdown模板错误

### 版本号：2.6.0

- 更新日期: 2022-10-15
- 更新内容：
    1. 修复markdown从出现换行错误导致渲染混乱，[#340](https://github.com/smart-doc-group/smart-doc-maven-plugin/issues/17)
    2. Post请求中文参数编码错误，[#19](https://github.com/smart-doc-group/smart-doc-maven-plugin/issues/19)
    3. 支持推送@since到torna的注释中，[#344](https://github.com/smart-doc-group/smart-doc/issues/344)
    4. spring mvc mapping method数组书写方式解析错误，[#361](https://github.com/smart-doc-group/smart-doc/issues/361)
    5. 修复jax-rs方法上绑定集合解析错误，[#358](https://github.com/smart-doc-group/smart-doc/issues/358)
    6. 修复customResponseFields配置无效错误，[#355](https://github.com/smart-doc-group/smart-doc/issues/355)
    7. 对Jackson注解@JsonNaming驼峰转下划线的支持,[#349](https://github.com/smart-doc-group/smart-doc/issues/349)
    8. curl请求用例显示混乱。
    9. 修复常量解析private字段被解析是发生异常问题，[#356](https://github.com/smart-doc-group/smart-doc/issues/356)
    10. RequestMapping中params解析出错,[#374](https://github.com/smart-doc-group/smart-doc/issues/374)
    11. 自循环依赖对象字段显示优化，[#376](https://github.com/smart-doc-group/smart-doc/issues/376)
    12. maven 插件是否能考虑支持appToken，[#354](https://github.com/smart-doc-group/smart-doc/issues/354)
    13. 优化到大量的代码，和2.5.3对比变化非常大。

### 版本号：2.5.3

- 更新日期: 2022-09-10
- 更新内容：
    1. 使用@see注释内容不是枚举类名运行错误，[#340](https://github.com/smart-doc-group/smart-doc/issues/340)
    2. jsr校验注解支持常量替换，[#334](https://github.com/smart-doc-group/smart-doc/issues/334)
    3. 支持推送@since到torna的注释中，[#344](https://github.com/smart-doc-group/smart-doc/issues/344)

### 版本号：2.5.2

- 更新日期: 2022-09-02
- 更新内容：
    1. 修复query参数推送到torna被设置到body中，[#336](https://github.com/smart-doc-group/smart-doc/issues/336)
    2. 请求头参数支持mock值设置，[#337](https://github.com/smart-doc-group/smart-doc/issues/337)
    3. 支持错误码自定义解析器。[#338](https://github.com/smart-doc-group/smart-doc/issues/338)
    4. 修改字段@see 非枚举时错误，[#340](https://github.com/smart-doc-group/smart-doc/issues/340)
    5. 修复openapi表单对象请求参数丢失。

### 版本号：2.5.1

- 更新日期: 2022-08-27
- 更新内容：
    1. 升级qdox，解决字段名使用record报错问题，[#231](https://github.com/smart-doc-group/smart-doc/issues/231)
    2. 优化枚举在文档中的展示[#332](https://github.com/smart-doc-group/smart-doc/issues/332)
    3. 修改date类型文档显示错误
    4. 修复list参数入参样例错误
    5. 修复2.5.0不支持jdk 8的问题
    6. html特殊字符增加转义处理，解决html文档显示问题[#333](https://github.com/smart-doc-group/smart-doc/issues/333)

### 版本号：2.5.0

- 更新日期: 2022-08-20
- 更新内容：
    1. 修复使用@see指向枚举类不存在时空指针问题，改成提示性异常处理，帮助用户可以定位问题，[#331](https://github.com/smart-doc-group/smart-doc/issues/331)
    2. 支持更多jdk 8时间类型[#315](https://github.com/smart-doc-group/smart-doc/issues/315)
    3. 支持jsr验证规则提取[#267](https://github.com/smart-doc-group/smart-doc/issues/267)

### 版本号：2.4.9

- 更新日期: 2022-08-07
- 更新内容：
    1. 支持使用@see指向枚举类，[#306](https://github.com/smart-doc-group/smart-doc/issues/306)
    2. 修复枚举请求的用例展示错误

### 版本号：2.4.8

- 更新日期: 2022-07-09
- 更新内容：
    1. 支持servlet 3.0 文件上传[#294](https://github.com/smart-doc-group/smart-doc/issues/294)
    2. 修复表单复杂对象嵌套Request-example错误问题[#284](https://github.com/smart-doc-group/smart-doc/issues/284)
    3. 修复注释换行导致html文档链接无法点击问题[#290](https://github.com/smart-doc-group/smart-doc/issues/290)
    4. 修复实现接口的枚举作为字段时生成文档报错,[#292](https://github.com/smart-doc-group/smart-doc/issues/292)
    5. 修复Delete Option json请求没有request body的问题[#300](https://github.com/smart-doc-group/smart-doc/issues/300)
    6. 修复OpenAPI导出一级菜单未使用注释名的bug,[#296](https://github.com/smart-doc-group/smart-doc/issues/296)
    7. 新增对JAX-RS @PATCH、@HEAD 的支持[#pr303](https://github.com/smart-doc-group/smart-doc/pull/303)
    8. 自增serverEnv配置用户支持在postman中设置服务器地址变量，[#280](https://github.com/smart-doc-group/smart-doc/issues/280)


### 版本号：2.4.7

- 更新日期: 2022-06-13
- 更新内容：
    1. 修复2.4.6版本使用高版本jdk出现module权限的问题
    2. 修复bug[#283](https://github.com/smart-doc-group/smart-doc/issues/283)


### 版本号：2.4.6

- 更新日期: 2022-05-29
- 更新内容：
    1. 新增枚举字典扫描，[#264](https://github.com/smart-doc-group/smart-doc/issues/264)
    2. BigDecimal类型输出优化，[#268](https://github.com/smart-doc-group/smart-doc/issues/268)
    3. 对不规范的mock注释增加检查，[#262](https://github.com/smart-doc-group/smart-doc/issues/262)

### 版本号：2.4.5

- 更新日期: 2022-05-10
- 更新内容：
    1. 修复部分代码空指针问题，加固健壮性
    2. 修复curl example错误问题，改为方法名[#256](https://github.com/smart-doc-group/smart-doc/issues/256)

#### 版本号：2.4.4

- 更新日期: 2022-05-03
- 更新内容：
    1. 优化对mongodb ObjectId类型的解析[#240](https://github.com/smart-doc-group/smart-doc/issues/240)
    2. 优化OpenAPI生成时operationId值的填充，改为方法名[#235](https://github.com/smart-doc-group/smart-doc/issues/235)
    3. 修复请求参数为Long类型数组时自定义mock提取错误[#244](https://github.com/smart-doc-group/smart-doc/issues/244)
    4. 修复文档说明生成输出多个br标签[#248](https://github.com/smart-doc-group/smart-doc/issues/248)
    5. 修复query param参数显示在Request-body中的问题[#242](https://github.com/smart-doc-group/smart-doc/issues/242)
    6. 修复Controller类上RequestMapping多path包含parameter时的解析错误[#206](https://github.com/smart-doc-group/smart-doc/issues/206)
    7. 修复多文件上传，推送到torna的类型错误[#234](https://github.com/smart-doc-group/smart-doc/issues/234)
    8. 修复分组验证在OpenAPI中不生效问题[#243](https://github.com/smart-doc-group/smart-doc/issues/243)
    9. 修复OpenAPI数据类型设置错误[#253](https://github.com/smart-doc-group/smart-doc/issues/253)
    10. 支持在smart-doc.json配置中对@RequestHeader进行忽略了[#250](https://github.com/smart-doc-group/smart-doc/issues/250)
    11. 修复controller注释html文档导航链接无效的问题[#255](https://github.com/smart-doc-group/smart-doc/issues/255)
    12. 支持内部类枚举私有属性解析。
    13. 移除Spring标记过时的`application/json;charset=UTF-8`，默认改为`application/json`

#### 版本号：2.4.3

- 更新日期: 2022-04-17
- 更新内容：
    1. 支持生成openapi时不生成请求和返回用例[#233](https://github.com/smart-doc-group/smart-doc/issues/233)
    2. 优化对map返回结构的解析[#223](https://github.com/smart-doc-group/smart-doc/issues/223)
    3. 修复生成openapi的时候对contentType的处理转义[#232](https://github.com/smart-doc-group/smart-doc/issues/232)
    4. 修改分组归档处理[#226](https://github.com/smart-doc-group/smart-doc/issues/226)

#### 版本号：2.4.2

- 更新日期: 2022-04-04
- 更新内容：
    1. 修改2.4.1静态常量自动解析不完善的问题。
    2. 新增header自动常量解析。
    3. 修改某些特殊情况下泛型继承解析问题[#215](https://github.com/smart-doc-group/smart-doc/issues/215)
    4. 优化对文档中特殊大于和小于符号的处理。

#### 版本号：2.4.1

- 更新日期: 2022-03-26
- 更新内容：
    1. customResponseFields新增replaceName配置支持替换字段名。
    2. 修复获取非@param注释tag值错误bug。
    3. 支持静态常量自动解析，无需再使用apiConstants来配置。
    4. 修复dubbo rpc

#### 版本号：2.4.0

- 更新日期: 2022-03-06
- 更新内容：
    1. 支持字段多泛型嵌套private B<K,V> body;的解析

#### 版本号：2.3.9

- 更新日期: 2022-02-26
- 更新内容：
    1. 支持推送已过时的接口标记到torna。
    2. rpc mock完善。
    3. dubbo接口文档支持导出字典。
    4. 支持solon，https://gitee.com/noear/solon

#### 版本号：2.3.8

- 更新日期: 2022-02-19
- 更新内容：
    1. 支持多层继承解析。
    2. 优化生成postman post请求文档。

#### 版本号：2.3.7

- 更新日期: 2022-01-17
- 更新内容：
    1. 支持JAX-RS。
    2. 优化对项目文件的加载。
    3. 解决文件数据未获取到是数据错误的bug。

#### 版本号：2.3.6

- 更新日期: 2022-01-02
- 更新内容：
    1. 导出postman时serverUrl不再强制要求设置协议。
    2. 修改设置responseBodyAdvice后推送torna数据错误。
    3. 解决gradle插件不能在kts脚本中添加include和exclude的问题。

#### 版本号：2.3.5

- 更新日期: 2021-12-18
- 更新内容：
    1. 修改author名称多余的双引号。
    2. 修复接口常量被当做字段的问题。
    3. 增强插件对旧版本依赖jar的过滤，防止新版本qdox解析错误。

#### 版本号：2.3.4

- 更新日期: 2021-12-05
- 更新内容：
    1. 修复将@NotEmpty放到字段最前面，后面其他注解不会被分析的bug。
    2. 插件增强对projectName设置。
    3. 增强插件对旧版本依赖jar的过滤，防止新版本qdox解析错误。

#### 版本号：2.3.3

- 更新日期: 2021-11-29
- 更新内容：
    1. 支持OffsetDateTime。
    2. 修复html文档搜索清空后目录显示不全的bug。
    3. 修复非分组使用时html文档目录序号错乱问题。
    4. 修复英文请求头转义问题。

#### 版本号：2.3.2

- 更新日期: 2021-11-21
- 更新内容：
    1. 修复debug调试页搜索后目录锚点错误。
    2. 修复生成openapi时List<String>等基本类型数组入参时类型转换错误。
    3. 优化泛型类型显示，当泛型类型的实参类型是基本类型时直接显示为基本类型。
    4. 新增对@RequestAttribute参数的忽略。
    5. 修复@RequstBody使用基本类型时的请求参数示例错误

#### 版本号：2.3.1

- 更新日期: 2021-11-13
- 更新内容：
    1. 修复debug调试不支持请求头设置中文值的bug。
    2. 修复response自定义tag设置返回未格式化换行问题。
    3. 修复枚举类型字段指定mock不生效的问题。
    4. 新增对@SessionAttribute参数做忽略。
    5. 支持controller实现接口，并使用default方法。

#### 版本号：2.3.0

- 更新日期: 2021-11-07
- 更新内容：
    1. 修复创建html文档丢失search.js文件问题。
    2. 修复packageFilters配置多个包时只有第一个生效的问题。
    3. 修复debug调试页面的curl指令错误。
    4. 修复设置了下载文件接口加download标记后，数据并未生效的bug。
    5. 优化一些部分常见字段的随机值生成。

#### 版本号：2.2.9

- 更新日期: 2021-10-31
- 更新内容：
    1. 修复生成openapi文件上传错误问题。
    2. 修复文件上传接口推送到torna参数被放置到query参数列表的问题。
    3. 修改List<T>类型参数推送到torna错误的问题
    4. 优化outPath配置，只使用torna推送时可以不再要求配置outPath项。

#### 版本号：2.2.8

- 更新日期: 2021-10-07
- 更新内容：
    1. 修复html文档无接口注释时锚点跳转错误的问题。
    2. 修复导出postman时服务端口配置成变量报错的问题。

#### 版本号：2.2.7

- 更新日期: 2021-09-12
- 更新内容：
    1. 修复dubbo文档css连接错误。
    2. 修复分组后组归错误。
    3. 修复路径常量相似度时替换错误的bug。
    4. JSR303分组优化，标记Null的分组字段将不再显示在文档中 。

#### 版本号：2.2.6

- 更新日期: 2021-09-05
- 更新内容：
    1. 修复html文档静态资源链接错误。
    2. 不配置分组时不显示分组。
    3. 修复分组后目录item搜索错误。
    4. 优化maven插件提示 。

#### 版本号：2.2.5

- 更新日期: 2021-08-08
- 更新内容：
    1. 支持在html文档中不显示参数列表。
    2. html文档使用的资源全部改成本地引用。
    3. 修复Boolean类型字段命名为is前缀时，is前缀被去除的bug。
    4. 修复配置了字典码列表后枚举字段注释显示错误[#139](https://github.com/smart-doc-group/smart-doc/issues/139)
    5. 修复分析过程中出现的数组越界异常 。
    6. 新增接口分组支持。

#### 版本号：2.2.4

- 更新日期: 2021-08-08
- 更新内容：
    1. 修复字典码推送torna错误 #https://gitee.com/smart-doc-team/smart-doc/issues/I43JQR。
    2. 新增jsr303 @size和@length支持。
    3. 修改html的模板样式错误。
    4. 修复postman错误#I41G2E 。
    5. 新增isReplace配置 。
    6. 修复当存在多个jsr注解时，部分注解失效问题。

#### 版本号：2.2.3

- 更新日期: 2021-07-18
- 更新内容：
    1. 增加pathPrefix配置项用于配置上下文，引入该配置项后serverUrl仅用于配置服务器地址。
    2. 支持请求头常量设置解析。
    3. 支持使用JsonIgnoreProperties和JSONType注解去忽略多字段。
    4. 修改部分文档设置allInOneDocFileName无效的问题,[#131](https://github.com/smart-doc-group/smart-doc/issues/131)
    5. 修复dubbo rpc文档模板格式错误 #https://gitee.com/smart-doc-team/smart-doc/issues/I40ZGE .
    6. 支持配置添加拦截器中设置全局请求参数，[#132](https://github.com/smart-doc-group/smart-doc/issues/132)
    7. 修复部分类型mock未推送到torna的问题。

#### 版本号：2.2.2

- 更新日期: 2021-07-04
- 更新内容：
    1. 修复url中遇到正则表达解析错误问题.
    2. 修复生成的json样例部分格式化后错误的bug,gitee #I3XSE5。
    3. 增强对文档中html特殊字符的处理，防止html渲染后显示错误，gitee #I3XO31。
    4. 请求头设置增强，支持配置urlPatterns和excludePathPatterns两个属性去匹配对应的接口。
    5. 优化了maven插件的提示，优化后可以将加载了那些目录模块代码路径打印。
    6. 提供了其它框架扩展文档解析的能力。
    7. 修复doc模板错误，gitee #I3Y640。
    8. 修复字典模板错误，[#119](https://github.com/smart-doc-group/smart-doc/issues/119)
    9. 添加忽略HttpServlet对象。
    10. 支持内置替换Jpa Pageable分页对象，去除不必要的请求参数替换配置。
    11. packageFilters增强，可使用正则进行匹配，gitee #I3YKZ4 。
    12. 修复dubbo rpc文档推送到torna数据错误。
    13. 修复customResponseFields和customRequestFields设置时不同类同名字段覆盖bug，gitee #I3Y6AL。
    14. 修复高版本gradle中使用implements添加依赖时，gradle插件无法加载依赖导致返回空json的bug。

#### 版本号：2.2.1

- 更新日期: 2021-06-20
- 更新内容：
    1. 移除代码中System.out.print打印.

#### 版本号：2.2.0

- 更新日期: 2021-06-20
- 更新内容：
    1. 修复参数多行注释时，注释提取错误，gitee #I3TYYP.
    2. 修复部分代码可能出现的空指针问题。
    3. 添加@response tag。支持自己设置response example
    4. 修复推送到torna请求或返回为数组时，示例显示错误
    5. Character类型解析支持。
    6. 修复使用Quartz中JobDataMap类型解析错误。
    7. 移除YapiBuilder。smart-doc不在支持其他第三方接口系统，请使用torna.

#### 版本号：2.1.9

- 更新日期: 2021-05-29
- 更新内容：
    1. 修复inlineEnum为false时枚举展示在参数中的问题。
    2. 返回Spring文件下载对象支持自动识别为文件下载，减少手动标记@download tag。
    3. smart-doc使用的css cdn更换，默认使用国内cdn，提升国内的加载速度，切换英文环境使用google的cdn.
    4. 添加多层泛型嵌套的解析支持。gitee #I3T6UV .
    5. 修复父类是泛型时父类中LocalDateTime类型字段生成json样例错误。
    6. 添加将接口排序order推送到torna中。
    7. 修复类上的@ignore tag不生效bug.
    8. 优化字典码推送，空字典码不会像torna发起推送请求。

#### 版本号：2.1.8

- 更新日期: 2021-05-22
- 更新内容：
    1. 修复推送接口到torna丢失部分mock值的问题。
    2. 修复在参数注释中配置类替换时将非类名解析成类名的bug 。
    3. 支持父类上加@RestController注解的子类能够被识别和扫描
    4. 新增将业务错误码和定义字典推送到torna。
    5. 修复maven插件torna-rest和torna-rpc两个task未加编译前缀的问题。
    6. 修复生成json用例中数组类型json错误的问题。
    7. 修复customRequestFields中设置字段value在用例中不生效的bug。
    8. 添加@JsonProperty支持,支持JsonProperty.Access控制字段。

#### 版本号：2.1.7

- 更新日期: 2021-05-12
- 更新内容：
    1. 添加推送接口作者信息到torna数据错误的bug。
    2. 修复空参数curl命令多余？号问题，github 。

#### 版本号：2.1.6

- 更新日期: 2021-05-10
- 更新内容：
    1. 修复不允许List中放文件上传对象错误的bug。
    2. 添加推送接口作者信息到torna,通过配置author设置推送人，不配置默认为计算机用户名。
    3. 添加推送queryParams参数到torna(需要使用torna 1.6.0+)

#### 版本号：2.1.5

- 更新日期: 2021-05-05
- 更新内容：
    1. 修复requestBodyAdvice请求样例丢之。
    2. 添加dubbo文档到torna的推送。

#### 版本号：2.1.4

- 更新日期: 2021-04-24
- 更新内容：
    1. 修复Controller继承时，父类的Mapping未继承的问题。
    2. 修复配置responseBodyAdvice后，controller中void方法返回显示错误。
    3. 修复往torna推送漏掉pathParams的问题。
    4. 修复非json请求集合中绑定枚举强制检查错误的问题。
    5. 新增requestBodyAdvice支持，可以实现请求参数包装。
    6. 修复泛型为List数据时，类型为object问题。
    7. 修复customFiled为继承参数时配置失效问题。

#### 版本号：2.1.3

- 更新日期: 2021-04-11
- 更新内容：
    1. 增强对文件上传的支持。
    2. 增加customRequestFields配置项，[#97](https://github.com/smart-doc-group/smart-doc/issues/97)
    3. 修复往torna推送漏掉pathParams的问题。
    4. 修改debug测试页面，支持post表单请求
    5. 修改表单请求对象中枚举字段默认值错误的bug

#### 版本号：2.1.2

- 更新日期: 2021-03-29
- 更新内容：
    1. 修复Map嵌套在某些结构体中栈溢出问题，gitee #I3CCLY。
    2. 修复Torna数据推送问题。

#### 版本号：2.1.1

- 更新日期: 2021-03-24
- 更新内容：
    1. 修复Map嵌套在某些结构体中栈溢出问题，gitee #I3CCLY。
    2. 修复Torna数据推送问题。

#### 版本号：2.1.0

- 更新日期: 2021-03-21
- 更新内容：
    1. 导出的postman的url资源下添加缺失的protocol。
    2. 增加@ignoreParams自定义tag来过滤掉不想显示在文档中参数。
    3. 增加了自动生成版本记录的功能。
    4. 修改torna推送的bug。
    5. 支持旧的SpringMVC项目的url后缀，新项目不建议加什么破玩意后缀。

#### 版本号：2.0.9

- 更新日期: 2021-03-12
- 更新内容：
    1. 支持UUID和ZonedDateTime字段类型，[#89](https://github.com/smart-doc-group/smart-doc/issues/89)
    2. 对map参数增加开关来兼容旧项目，还是不建议使用map参数。
    3. 完成和Torna的对接。

#### 版本号：2.0.8

- 更新日期: 2021-02-26
- 更新内容：
    1. 修复文件上传的参数丢失的注释。
    2. 修复2.0.7新增忽略接口方法后解析父类字段缺失注释bug。
    3. 修改byte类型的转换，将过去的string转为int8。

#### 版本号：2.0.7

- 更新日期: 2021-01-30
- 更新内容：
    1. 修复postman的url中不附加的context-path的问题。
    2. 修复带正则的path路径参数解析出现截取越界的问题。
    3. 添加对默认接口实现中get方法重写忽略的能力解析。
    4. 修改数组、map等字段类型的自定义mock值显示错误问题。
    5. 修复对mapping中headers的处理。

#### 版本号：2.0.6

- 更新日期: 2021-01-15
- 更新内容：
    1. 修复带正则的path路径参数在postman中用例问题。
    2. 增强对祖传不良代码的分析兼容。

#### 版本号：2.0.5

- 更新日期: 2021-01-09
- 更新内容：
    1. 修复集合类无泛型参数作为入参出参时的数组越界。
    2. 修复新开tab访问的url拼接问题。

#### 版本号：2.0.3-2.0.4

- 更新日期: 2021-01-01
- 更新内容：
    1. 修改页面的错误列表标题显示。
    2. 修改debug页面curl header语法错误。
    3. 修改debug页面json参数输入框，允许粘贴小段文本。
    4. 解决使用dubbo 2.7+，在provider中生成文档出错问题[#77](https://github.com/smart-doc-group/smart-doc/issues/77)

#### 版本号：2.0.2

- 更新日期: 2020-12-27
- 更新内容：
    1. 修改创建openapi时的空指针异常。
    2. 修改debug页面时未使用mock值的问题。
    3. debug页面可以根据请求动态更新curl命令。
    4. 优化debug页面中的文件下载测试。
    5. 优化enum入参mock错误的bug。
    6. mock页面支持使用新窗口打开后端渲染的页面。
    7. 修改生成一些字段值生成错误的bug。
    8. 修改类中使用集合字段未指定泛型可能出错的bug。
    9. 优化set等集合类在文档中的类型显示。
    10. 添加对集合字段中枚举的处理。
    11. 枚举序列化支持优化。
    12. 调试页面新增Highlight支持。

#### 版本号：2.0.1

- 更新日期: 2020-12-20
- 更新内容：
    1. debug调试页面支持文件上传。
    2. 修改简单请求参数mock值和类型不匹配问题。
    3. debug页面完全支持文件下载测试。
    4. 所有html的文档支持接口目录搜索。
    5. 剔除flexmark依赖，旧的非allInOne模板删除，统一h5文档样式。

#### 版本号：2.0.0

- 更新日期: 2020-12-13
- 更新内容：
    1. 优化了文档的显示，将query和path单独提出来做了展示
    2. 优化openapi 3.0文档规范的支持，可集成swagger ui等ui使用。
    3. 优化postman collection 2.0的支持。
    4. 添加分组支持group。
    5. 修改mock的一些bug和增强使用
    6. 支出创建debug页面

#### 版本号：1.9.9.1

- 更新日期: 2020-11-23
- 更新内容：
    1. 这是一个紧急修改版本。
    2. 解决1.9.9版本controller中存在非路径映射方法时的错误。

#### 版本号：1.9.9

- 更新日期: 2020-11-23
- 更新内容：
    1. 修改1.9.8启用严格检查注释模式下的bug。
    2. 修改使用泛型数组参数时解析错误。
    3. 修复ResponseEntity中的数组解析错误。
    4. 修复controller方法标注ignore后文档序号错误。
    5. 增加对@RequestMapping注解的path属性的解析支持
    6. 修复postman中formdata表单不显示描述信息的问题
    7. html5 allInOne模板支持代码高亮。

#### 版本号：1.9.8

- 更新日期: 2020-11-10
- 更新内容：
    1. 忽略Class对象的解析。
    2. 增加对抽象Controller方法的解析。
    3. 修改阿里版本dubbo注解名称解析错误 。
    4. 修改模拟值生成错误。
    5. 支持ResponseBodyAdvice通用接口响应包装设置。
    6. 修复类同时继承和基类和实现接口中可能出现字段重复的bug。

#### 版本号：1.9.7

- 更新日期: 2020-10-24
- 更新内容：
    1. 修复restful接口泛型中使用?时的解析错误。
    2. 优化rpc html非all in one的问题。
    3. 对rest query参数自动添加描述，增加可读性。
    4. support ali dubbo,#I22CF7 .
    5. support @RequestMapping headers.

#### 版本号：1.9.6

- 更新日期: 2020-10-09
- 更新内容：
    1. 修复RequestParam 解析错误。
    2. 修复泛型中使用?时的解析错误。
    3. 修改服务url的地址为空字符串，不再提供默认http前缀
    4. 增加泛型实际类型的显示开关控制。
    5. 修复类继承一个泛型类时的解析错误。
    6. 优化smart-doc maven插件，提升用户在多模块下的使用体验。

#### 版本号：1.9.5

- 更新日期: 2020-09-19
- 更新内容：
    1. 接口参数无注解时将required设置为false。
    2. 修改html自适应。

#### 版本号：1.9.4

- 更新日期: 2020-09-06
- 更新内容：
    1. 添加order tag支持对api做排序。
    2. 优化一些重复的代码。
    3. 修改基础url中使用常量出现空格的问题。
    4. 添加生成yapi文件的功能。

#### 版本号：1.9.3

- 更新日期: 2020-08-30
- 更新内容：
    1. 修复Get请求用例参数值被去空格问题。
    2. 修改复杂参数表树型数据转化的错误。
    3. 修复非allInOne模板使用渲染错误。
    4. 修复一些泛型例子解析错误bug。
    5. 优化MultipartFile文件上传参数处理，不对该参数进行展开分析。

###https://github.com/smart-doc-group/smart-doc/issues/ 版本号：1.9.2

- 更新日期: 2020-08-23
- 更新内容：
    1. 修改前面版本修改引发的普通jsr 303验证解析错误问题。
    2. 新增忽略请求参数对象的配置gitee #I1RBJO。
    3. 修改smart-doc的beetl配置避免和用户的业务中beetl配置冲突。
    4. 新增ApiDataBuilder中获取树形格式参数数据的接口[#40](https://github.com/smart-doc-group/smart-doc/issues/40)
    5. 新增对Open Api 3.0的支持。
    6. 修改字典表空时内部发生空指针的问题。
    7. 优化curl用例，增加请求头。

#### 版本号：1.9.1

- 更新日期: 2020-08-02
- 更新内容：
    1. 修改进去版本更新导致的泛型解析问题。
    2. 修改1.8.9版本修改后带来的dubbo接口文档显示问题
    3. 修改smart-doc-maven-plugin生成dubbo文档时缺乏配置文件错误问题。
    4. 修改gradle插件的对多模块的支持。

#### 版本号：1.9.0

- 更新日期: 2020-07-19
- 更新内容：
    1. 修改dubbo html依赖部分错乱问题。
    2. 新增自定义输出文件名称的配置。
    3. 添加请求和响应示例的开关配置项。
    4. 修改使用JSR303参数校验时，默认分组验证被忽略问题。
    5. 修改jackson JsonIgnore注解在参数对象中不生效的问题。

#### 版本号：1.8.9

- 更新日期: 2020-07-05
- 更新内容：
    1. 修改[#38](https://github.com/smart-doc-group/smart-doc/issues/38)
    2. 修改gitee #I1LBKO。
    3. 修改fix #39多泛型解析顺序问题。
    4. 优化支持gitee #I1IQKY常量解析需求

#### 版本号：1.8.8

- 更新日期: 2020-06-21
- 更新内容：
    1. 修改忽略对LinkedHashMap的解析，gitee #I1JI5W。
    2. 修改接口或和实现类合并分析是字段重复问题，gitee #I1JHMW。
    3. 优化接口方法字段不能获取docletTag的问题。
    4. 优化枚举参数展示，支持自定义控制显示。
    5. 添加Feign的支持。
    6. 优化递归执行，对外提供递归次数限制。

#### 版本号：1.8.7

- 更新日期: 2020-06-01
- 更新内容：
    1. 增加对java接口的分析，例如Jpa的分页Page类。
    2. 增强对使用@RequestBody绑定参数方法的解析。
    3. 增加dubbo rpc文档生成支持。
    4. 增加将驼峰字段格式转化为下划线格式。
    5. maven插件和gradle插件提供includes支持，方便自行配置加载第三方库。
    6. fix https://github.com/smart-doc-group/smart-doc/issues/32.
    7. 增加文档接口根据接口标题排序功能。

#### 版本号：1.8.6

- 更新日期: 2020-05-09
- 更新内容：
    1. 增加localTime支持[gitee #I1F7CW](https://gitee.com/sunyurepository/smart-doc/issues/I1F7CW)。
    2. 优化smart-doc导入Postman
       collection时的header问题[gitee #I1EX42](https://gitee.com/sunyurepository/smart-doc/issues/I1EX42)
    3. 优化smart-doc-maven-plugin加载source的过滤，支持使用通配符来过滤。
    4. 首次发布gradle插件，发布smart-doc-gradle-plugin插件，
    5. 修复通用泛型解析出错[git #28](https://github.com/smart-doc-group/smart-doc/issues/28)。

#### 版本号：1.8.5

- 更新日期: 2020-04-19
- 更新内容：
    1. maven插件错误码列表导出bug[git #I1EHXA](https://gitee.com/sunyurepository/smart-doc/issues/I1EHXA)
    2. 增加@PatchMapping支持[gitee #I1EDRF](https://gitee.com/sunyurepository/smart-doc/issues/I1EDRF)
    3. 解决javadoc包含重复tag生成文档报错[gitee #I1ENNM](https://gitee.com/sunyurepository/smart-doc/issues/I1ENNM)
    4. 修改当请求参数为泛型时数据解析错误问题。
    5. 修复分组验证空指针问题，不对返回对象做分组验证处理。
    6. 优化smart-doc-maven-plugin对多级maven项目的加载。
    7. 支持请求参数对象替换成另外的对象来渲染文档

#### 版本号：1.8.4

- 更新日期: 2020-03-30
- 更新内容：
    1. Controller新增时候@ignore
       tag,可适应该tag忽略不需要生成文档的controller[git #24](https://github.com/smart-doc-group/smart-doc/issues/24)
    2. 参数中包含 HttpSession时smart-doc卡主，[gitee #I1CA9M](https://gitee.com/sunyurepository/smart-doc/issues/I1CA9M)
    3. 解决一些复杂分组场景smart-doc报错的问题[gitee #I1CPSM](https://gitee.com/sunyurepository/smart-doc/issues/I1CPSM)
    4. 解决smart-doc-maven-plugin插件读取配置乱码问题。

#### 版本号：1.8.3

- 更新日期: 2020-03-21
- 更新内容：
    1. 增加从接口方法getter或者setter方法中读取注释。
    2. 修改smart-doc默认编码为utf-8，解决生成文档乱码问题。
    3. 增加对代码中@author tag的支持，支持多作者。

#### 版本号：1.8.2

- 更新日期: 2020-03-13
- 更新内容：
    1. 修改gitee #I19IYW 。
    2. 修改文档模板中的title设置错误。
    3. 修改gitee #I191EO
    4. 支持@Validated 分组

#### 版本号：1.8.1

- 更新日期: 2020-01-22
- 更新内容：
    1. 增加对接口get方法的分析。
    2. 增加对第三方jar中list泛型数据的解析。
    3. 删除原来冗长的SourceBuilder代码。
    4. 修改AdocDocBuilder、HtmlApiDocBuilder、ApiDocBuilder的方法名规范化，单元测试的升级需要做小部分变更。
    5. 修改1.8.0重构后的请求示例将header放入普通参数的bug。
    6. 修改参数加上@Validated注解后，文档里没有该参数信息的bug。
    7. 新增@Deprecated标注接口的支持(使用line through完成样式标记)

#### 版本号：1.8.0

- 更新日期: 2020-01-01
- 更新内容：
    1. 修改参数上多个验证注解不支持的问题。
    2. 修改支持上传文件参数不列举到文档的问题。
    3. 新增ApiDataBuilder用于获取smart-doc生成的文档数据，包含header、字典、错误码等。
    4. 合并fork分支的github book html5模板，新增搜索和锚点。
    5. 新增自定义@mock tag用于指定生成文档的字段值，@param 的参数注释增加mock值的功能(@param name 姓名|张三)
    6. 重点：smart-doc的maven插件smart-doc-maven-plugin增强对maven标准项目的支持。
    7. 全面支持spring的表单参数绑定解析。
    8. postman json生成支持所有参数自动回填。再也不用自己建参数了。
    9. 优化对实体类中枚举字段的支持。
    10. 增加对实体中静态常量常量字段的过滤。

#### 版本号：1.7.9

- 更新日期: 2019-12-16
- 更新内容：
    1. 修改request请求参数中嵌套对象不能解析的bug，参考gitee #I16AN2.
    2. controller参数是数组时添加@PathVariable注解会报空指针,参考gitee #I16F6G
    3. 新增ApiDataBuilder用于获取smart-doc生成的文档数据，包含header、字典、错误码等。
    4. 修改github https://github.com/smart-doc-group/smart-doc/issues/9 文档错误bug.
    5. 新增接口的@author展示，方法从文档中查到找到接口负责人，生成文档可以选择关闭显示。
    6. 重点：smart-doc的maven插件smart-doc-maven-plugin 1.0.0版本发布。

#### 版本号：1.7.8

- 更新日期: 2019-12-02
- 更新内容：
    1. 修改Spring Controller使用非Spring Web注解时生成的响应示例出错的bug。
    2. 修改使用mybatis-plus实体继承Model对象时将log字段输出到文档的问题。
    3. 添加对transient修饰字段文档输出开关，默认不输出。
    4. html文档添加项目名称显示
    5. 修改[#github 4](https://github.com/smart-doc-group/smart-doc/issues/4) 泛型中Void类型解析死循环
    6. 修改[#github 5](https://github.com/smart-doc-group/smart-doc/issues/5) 简单枚举参数解析空指针异常
    7. 添加导出PostMan json数据

#### 版本号：1.7.7

- 更新日期：2019-11-18
- 更新内容：
    1. 修改timestamp类型字段创建json示例错误bug。
    2. fix #I1545A 单接口多路径bug。
    3. 修改部分url生成部署空格问题。
    4. 优化对java.util.concurrent.ConcurrentMap的解析。

#### 版本号：1.7.6

- 更新日期：2019-11-13
- 更新内容：
    1. fix #I14PT5 header重复渲染到文档
    2. fix #I14MV7 不设置dataDictionaries出现空指针错误
    3. 增加请求参数枚举字段解析(试用功能)

#### 版本号：1.7.5

- 更新日期：2019-11-06
- 更新内容：
    1. 优化文档中错误列表的标题，可根据语言环境变化显示中文或因为。
    2. 解决项目外jar中内部类生成文档错误的bug。
    3. 支持环形依赖分析。只要你敢写！
    4. 修改使用SpringMvc或者SpringBoot上传时接口的Content-Type显示错误。
    5. 支持设置项目作为markdown的一级标题。
    6. 修改方法注释相同引起的html链接跳转错误。
    7. 添加生成AllInOne的覆盖配置项，默认自动加版本号不覆盖。
    8. 新增枚举字典码导出到文档的功能。

#### 版本号：1.7.4

- 更新日期：2019-10-29
- 更新内容：
    1. 修改gitee上bug #I1426C。
    2. 修改gitee上bug #I13ZAL,1.7.0~1.7.3 结构优化后产生的bug，建议用户升级。
    3. 修改gitee上bug #I13U4C。
    4. 修改设置中文语言环境(默认中文)下错误码列表title显示英文的问题。
    5. 优化AllInOne的markdown展示，生成时带上自动产生的序号。

#### 版本号：1.7.3

- 更新日期：2019-10-24
- 更新内容：
    1. 优化html5模板左侧文档目录展示，能够展开和收缩。
    2. 修改gitee上bug #I13R3K。
    3. 修改gitee上bug #I13NR1。
    4. 开放的文档数据获取接口添加返回方法的唯一id和方法名称，方便一些企业自己做对接。

#### 版本号：1.7.2

- 更新日期：2019-10-19
- 更新内容：
    1. 优化注释换行\n\r问题，依赖common-util 1.8.7。
    2. 修改gitee上bug #I135PG、#I13NR1。
    3. 添加@requestHeader注解的支持，文档自定将参数绑定到请求头列表中。
    4. 增加javadoc apiNote tag的支持。
    5. 解决扫描分析controller中private方法的问题。
    6. 添加支持@RequestParam注解重写参数名和设置默认值的文档解析。
    7. 支持使用@PostMapping和@PutMapping请求自定义注解接收单个json参数场景下生成json请求实例。
    8. 新增对Spring ResponseEntity的解析。
    9. 增加内部类返回结构解析。
    10. 修改文档中显示的字段类型，float、double等由原来的number直接变成具体类型。

#### 版本号：1.7.1

- 更新日期：已废弃
- 更新内容：
    1. 优化注释换行\n\r问题。
    2. 修改bug #I135PG
    3. 添加requestHeader功能

#### 版本号：1.7.0

- 更新日期：2019-09-30
- 更新内容：
    1. 优化代码。
    2. 添加生成HTML5和Asciidoctor文档的功能。
    3. 增加开放API数据接口功能。
    4. 支持Callable,Future,CompletableFuture等异步接口返回的推导。
    5. 支持Spring Boot Web Flux(Controller方式书写)。

#### 版本号：1.6.4

- 更新日期：2019-09-23
- 更新内容：
    1. 优化代码
    2. 增加对普通的get请求参数拼装示例的生成
    3. 增加spring mvc占位符restful url请求示例生成

#### 版本号：1.6.2

- 更新日期：2019-09-13
- 更新内容：
    1. 修改字段注释多行显示错误bug
    2. 字段描述文档增加@Since tag的支持
    3. 解析代码忽略WebRequest类防止生产过多信息
    4. 升级基础库依赖版本

#### 版本号：1.3

- 更新日期：2018-09-15
- 更新内容：
    1. 增加PutMapping和DeleteMapping支持
    2. 添加字符串date和Date类型时间的模拟值生成

#### 版本号：1.2

- 更新日期：2018-09-04
- 更新内容：
    1. 根据用户反馈增加controller报名过滤功能，该功能为可选项

#### 版本号：1.1

- 更新日期：2018-08-30
- 更新内容：
    1. 修改PostMapping和GetMapping value为空报错的bug
    2. 增强时间字段的mock数据创建
    3. 修改smart-doc解析自引用对象出错的bug

#### 版本号：1.0

- 更新日期：2018-08-25
- 更新内容：
    1. smart-doc增加将所有文档导出归档到一个markdown中件的功能
    2. 参考阿里开发手册将直接提升到1.0，之前的版本主要是个人内部测试

#### 版本号：0.5

- 更新日期：2018-08-23
- 更新内容：
    1. 将api-doc重命名为smart-doc并发布到中央仓库

#### 版本号：0.4

- 更新日期：2018-07-11
- 更新内容：
    1. 修改api-doc对类继承属性的支持。

#### 版本号：0.3

- 更新日期：2018-07-10
- 更新内容：
    1. api-doc增加对jackson和fastjson注解的支持，可根据注解定义来生成返回信息。

### 版本号：0.2

- 更新日期：2018-07-07
- 更新内容：
    1. 修改api-doc泛型推导的bug.

### 版本号：0.1

- 更新日期：2018-06-25
- 更新内容：
    1. 手册将api-doc发布到中央仓库