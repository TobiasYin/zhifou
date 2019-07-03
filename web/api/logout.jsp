<%@ page import="beans.User"%><%--
  Created by IntelliJ IDEA.
  User: Tobias
  Date: 2019/7/3
  Time: 14:47
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    User u  = (User)request.getAttribute("user");
    if (u!=null){
        for(Cookie c: request.getCookies()){
            if (c.getName().equals("username") || c.getName().equals("id")){
                c.setMaxAge(0);
                response.addCookie(c);
            }
        }
    }
%>
<j:json>
{
    "success": true
}
</j:json>