# Blossom-backend

Blossom 笔记软件的后台服务

[博客后台的使用文档](https://www.wangyunf.com/blossom-doc/doc/backend)

# 模块说明

```
root
 ├─ aodc              脚本
 ├─ backend           后台应用，当不需要多应用部署时，默认使用该应用对外提供所有服务，该应用只写 Controller 层。
 ├─ common            项目公共模块
 |  ├─ common-base    基础对象（如公共响应 R.class），工具类，异常捕获，动态日志级别。
 |  ├─ common-cache   缓存封装，Redis 封装，Caffeine 封装。
 |  ├─ common-db      数据库模块，mybatis plus, 慢SQL监控。
 |  ├─ common-iaas    云厂商功能
 | 
 ├─ expand-sentinel   对 Alibaba Sentinel 的封装，拓展了一些功能，实现了一些本地流量查询接口。
 └─ expand-tracker    自研的链路追踪核心模块, 源自 tracker-core, 仅提供本地日志记录和日志中插入 Trace 的功能。
```
