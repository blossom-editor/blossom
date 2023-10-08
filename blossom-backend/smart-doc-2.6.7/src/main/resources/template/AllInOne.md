<%if(isNotEmpty(projectName)){%>
# ${projectName}
<%}%>
<%if(isNotEmpty(revisionLogList)){%>

| Version | Update Time | Status | Author | Description |
|---------|-------------|--------|--------|-------------|
<%
for(revisionLog in revisionLogList){
%>
|${revisionLog.version}|${revisionLog.revisionTime}|${revisionLog.status}|${revisionLog.author}|${revisionLog.remarks}|
<%}%>

<%}%>


<%
for(apiGroup in apiDocList){
%>
<%
if(!apiDocListOnlyHasDefaultGroup) {%>
# ${apiGroup.group}
<%}%>
<%
for(api in apiGroup.childrenApiDocs){
%>
## ${api.desc}
<%
for(doc in api.list){
%>
<%if(doc.deprecated){%>
### ~~${htmlEscape(doc.desc)}~~
<%}else{%>
### ${htmlEscape(doc.desc)}
<%}%>
**URL:** ${doc.url}

**Type:** ${doc.type}

<%if(isNotEmpty(doc.author)){%>
**Author:** ${doc.author}
<%}%>

**Content-Type:** ${doc.contentType}

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
<% } %>
<% } %>
<%if(isNotEmpty(errorCodeList)){%>
<%if(apiDocListOnlyHasDefaultGroup) { %>
## ${errorListTitle}
<% } else { %>
# ${errorListTitle}
<% } %>

| Error code | Description |
|------------|-------------|
<%
for(error in errorCodeList){
%>
|${error.value}|${htmlEscape(error.desc)}|
<%}%>
<%}%>

<%if(isNotEmpty(dictList)){%>
<%if(apiDocListOnlyHasDefaultGroup) { %>
## ${dictListTitle}
<% } else { %>
# ${dictListTitle}
<% } %>

<%
for(dict in dictList){
%>

<%if(apiDocListOnlyHasDefaultGroup) { %>
### ${dict.title}
<% } else { %>
## ${dict.title}
<% } %>

| Code | Type | Description |
|------|------|-------------|
<%
for(dataDict in dict.dataDictList){
%>
|${dataDict.value}|${dataDict.type}|${htmlEscape(dataDict.desc)}|
<%}%>
<%}%>
<%}%>
