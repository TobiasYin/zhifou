<%@ page import="java.awt.*" %>
<%@ page import="javax.imageio.ImageIO" %>
<%@ page import="util.GetCaptcha" %>
<%@ page import="com.mysql.cj.Session" %><%--
  Created by IntelliJ IDEA.
  User: yrt19
  Date: 2019/7/2
  Time: 23:42
--%>
<%@ page contentType="image/jpeg" language="java" %>
<%
    GetCaptcha.Captcha c = GetCaptcha.get();
    ImageIO.write(c.getImage(), "jpeg", response.getOutputStream());
    session.setAttribute("captcha", c.getCaptcha());
%>