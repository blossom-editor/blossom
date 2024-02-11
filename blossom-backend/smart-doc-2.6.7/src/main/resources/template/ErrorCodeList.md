
# Error code list

| Error code | Description |
|------------|-------------|
<%
for(error in list){
%>
|${error.value}|${htmlEscape(error.desc)}|
<%}%>