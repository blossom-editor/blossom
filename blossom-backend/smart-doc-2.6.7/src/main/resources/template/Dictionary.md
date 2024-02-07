<%if(isNotEmpty(dictList)){%>
# ${dictListTitle!"Data Dictionaries"}
<%
for(dict in dictList){
%>
## ${dict.title}

| Code | Type | Description |
|------|------|-------------|
<%
for(dataDict in dict.dataDictList){
%>
|${dataDict.value}|${dataDict.type}|${htmlEscape(dataDict.desc)}|
<%}%>
<%}%>
<%}%>