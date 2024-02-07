# Add dependency

```
<%
for(dependency in dependencyList){
%>
<dependency>
    <groupId>${dependency.groupId}</groupId>
    <artifactId>${dependency.artifactId}</artifactId>
    <version>${dependency.version}</version>
</dependency>

<%}%>
```

<%if(isNotEmpty(consumerConfigExample)){%>
Consumer config

```
${consumerConfigExample}
```

<%}%>