# 一、项目说明

Blossom 笔记软件的后台服务

# 二、模块说明

```
root
 ├─ aodc              相关文档
 ├─ backend           后台应用，当不需要多应用部署时，默认使用该应用对外提供所有服务，该应用只写 Controller 层。
 ├─ common            项目公共模块
 |  ├─ common-base    基础对象（如公共响应 R.class），工具类，异常捕获，动态日志级别，Caffeine 等。
 |  ├─ common-cache   缓存封装，Redis 封装，Redis Cache 封装。
 |  ├─ common-db      数据库模块，mybatis plus, 慢SQL监控。
 |  ├─ common-iaas    云厂商功能
 | 
 ├─ expand-sentinel   对 Alibaba Sentinel 的封装，拓展了一些功能，实现了一些本地流量查询接口。
 ├─ expand-tracker    自研的链路追踪核心模块, 源自 tracker-core, 仅提供本地日志记录和日志中插入 Trace 的功能。
```

---

# 三、使用说明

## 3.1 系统登录

登录请访问`/<context-path>/login`

请求体:
```json
{
  // 授权客户端的ID, 见配置文件 prohect.auth.clients.client-id
  "clientId": "blossom",
  // 授权客户端的登录方式, 见配置文件 prohect.auth.clients.grant-type
  "grantType":"password",
  "username": "blos",
  "password": "blos"
}
```
登录返回的 token 信息需要放在请求头的 `Authorization` 中, 并以 `Bearer `开头, 例如

```
Authorization:Bearer ac27fd57303d4bddb0229c3b6d71b611
```

> token 的用法遵循 Bearer Token 规范, 可见：https://learning.postman.com/docs/sending-requests/authorization/#bearer-token