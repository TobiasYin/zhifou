<%@ page import="beans.User"%><%@ page import="java.util.ArrayList"%><%@ page import="java.util.Map"%><%@ page import="java.util.HashMap"%><%@ page import="java.util.stream.Collectors"%><%@ page import="util.Json"%><%--
  Created by IntelliJ IDEA.
  User: yrt19
  Date: 2019/7/6
  Time: 0:40
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%
    User self = (User) request.getAttribute("user");
    boolean success;
    String text= request.getParameter("text");
//    int start = Integer.parseInt(request.getParameter("start"));
//    int end = Integer.parseInt(request.getParameter("end"));
    ArrayList<User> users = User.getUsersBySearch(text);
    Map<String, Object> res = new HashMap<>();
    if (users == null){
        res.put("error", "发生未知错误");
        success = false;
    }else {
        success = true;
//        res.put("hasMore", end -start == users.size());
        res.put("data", users.stream().map((it)->{
            Map<String, Object> map = it.getFields();
            map.put("answer_count", it.getAnswerCount());
            map.put("followers_count", it.getFollowCount());
            if (self != null)
                map.put("isFollow", self.isFollow(it));
            else map.put("isFollow", false);
            return map;
        }).collect(Collectors.toList()));
    }
    res.put("success", success);
    out.print(Json.fromCollection(res));
%>