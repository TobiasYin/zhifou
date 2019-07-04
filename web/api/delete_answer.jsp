<%@ page import="beans.User"%><%@ page import="beans.Answer"%><%--
  Created by IntelliJ IDEA.
  User: Tobias
  Date: 2019/7/4
  Time: 19:13
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    User self = (User)request.getAttribute("user");
    String id = request.getParameter("answer_id");
    Answer answer = new Answer(id);
    boolean success;
    String error = "";
    if (answer.getId() == null){
        error = "没有找到文章";
        success = false;
    }else if(self == null){
        error = "请先登录";
        success = false;
    }else if(!self.equals(answer.getUser())){
        error = "你没有权限删除这篇文章";
        success = false;
    }else {
        success = answer.deleteAnswer();
        if (!success){
            error = "未知错误";
        }
    }
    pageContext.setAttribute("success", success);
    pageContext.setAttribute("error", error);
%>
<j:json>
{
    "success": success,
    "error": error
}
</j:json>