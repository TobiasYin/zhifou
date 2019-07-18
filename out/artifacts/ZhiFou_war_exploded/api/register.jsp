<%@ page import="beans.User"%><%--
  Created by IntelliJ IDEA.
  User: yrt19
  Date: 2019/7/2
  Time: 20:47
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    boolean success;
    String error;
    if(request.getAttribute("user")!=null){
        success = false;
        error = "已登录不能注册!";
    }else {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String captcha = request.getParameter("captcha");
        String realcaptcha = (String) session.getAttribute("captcha");
        if (!realcaptcha.equals(captcha)){
            success = false;
            error = "验证码错误";
        }else {
            User u = User.register(username, password);
            success = u.isStatus();
            error = u.getMessage();
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