<%@ page import="java.util.Map"%><%@ page import="java.util.HashMap"%><%@ page import="beans.Question"%><%@ page import="java.util.ArrayList"%><%@ page import="java.util.stream.Collectors"%><%@ page import="util.Json"%><%--
  Created by IntelliJ IDEA.
  User: Tobias
  Date: 2019/7/9
  Time: 23:50
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    boolean success;
    int start = Integer.parseInt(request.getParameter("start"));
    int end = Integer.parseInt(request.getParameter("end"));
    Map<String, Object> res = new HashMap<>();
    ArrayList<Question> questions = Question.getQuestions(start, end);
    if (questions == null){
        success = false;
        res.put("error", "未知错误");
    }else {
        success = true;
        res.put("hasMore", end - start == questions.size());
        res.put("data", questions.stream().map((it)->{
            Map<String, Object> map = new HashMap<>();
            map.put("question_id", it.getId());
            map.put("question_title", it.getQuestion());
            map.put("time", it.getTime());
            map.put("answer_count", it.getAnswerCount());
            return map;
        }).collect(Collectors.toList()));
    }
    res.put("success", success);
    out.print(Json.fromCollection(res));
%>