<%@ page import="beans.User"%><%@ page import="java.util.ArrayList"%><%@ page import="java.util.Map"%><%@ page import="java.util.HashMap"%><%@ page import="java.util.List"%><%@ page import="util.Json"%><%--
  Created by IntelliJ IDEA.
  User: Tobias
  Date: 2019/7/3
  Time: 10:16
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%@ taglib prefix="j" uri="json.tag" %>
<%
    boolean success;
    String id = request.getParameter("user_id");
    User user = User.getById(id);
    User self = (User)request.getAttribute("user");
    Map<String, Object> res = new HashMap<>();
    if (user.getUsername()!=null){
        ArrayList<User> u = user.getFollowees();
        ArrayList<Map<String, Object>> obj = new ArrayList<>();
        if (u==null){
            success = false;
            res.put("error", "未知错误");
        }else if (self == null){
            for(User i: u){
                Map<String, Object> m = i.getFields();
                m.put("isFollow", false);
                m.put("followers_count", i.getFollowCount());
                m.put("anwser_count", i.getAnswerCount());
                m.put("answer_count", i.getAnswerCount());
                obj.add(m);
            }
            res.put("data", obj);
            success = true;
        }else {
            for(User i: u){
                Map<String, Object> m = i.getFields();
                m.put("isFollow", self.isFollow(i));
                m.put("followers_count", i.getFollowCount());
                m.put("anwser_count", i.getAnswerCount());
                m.put("answer_count", i.getAnswerCount());
                obj.add(m);
            }
            res.put("data", obj);
            success = true;
        }
    }else {
        success = false;
        res.put("error", "没有找到用户");
    }
    res.put("success", success);
    out.print(Json.fromCollection(res));
%>