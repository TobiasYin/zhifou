<%@ page import="beans.User"%><%@ page import="java.util.Map"%><%@ page import="util.Json"%><%--
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
    Map<String, Object> data = Json.fromReaderGetMap(request.getReader());
    if(request.getAttribute("user")!=null){
        success = false;
        error = "已登录不能注册!";
    }else {
        String username = (String) data.get("username");
        String password = (String) data.get("password");
        String captcha = (String) data.get("captcha");
        String realcaptcha = (String) session.getAttribute("captcha");
        if (captcha == null || !realcaptcha.toLowerCase().equals(captcha.toLowerCase())){
            success = false;
            error = "验证码错误";
        }else {
            User u = User.register(username, password);
            success = u.isStatus();
            error = u.getMessage();
        }
    }
    session.removeAttribute("captcha");
    pageContext.setAttribute("success", success);
    pageContext.setAttribute("error", error);
%>
<j:json>
{
    "success": success,
    "error": error
}
</j:json>