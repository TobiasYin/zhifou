<%--
  Created by IntelliJ IDEA.
  User: yrt19
  Date: 2019/7/2
  Time: 21:18
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    boolean islogin = request.getAttribute("user")!=null;
    pageContext.setAttribute("islogin", islogin);
%>
<j:json>
{
    "islogin": islogin
}
</j:json>