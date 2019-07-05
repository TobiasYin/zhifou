<%@ page import="java.util.ArrayList"%><%@ page import="beans.Topic"%><%@ page import="java.util.Map"%><%@ page import="java.util.HashMap"%><%@ page import="beans.Entity"%><%@ page import="java.util.stream.Collectors"%><%@ page import="util.Json"%><%--
  Created by IntelliJ IDEA.
  User: yrt19
  Date: 2019/7/6
  Time: 0:52
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    boolean success;
    Map<String, Object> res = new HashMap<>();
    String name = request.getParameter("name");
    ArrayList<Topic> topics = Topic.getTopicsByKeyword(name);
    if (topics == null){
        res.put("error", "未知错误");
        success = false;
    }else {
        success = true;
        res.put("data", topics.stream().map(Entity::getFields).collect(Collectors.toList()));
    }
    res.put("success", success);
    out.println(Json.fromCollection(res));
%>