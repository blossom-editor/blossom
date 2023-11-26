# Blossom-backend

Blossom 笔记软件的后台服务

[Blossom 后台部署文档](https://www.wangyunf.com/blossom-doc/guide/deploy/backend.html)

# 模块说明

```
root
 ├─ backend           后台应用，当不需要多应用部署时，默认使用该应用对外提供所有服务，该应用只写 Controller 层。
 ├─ common            项目公共模块
 |  ├─ common-base    基础对象（如公共响应 R.class），工具类，异常捕获，动态日志级别。
 |  ├─ common-cache   缓存封装，Redis 封装，Caffeine 封装。
 |  ├─ common-db      数据库模块，mybatis plus, 慢SQL监控。
 |  └─ common-iaas    云厂商功能
 | 
 ├─ expand-sentinel   对 Alibaba Sentinel 的封装，拓展了一些功能，实现了一些本地流量查询接口。
 ├─ expand-tracker    自研的链路追踪核心模块, 源自 tracker-core, 仅提供本地日志记录和日志中插入 Trace 的功能。
 └─ script            数据库与启动脚本
```

# 编译镜像

### Linux编译

安装好`Docker`环境，安装好`git`，克隆代码仓库，

在项目根目录下，运行如下命令：

```shell
maven install
maven clean build
```

在 `blossom-backend` 目录下，运行如下命令：

```shell
maven package
```

```shell
docker build -t jasminexzzz/blossom:dev -f Dockerfile .
```

等待编译完成后，
通过 `docker images` 可以查看到存在镜像 `jasminexzzz/blossom:dev`

### 本地开发

这里使用的是 `IntelliJ IDEA`，
如果开发机器是Windows，则需要安装好 `Docker for Windows`
如果开发机器是其它的，需要安装好`Docker`环境

1. 编辑配置
2. 添加Dockerfile运行配置
3. 配置如下参数
   - 服务器目录：本地的`Docker for Windows`
   - Dockerfile: blossom-backend\Dockerfile
   - 镜像标记：jasminexzzz/blossom:dev
4. 添加执行前操作: 添加 运行Maven目标，配置命令 `clean package`，确认
5. 确定保存

### 运行镜像的参数参考

```shell
docker run -d \
  --name blossom-dev \
  -p 9999:9999 \
  -v ~/blossom:/home/bl \
  jasminexzzz/blossom:dev \
  --spring.profiles.active=prod \
  --project.iaas.blos.domain="http://192.168.2.222:9999/pic/" \
  --spring.datasource.url="jdbc:mysql://192.168.2.222:3306/blossom?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false&&serverTimezone=GMT%2B8" \
  --spring.datasource.username=root \
  --spring.datasource.password=123456 
```