<%@ page import="beans.User"%><%@ page import="java.util.Map"%><%@ page import="java.util.HashMap"%><%@ page import="util.Json"%><%--
  Created by IntelliJ IDEA.
  User: Tobias
  Date: 2019/7/3
  Time: 9:44
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    boolean success;
    String id = request.getParameter("user_id");
    User u = User.getById(id);
    User self = (User)request.getAttribute("user");
    Map<String, Object> data;
    if(u.getId()!=null){
        data = u.getFields();
        if (self!=null){
            data.put("isFollow", self.isFollow(u));
        }else {
            data.put("isFollow", false);
        }
        success = true;
    }else {
        data = new HashMap<>();
        success = false;
        data.put("error", "没有找到用户");
    }
    data.put("success", success);
    out.print(Json.fromCollection(data));
%>