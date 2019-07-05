<%@ page import="beans.Topic"%><%@ page import="java.util.ArrayList"%><%@ page import="java.util.Map"%><%@ page import="java.util.HashMap"%><%@ page import="beans.Entity"%><%@ page import="java.util.stream.Collectors"%><%@ page import="util.Json"%><%--
  Created by IntelliJ IDEA.
  User: yrt19
  Date: 2019/7/5
  Time: 21:02
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%
    int start = Integer.parseInt(request.getParameter("start"));
    int end = Integer.parseInt(request.getParameter("end"));
    ArrayList<Topic> topics = Topic.getAllTopics(start, end);
    Map<String, Object> res = new HashMap<>();
    res.put("success", true);
    res.put("hasMore", start - end == topics.size());
    res.put("data", topics.stream().map(Entity::getFields).collect(Collectors.toList()));
    out.print(Json.fromCollection(res));
%>