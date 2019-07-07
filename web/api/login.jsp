<%@ page import="beans.User"%><%@ page import="util.EncodeAndDecode"%><%@ page import="java.util.Enumeration"%><%@ page import="java.io.InputStream"%><%@ page import="java.io.BufferedInputStream"%><%@ page import="java.io.InputStreamReader"%><%@ page import="java.io.BufferedReader"%><%@ page import="java.util.Map"%><%@ page import="util.Json"%><%--
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
        Map<String, Object> data = Json.fromReaderGetMap(request.getReader());
        String username = (String)data.get("username");
        String password = (String)data.get("password");
        System.out.println("para");
        for (Object m:data.keySet()){
            System.out.println(m + "--->" + data.get(m));
        }
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