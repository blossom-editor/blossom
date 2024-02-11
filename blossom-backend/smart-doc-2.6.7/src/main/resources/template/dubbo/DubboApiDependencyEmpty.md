# Add dependency

```
<dependency>
    <groupId>Your api group</groupId>
    <artifactId>Your api artifactId</artifactId>
    <version>1.0.0</version>
</dependency>
```

Consumer config

```
dubbo:
  registry:
    protocol: zookeeper
    address:  localhost:2181
```