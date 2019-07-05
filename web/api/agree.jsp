<%@ page import="beans.User"%><%@ page import="java.util.Map"%><%@ page import="java.util.HashMap"%><%@ page import="beans.Answer"%><%@ page import="util.Json"%><%--
  Created by IntelliJ IDEA.
  User: yrt19
  Date: 2019/7/6
  Time: 0:28
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    boolean success;
    Map<String, Object> map = new HashMap<>();
    String answer_id = request.getParameter("answer_id");
    int agree = Integer.parseInt(request.getParameter("agree"));
    User self = (User)request.getAttribute("user");
    if (self == null){
        success = false;
        map.put("error", "登陆才能赞同");
    }else {
        Answer answer = new Answer(answer_id);
        if (answer.getId() == null){
            success = false;
            map.put("error", "未找到此文章");
        }else {
            success = true;
            if (agree == -1)
                answer.disAgree(self);
            else if (agree == 1)
                answer.agree(self);
            else if (agree == 0)
                answer.cancelAgree(self);
            else {
                success = false;
                map.put("error", "状态错误");
            }
        }
    }
    map.put("success", success);
    out.print(Json.fromCollection(map));
%>