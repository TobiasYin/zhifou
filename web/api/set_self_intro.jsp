<%@ page import="beans.User"%><%--
  Created by IntelliJ IDEA.
  User: Tobias
  Date: 2019/7/3
  Time: 10:41
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    boolean success;
    String error = "";
    String intro = request.getParameter("intro");
    User u = (User)request.getAttribute("user");
    if (u == null){
        success = false;
        error = "请先登录";
    }else {
        success = u.setIntro(intro);
        if (!success) error = "未知错误";
    }
    pageContext.setAttribute("success", success);
    pageContext.setAttribute("error", error);
%>
<j:json>
{
    "success": success,
    "error":error
}
</j:json>