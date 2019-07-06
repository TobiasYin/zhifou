<%@ page import="java.util.Map"%><%@ page import="java.util.HashMap"%><%@ page import="beans.User"%><%@ page import="util.Json"%><%--
  Created by IntelliJ IDEA.
  User: yrt19
  Date: 2019/7/7
  Time: 01:19
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%
    boolean success;
    Map<String, Object> res = new HashMap<>();
    String username = request.getParameter("username");
    String password = request.getParameter("old_password");
    String new_pass = request.getParameter("new_password");
    String captcha = request.getParameter("captcha");
    User self = (User)request.getAttribute("user");
    String realcaptcha = (String) session.getAttribute("captcha");
    if (self == null){
        self = User.login(username, password);
    }
    if (!realcaptcha.toLowerCase().equals(captcha.toLowerCase())){
            success = false;
            res.put("error", "验证码错误");
    }else if (self == null){
        res.put("error", "登陆才能修改密码");
        success = false;
    }else if (!self.checkPassword(password)){
        res.put("error", "密码错误");
        success = false;
    }else if(!self.setPassword(new_pass)){
        res.put("error", "未知错误");
        success = false;
    }else {
        success = true;
    }
    if (self!=null){
        for(Cookie c: request.getCookies()){
            if (c.getName().equals("username") || c.getName().equals("id")){
                c.setMaxAge(0);
                response.addCookie(c);
            }
        }
    }
    res.put("success", success);
    out.print(Json.fromCollection(res));
%>