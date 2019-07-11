<%@ page import="beans.Topic"%><%@ page import="java.util.Map"%><%@ page import="java.util.HashMap"%><%@ page import="beans.Answer"%><%@ page import="java.util.ArrayList"%><%@ page import="beans.User"%><%@ page import="java.util.stream.Collectors"%><%@ page import="util.Json"%><%--
  Created by IntelliJ IDEA.
  User: Tobias
  Date: 2019/7/9
  Time: 20:42
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    boolean success;
    String topic_id = request.getParameter("topic_id");
    int start = Integer.parseInt(request.getParameter("start"));
    int end = Integer.parseInt(request.getParameter("end"));
    Topic t = new Topic(topic_id);
    User self = (User)request.getAttribute("user");
    Map<String, Object> res = new HashMap<>();
    if (t.getId() == null){
        success = false;
        res.put("error", "没有找到话题");
    }else {
        ArrayList<Answer> answers = Answer.getAnswersByTopic(t, start, end);
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