<%@ page import="beans.User"%><%@ page import="java.util.Map"%><%@ page import="java.util.HashMap"%><%@ page import="util.Json"%><%--
  Created by IntelliJ IDEA.
  User: yrt19
  Date: 2019/7/2
  Time: 21:22
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    boolean success;
    String error;
    String username;
    String userId;
    String head;
    int tips = 0;
    int message = 0;
    User u = (User)request.getAttribute("user");
    Map<String, Object> res = new HashMap<>();
    if (u!=null){
        success = true;
        res.putAll(u.getFields());
    }else {
        success = false;
        error = "需要登录!";
        res.put("error", error);
    }
    res.put("success", success);
    out.print(Json.fromCollection(res));
%>