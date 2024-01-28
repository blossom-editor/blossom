# 应用部署文档

本文说明了所有部署该应用的方式。

```
docker
 ├─ build                    镜像构建
 |  └─ Dockerfile            应用镜像构建脚本
 └─ compose                  docker compose 配置
    └─ blossom-mysql8.yaml   包含后台应用 Blossom-backend 与 MySql8
```

应用所需环境与版本

1. `JDK8`
2. `MySQL8+`

# 一、使用应用的公共镜像

## 1. 拉取MySQL镜像并启动

> 如果已安装数据库，可以跳过该步骤。

```bash
docker pull mysql:8.0.31
```

启动镜像示例

```bash
docker run \
-d \
--name mysql \
-e MYSQL_ROOT_PASSWORD=jasmine888 \
-p 3306:3306 \
-v /usr/local/docker/mysql/data:/var/lib/mysql \
-v /usr/local/docker/mysql-files/log:/var/lib/mysql-files \
-v /usr/local/docker/mysql/log:/var/log/mysql \
mysql:8.0.31
```

## 2. 创建数据库

你需要在 MySQL 中先创建一个数据库，数据库名称与启动容器时的`--spring.datasource.url`配置的数据库名称相同，如果不需要自定义数据库名称，你可以直接使用如下语句创建数据库：

```sql
CREATE DATABASE `blossom` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
```

## 3. 拉取镜像

```
docker pull jasminexzzz/blossom:latest
```

## 4. 启动容器

启动示例

```bash
docker run -d \
  # 容器名称
  --name blossom-backend \
  # 指定端口映射
  -p 9999:9999 \
  # 挂载图片保存路径，如果是windows环境，可以使用/c/home/bl/来指定磁盘
  -v /home/bl/:/home/bl/ \
  # 启动的镜像名称
  jasminexzzz/blossom:latest \
  # 配置数据库访问地址
  --spring.datasource.url="jdbc:mysql://192.168.31.99:3306/blossom?useUnicode=true&characterEncoding=utf-8&allowPublicKeyRetrieval=true&allowMultiQueries=true&useSSL=false&&serverTimezone=GMT%2B8" \
  # 配置数据库用户名
  --spring.datasource.username=root \
  # 配置数据库密码
  --spring.datasource.password=jasmine888
```

windows 控制台如下

```bash
docker run -d --name blossom-backend -p 9999:9999 -v /home/bl/:/home/bl/ jasminexzzz/blossom:latest --spring.profiles.active=prod --project.iaas.blos.domain="http://127.0.0.1:9999/pic/" --project.iaas.blos.default-path="/home/bl/img/" --spring.datasource.url="jdbc:mysql://192.168.31.99:3306/blossom?useUnicode=true&characterEncoding=utf-8&allowPublicKeyRetrieval=true&allowMultiQueries=true&useSSL=false&&serverTimezone=GMT%2B8" --spring.datasource.username=root --spring.datasource.password=jasmine888
```

# 二、使用 docker compose 拉取镜像

可以使用 docker compose 单独拉取应用镜像，或者连同 MySQL 一起拉取构建。下列示例均可在项目`/docker/compose`目录下查看

## 1. 拉取应用镜像与 MySQL 镜像示例

该 docker compose 包含 MySQL，MySQL 容器在初始化时会自动创建数据库 Blossom，但你需要挂载 MySQL 文件到宿主机，防止数据丢失。

```yml
version: "3.8"

networks:
  blossomnet:
    driver:
      bridge

services:
  blossom:
    image: jasminexzzz/blossom:latest
    container_name: blossom-backend
    volumes:
      # 【需修改】挂载图片保存路径，如果是windows环境，可以使用/c/home/bl/img/来指定磁盘
      - /d/blossom/bl/:/home/bl/
    environment:
      # 配置数据库访问地址
      SPRING_DATASOURCE_URL: jdbc:mysql://blmysql:3306/blossom?useUnicode=true&characterEncoding=utf-8&allowPublicKeyRetrieval=true&allowMultiQueries=true&useSSL=false&&serverTimezone=GMT%2B8
      # 【需修改】配置数据库用户名
      SPRING_DATASOURCE_USERNAME: root
      # 【需修改】配置数据库密码
      SPRING_DATASOURCE_PASSWORD: jasmine888
    ports:
      - "9999:9999"
    networks:
      - blossomnet
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9999/sys/alive"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 5s
    restart: always
    depends_on:
      blmysql:
        condition: service_healthy
  blmysql:
    image: mysql:8.0.31
    container_name: blossom-mysql
    restart: on-failure:3
    # 【需修改】注意挂载路径
    volumes:
      - /d/blossom/Docker/mysql/data:/var/lib/mysql
      - /d/blossom/Docker/mysql/log:/var/log/mysql
      - /d/blossom/Docker/mysql/mysql-files/log:/var/lib/mysql-files
    environment:
      MYSQL_DATABASE: blossom
      # 【需修改】多数情况下与 services.blossom.environment.SPRING_DATASOURCE_PASSWORD 相同
      MYSQL_ROOT_PASSWORD: jasmine888
      LANG: C.UTF-8
      TZ: Asia/Shanghai
    ports:
      - "3306:3306"
    networks:
      - blossomnet
    healthcheck:
      test: ["CMD", "mysqladmin", "-uroot", "-pjasmine888", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 3s
      retries: 12

```

启动 Docker Compose

```
docker compose -f docker/compose/blossom-mysql8.yaml up -d
```
























