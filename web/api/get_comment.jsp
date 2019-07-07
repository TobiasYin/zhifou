<%@ page import="java.util.ArrayList"%><%@ page import="beans.Comment"%><%@ page import="beans.Answer"%><%@ page import="java.util.Map"%><%@ page import="java.util.HashMap"%><%@ page import="java.util.stream.Collectors"%><%@ page import="util.Json"%><%--
  Created by IntelliJ IDEA.
  User: yrt19
  Date: 2019/7/5
  Time: 20:44
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    boolean success;
    Map<String, Object> data = Json.fromReaderGetMap(request.getReader());
    String answer_id = (String) data.get("answer_id");
    Answer answer = new Answer(answer_id);
    Map<String , Object> res = new HashMap<>();
    if (answer.getId() == null){
        res.put("error", "没找到回答");
        success = false;
    }else {
        ArrayList<Comment> comments = Comment.getCommentsByAnswer(answer);
        res.put("data", comments.stream().map((it)->{
            Map<String, Object> map = it.getFields();
            map.remove("user");
            map.remove("answer");
            return map;
        }).collect(Collectors.toList()));
        success = true;
    }
    res.put("success",success);
    out.print(Json.fromCollection(res));
%>
