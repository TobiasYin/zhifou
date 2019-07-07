<%@ page import="beans.User"%><%@ page import="java.util.Map"%><%@ page import="java.util.HashMap"%><%@ page import="beans.Question"%><%@ page import="util.Json"%><%@ page import="java.lang.reflect.Array"%><%@ page import="java.util.ArrayList"%><%--
  Created by IntelliJ IDEA.
  User: Tobias
  Date: 2019/7/4
  Time: 19:54
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%
    boolean success;
    Map<String, Object> data = Json.fromReaderGetMap(request.getReader());
    User u = (User)request.getAttribute("user");
    String question_title = (String) data.get("question");
    String question_desc = (String) data.get("question_desc");
    ArrayList<String> topics = (ArrayList<String>) data.get("topics");
    Map<String, Object> res = new HashMap<>();
    if (u == null){
        res.put("error", "登陆才能发表文章");
        success = false;
    }else {
        Question q = Question.addQuestion(u, question_title, question_desc);
        if (q == null){
            res.put("error", "发生未知错误");
            success = false;
        }else {
            q.setTopics(topics);
            success = true;
            res.put("question_id", q.getId());
        }
    }
    res.put("success", success);
    out.print(Json.fromCollection(res));
%>