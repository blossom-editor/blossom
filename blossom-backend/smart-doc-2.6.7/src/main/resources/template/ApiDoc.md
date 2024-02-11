
# ${desc}
<%
for(doc in list){
%>
<%if(doc.deprecated){%>
## ~~${htmlEscape(doc.desc)}~~
<%}else{%>
## ${htmlEscape(doc.desc)}
<%}%>

**URL:** `${doc.url}`

**Type:** `${doc.type}`

<%if(isNotEmpty(doc.author)){%>
**Author:** ${doc.author}
<%}%>

**Content-Type:** `${doc.contentType}`

**Description:** ${doc.detail}

<%if(isNotEmpty(doc.headers)){%>
**Request-headers:**

| Header | Type | Required | Description | Since |
|--------|------|----------|-------------|-------|
${doc.headers}
<%}%>

<%if(isNotEmpty(doc.pathParams)){%>
**Path-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
<%
for(param in doc.pathParams){
%>
|${param.field}|${param.type}|${param.required}|${lineBreaksToBr(param.desc)}|${param.version}|
<%}%>
<%}%>

<%if(isNotEmpty(doc.queryParams)){%>
**Query-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
<%
for(param in doc.queryParams){
%>
|${param.field}|${param.type}|${param.required}|${lineBreaksToBr(param.desc)}|${param.version}|
<%}%>
<%}%>

<%if(isNotEmpty(doc.requestParams)){%>
**Body-parameters:**

| Parameter | Type | Required | Description | Since |
|-----------|------|----------|-------------|-------|
<%
for(param in doc.requestParams){
%>
|${param.field}|${param.type}|${param.required}|${lineBreaksToBr(param.desc)}|${param.version}|
<%}%>
<%}%>

<%if(isNotEmpty(doc.requestUsage)&&isRequestExample){%>
**Request-example:**
```
${doc.requestUsage}
```
<%}%>
<%if(isNotEmpty(doc.responseParams)){%>

**Response-fields:**

| Field | Type | Description | Since |
|-------|------|-------------|-------|
<%
for(param in doc.responseParams){
%>
|${param.field}|${param.type}|${lineBreaksToBr(param.desc)}|${param.version}|
<%}%>
<%}%>

<%if(isNotEmpty(doc.responseUsage)&&isResponseExample){%>
**Response-example:**
```
${doc.responseUsage}
```
<%}%>

<% } %>
