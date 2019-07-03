<%@ page import="beans.User"%><%--
  Created by IntelliJ IDEA.
  User: Tobias
  Date: 2019/7/3
  Time: 15:22
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    boolean success;
    String error = "";
    String user_id = request.getParameter("user_id");
    System.out.println(user_id);
    User u = User.getById(user_id);
    User self = (User)request.getAttribute("user");
    System.out.println(u.getId());
    System.out.println(u);
    if (u.getId()!=null){
        if (self!=null){
            if(self.unfollow(u)){
                success = true;
            }else {
                error = "未关注不能取关";
                success = false;
            }
        }else {
            error = "请先登录";
            success = false;
        }
    }else {
        error = "没有找到用户";
        success = false;
    }
    pageContext.setAttribute("success",success);
    pageContext.setAttribute("error", error);
%>
<j:json>
{
    "success": success,
    "error": error
}
</j:json>