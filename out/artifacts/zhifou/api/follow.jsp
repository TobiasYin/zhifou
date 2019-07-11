<%@ page import="beans.User"%><%@ page import="java.util.Map"%><%@ page import="util.Json"%><%--
  Created by IntelliJ IDEA.
  User: Tobias
  Date: 2019/7/3
  Time: 14:55
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    boolean success;
    Map<String, Object> data = Json.fromReaderGetMap(request.getReader());
    String error = "";
    String user_id = (String) data.get("user_id");
    System.out.println(user_id);
    User u = User.getById(user_id);
    User self = (User)request.getAttribute("user");
    System.out.println(u.getId());
    System.out.println(u);
    if (u.getId()!=null){
        if (self!=null){
            if(self.follow(u)){
                success = true;
            }else {
                error = "已关注过了";
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