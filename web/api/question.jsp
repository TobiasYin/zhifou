<%@ page import="java.util.Map"%><%@ page import="java.util.HashMap"%><%@ page import="beans.Answer"%><%@ page import="beans.Question"%><%@ page import="java.util.ArrayList"%><%@ page import="beans.Topic"%><%@ page import="java.util.stream.Collectors"%><%@ page import="beans.User"%><%@ page import="util.Json"%><%--
  Created by IntelliJ IDEA.
  User: Tobias
  Date: 2019/7/4
  Time: 21:06
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    boolean success;
    Map<String ,Object> res = new HashMap<>();
    String questionId = request.getParameter("question_id");
    int start = Integer.parseInt(request.getParameter("start"));
    int end = Integer.parseInt(request.getParameter("end"));
    User self = (User)request.getAttribute("user");
    Question question = new Question(questionId);
    if (question.getId() == null){
        success = false;
        res.put("error", "未找到此问题");
    }else {
        ArrayList<Answer> answers = Answer.getAnswersByQuestion(question, start, end);
        if (answers == null){
            success = false;
            res.put("error", "发送未知错误");
        }else {
            success = true;
            res.putAll(question.getFields());
            res.remove("user");
            res.put("topics", Topic.getTopicsByQuestion(question).stream().map((it)->{
                Map<String, Object> map = new HashMap<>();
                map.put("topic", it.getTopic());
                map.put("topic_id", it.getId());
                return map;
            }).collect(Collectors.toList()));
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