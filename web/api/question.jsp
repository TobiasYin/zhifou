<%--
  Created by IntelliJ IDEA.
  User: Tobias
  Date: 2019/7/4
  Time: 21:06
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    String questionId = request.getParameter("question_id");
    int start = Integer.parseInt(request.getParameter("start"));
    int end = Integer.parseInt(request.getParameter("end"));
%>
<j:json>
{
}
</j:json>