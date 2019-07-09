<%@ page import="beans.Topic"%><%@ page import="java.util.Map"%><%@ page import="java.util.HashMap"%><%@ page import="util.Json"%><%--
  Created by IntelliJ IDEA.
  User: Tobias
  Date: 2019/7/9
  Time: 20:35
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    boolean success;
    Map<String, Object> map = new HashMap<>();
    String topic_id = request.getParameter("topic_id");
    Topic t = new Topic(topic_id);
    if (t.getId()==null){
        map.put("error", "未找到话题");
        success = false;
    }else {
        map.putAll(t.getFields());
        success = true;
    }
    map.put("success", success);
    out.print(Json.fromCollection(map));
%>