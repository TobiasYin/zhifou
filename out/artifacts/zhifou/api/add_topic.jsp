<%@ page import="beans.User"%><%@ page import="java.util.Map"%><%@ page import="java.util.HashMap"%><%@ page import="beans.Topic"%><%@ page import="util.Json"%><%@ page import="java.util.Collection"%><%@ page import="java.io.Reader"%><%@ page import="java.io.BufferedReader"%><%--
  Created by IntelliJ IDEA.
  User: yrt19
  Date: 2019/7/5
  Time: 21:26
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    boolean success;
    Map<String, Object> data = Json.fromReaderGetMap(request.getReader());
    Map<String, Object> res = new HashMap<>();
    User self = (User)request.getAttribute("user");
    if (self == null){
        success = false;
        res.put("error", "登陆才能发布话题");
    }else {
        String topic_name = (String) data.get("topic_name");
        String topic_desc = (String) data.get("topic_desc");
//        Part part = request.getPart("topic_pic");
//        if (part.getContentType().equals("image/png") || part.getContentType().equals("image/jpeg")){
//            String path = ((ServletContext)jspContext).getRealPath("/image/user_head/");
//            String name = self.getId()+System.currentTimeMillis() + "." + part.getContentType().split("/")[1];
//            part.write(path + name);
//            Topic t = Topic.addTopic(topic_name, topic_desc, name);
//            if (t == null){
//                success = false;
//                res.put("error", "未知错误");
//            }else {
//                success = true;
//                res.putAll(t.getFields());
//            }
//        }else {
//            success = false;
//            res.put("error", "图片格式不支持");
//        }
//    }
        Topic t = Topic.addTopic(topic_name, topic_desc, null);
        if (t == null){
            success = false;
            res.put("error", "未知错误");
        }else {
            success = true;
            res.put("info", t.getFields());
        }
    }
    res.put("success", success);
    out.print(Json.fromCollection(res));
%>