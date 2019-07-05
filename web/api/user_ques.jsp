<%@ page import="java.util.ArrayList"%><%@ page import="beans.Question"%><%@ page import="beans.User"%><%@ page import="java.util.Map"%><%@ page import="java.util.HashMap"%><%@ page import="java.util.stream.Collectors"%><%@ page import="util.Json"%><%--
  Created by IntelliJ IDEA.
  User: yrt19
  Date: 2019/7/5
  Time: 23:04
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    boolean success;
    String user_id = request.getParameter("user_id");
    int start = Integer.parseInt(request.getParameter("start"));
    int end = Integer.parseInt(request.getParameter("end"));
    User user = User.getById(user_id);
    Map<String, Object> res = new HashMap<>();
    if (user.getId() == null){
        success = false;
        res.put("error", "没有找到用户");
    }else {
        ArrayList<Question> questions = Question.getQuestionsByUser(user, start, end);
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
                map.put("time", it.getTime().getTime());
                map.put("answer_count", it.getAnswerCount());
                return map;
            }).collect(Collectors.toList()));
        }
    }
    res.put("success", success);
    out.print(Json.fromCollection(res));
%>