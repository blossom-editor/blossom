# ${htmlEscape(desc)}

**URI:** ${uri}

**Service:** ${name}

**Protocol:** ${protocol}

**Author:** ${author}

**Version:** ${version}
<%
for(doc in list){
%>
<%if(doc.deprecated){%>

## ~~${htmlEscape(doc.desc)}~~

<%}else{%>

## ${htmlEscape(doc.desc)}

<%}%>

**Definition：** ${doc.methodDefinition}

<%if(isNotEmpty(doc.author)){%>
**Author:** ${doc.author}
<%}%>

**Description:** ${doc.detail}

<%if(isNotEmpty(doc.requestParams)){%>
**Invoke-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
<%
for(param in doc.requestParams){
%>
|${param.field}|${param.type}|${param.required}|${htmlEscape(param.desc)}|${param.version}
<%}%>
<%}%>

<%if(isNotEmpty(doc.responseParams)){%>
**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
<%
for(param in doc.responseParams){
%>
|${param.field}|${param.type}|${htmlEscape(param.desc)}|${param.version}
<%}%>
<%}%>

<% } %>



