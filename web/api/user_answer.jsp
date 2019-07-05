<%@ page import="beans.User"%><%@ page import="java.util.Map"%><%@ page import="java.util.HashMap"%><%@ page import="beans.Answer"%><%@ page import="java.util.ArrayList"%><%@ page import="java.util.stream.Collectors"%><%@ page import="util.Json"%><%--
  Created by IntelliJ IDEA.
  User: yrt19
  Date: 2019/7/5
  Time: 22:52
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    boolean success;
    String user_id = request.getParameter("user_id");
    int start = Integer.parseInt(request.getParameter("start"));
    int end = Integer.parseInt(request.getParameter("end"));
    User u = User.getById(user_id);
    User self = (User)request.getAttribute("user");
    Map<String, Object> res = new HashMap<>();
    if (u.getId() == null){
        success = false;
        res.put("error", "没找到用户");
    }else {
        ArrayList<Answer> answers = Answer.getAnswersByUser(u, start, end);
        if (answers == null){
            success = false;
            res.put("error", "未知错误");
        }else {
            success = true;
            res.put("hasMore", end - start == answers.size());
            res.put("data", answers.stream().map((it)->{
                Map<String, Object> map = it.getFields();
                map.remove("user");
                map.remove("question");
                map.put("is_agree", it.isAgree(self));
                return map;
            }).collect(Collectors.toList()));
        }
    }
    res.put("success", success);
    out.print(Json.fromCollection(res));
%>