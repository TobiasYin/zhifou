<%@ page import="beans.User"%><%@ page import="util.EncodeAndDecode"%><%--
  Created by IntelliJ IDEA.
  User: yrt19
  Date: 2019/7/2
  Time: 20:08
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    boolean success;
    String error = "";
    if(request.getAttribute("user")!=null){
        success = true;
    }else {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        User user = User.login(username, password);
        if (user == null){
            success = false;
            error = "账户名或密码错误";
        }else {
            success = true;
            Cookie nameCookie = new Cookie("username", EncodeAndDecode.encode(username));
            Cookie idCookie = new Cookie("id", EncodeAndDecode.encode(user.getId()));
            nameCookie.setMaxAge(30 * 24 *3600);
            idCookie.setMaxAge(30 * 24 *3600);
            response.addCookie(nameCookie);
            response.addCookie(idCookie);
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