<%@ page import="beans.User"%><%@ page import="java.util.Map"%><%@ page import="java.util.HashMap"%><%@ page import="beans.Topic"%><%@ page import="util.Json"%><%--
  Created by IntelliJ IDEA.
  User: yrt19
  Date: 2019/7/5
  Time: 21:26
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    boolean success;
    Map<String, Object> res = new HashMap<>();
    User self = (User)request.getAttribute("user");
    if (self == null){
        success = false;
        res.put("error", "登陆才能发布话题");
    }else {
        String topic_name = request.getParameter("topic_name");
        String topic_desc = request.getParameter("topic_desc");
        Part part = request.getPart("topic_pic");
        if (part.getContentType().equals("image/png") || part.getContentType().equals("image/jpeg")){
            String path = ((ServletContext)jspContext).getRealPath("/image/user_head/");
            String name = self.getId()+System.currentTimeMillis() + "." + part.getContentType().split("/")[1];
            part.write(path + name);
            Topic t = Topic.addTopic(topic_name, topic_desc, name);
            if (t == null){
                success = false;
                res.put("error", "未知错误");
            }else {
                success = true;
                res.putAll(t.getFields());
            }
        }else {
            success = false;
            res.put("error", "图片格式不支持");
        }
    }
    res.put("success", success);
    out.print(Json.fromCollection(res));
%>