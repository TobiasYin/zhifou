<%@ page import="java.util.HashMap"%><%@ page import="java.util.Map"%><%@ page import="beans.Answer"%><%@ page import="java.util.ArrayList"%><%@ page import="beans.User"%><%@ page import="java.util.List"%><%@ page import="java.util.stream.Collectors"%><%@ page import="util.Json"%><%--
  Created by IntelliJ IDEA.
  User: Tobias
  Date: 2019/7/4
  Time: 18:14
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%
    int start = Integer.parseInt(request.getParameter("start"));
    int end = Integer.parseInt(request.getParameter("end"));
    boolean success;
    User u = (User) request.getAttribute("user");
    Map<String, Object> res = new HashMap<>();
    ArrayList<Answer> answers = Answer.getNewestAnswers(start, end);
    List<Map<String, Object>> list;
    if (answers != null){
        list = answers.stream().map((it) -> {
            Map<String, Object> temp = it.getFields();
            temp.put("is_agree", it.isAgree(u));
            temp.remove("user");
            temp.remove("question");
            return temp;
        }).collect(Collectors.toList());
        success = true;
        res.put("data", list);
        res.put("hasMore", answers.size() == (start - end));
    }else {
        success = false;
        res.put("error", "获取失败，位置错误");
    }
    res.put("success", success);
    out.print(Json.fromCollection(res));
%>