<%@ page import="beans.User"%><%--
  Created by IntelliJ IDEA.
  User: Tobias
  Date: 2019/7/3
  Time: 10:47
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    boolean success;
    String error = "";
    User u = (User)request.getAttribute("user");
    if (u == null){
        success = false;
        error = "登陆才能设置头像";
    }else {
        Part part = request.getPart("data");
        if (part.getContentType().equals("image/png") || part.getContentType().equals("image/jpeg")){
            String path = ((ServletContext)jspContext).getRealPath("/image/user_head/");
            String name = u.getId()+System.currentTimeMillis() + "." + part.getContentType().split("/")[1];
            part.write(path + name);
            boolean res = u.setHead(name);
            if (!res){
                success = false;
                error = "未知错误";
            } else success = true;
        }else {
            success = false;
            error = "不支持的格式";
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