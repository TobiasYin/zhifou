<%@ page import="java.util.Map"%><%@ page import="java.util.HashMap"%><%@ page import="beans.User"%><%@ page import="util.Json"%><%@ page import="beans.Answer"%><%@ page import="java.util.ArrayList"%><%@ page import="java.util.stream.Collectors"%><%--
  Created by IntelliJ IDEA.
  User: yrt19
  Date: 2019/7/5
  Time: 23:48
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%
    boolean success;
    int start = Integer.parseInt(request.getParameter("start"));
    int end = Integer.parseInt(request.getParameter("end"));
    Map<String, Object> res = new HashMap<>();
    User self = (User)request.getAttribute("user");
    if (self == null){
        res.put("error", "登陆才可查看此界面");
        success = false;
    }else {
        ArrayList<Answer> answers = Answer.getFollowingsAnswersList(self, start, end);
        if (answers == null){
            success = false;
            res.put("error", "未知错误");
        }else {
            success = true;
            res.put("hasMore", end - start == answers.size());
            res.put("data", answers.stream().map((it)->{
                Map<String, Object> map = it.getFields();
                map.remove("question");
                map.remove("user");
                map.put("is_agree", it.isAgree(self));
                return map;
            }).collect(Collectors.toList()));
        }

    }
    res.put("success", success);
    out.print(Json.fromCollection(res));
%>
