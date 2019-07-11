<%@ page import="beans.User"%><%@ page import="java.util.Map"%><%@ page import="java.util.HashMap"%><%@ page import="beans.Answer"%><%@ page import="beans.Question"%><%@ page import="util.Json"%><%--
  Created by IntelliJ IDEA.
  User: yrt19
  Date: 2019/7/5
  Time: 22:46
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    boolean success;
    Map<String, Object> data = Json.fromReaderGetMap(request.getReader());
    User u = (User)request.getAttribute("user");
    String question_id = (String) data.get("question_id");
    String content = (String) data.get("content");
    Map<String, Object> res = new HashMap<>();
    if (u == null){
        res.put("error", "登陆才能发表文章");
        success = false;
    }else {
        Question question = new Question(question_id);
        if (question.getId() == null){
            res.put("error", "没有找到目标问题");
            success = false;
        }else {
            Answer answer = Answer.addAnswer(question, u, content);
            if (answer == null){
                res.put("error", "发生未知错误");
                success = false;
            }else {
                success = true;
                Map<String, Object> fields = answer.getFields();
                fields.put("is_agree", 0);
                res.put("info", fields);
            }
        }
    }
    res.put("success", success);
    out.print(Json.fromCollection(res));
%>