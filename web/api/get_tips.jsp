<%@ page import="beans.User"%><%@ page import="java.util.Map"%><%@ page import="java.util.HashMap"%><%@ page import="beans.Tip"%><%@ page import="java.util.ArrayList"%><%@ page import="beans.Entity"%><%@ page import="java.util.stream.Collectors"%><%@ page import="util.Json"%><%--
  Created by IntelliJ IDEA.
  User: yrt19
  Date: 2019/7/6
  Time: 0:50
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%
    boolean success;
    Map<String, Object> res = new HashMap<>();
    User self = (User) request.getAttribute("user");
    if (self == null){
        res.put("error", "登陆才能获取提醒");
        success = false;
    }else {
        ArrayList<Tip> tips = Tip.getTips(self);
        System.out.println(tips.get(0).getFields());
        if (tips == null){
            res.put("error", "未知错误");
            success = false;
        }else {
            res.put("tips", tips.stream().map(Entity::getFields).collect(Collectors.toList()));
            success = true;
        }
    }
    res.put("success", success);
    out.print(Json.fromCollection(res));
%>