<%@ page import="beans.User" %>
<%@ page import="java.awt.*" %>
<%@ page import="javax.imageio.stream.ImageInputStream" %>
<%@ page import="javax.imageio.ImageIO" %>
<%@ page import="java.awt.image.BufferedImage" %>
<%@ page import="com.mysql.cj.jdbc.Blob" %>
<%@ page import="java.io.*" %><%--
  Created by IntelliJ IDEA.
  User: yrt19
  Date: 2019/7/2
  Time: 21:47
--%>
<%@ page contentType="image/jpg" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    User u = User.getById(request.getParameter("id"));
    String url;
    if (u.getId() == null) {
        url = "F:\\ZhiFou\\web\\image\\default.jpg";
        try (InputStream is = new BufferedInputStream(new FileInputStream(url))) {
            BufferedImage image = ImageIO.read(is);
            ImageIO.write(image, "jpeg", response.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    } else {
        url = u.getHead();
        pageContext.forward("/image/user_head/" + url);
    }
%>