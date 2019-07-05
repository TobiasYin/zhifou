<%@ page import="beans.User"%><%@ page import="java.util.HashMap"%><%@ page import="java.util.Map"%><%@ page import="beans.Comment"%><%@ page import="beans.Answer"%><%@ page import="util.Json"%><%--
  Created by IntelliJ IDEA.
  User: yrt19
  Date: 2019/7/5
  Time: 20:52
--%>
<%@ page contentType="application/json;charset=UTF-8" language="java" %>
<%
    boolean success;
    User self = (User)request.getAttribute("user");
    String answer_id = request.getParameter("answer_id");
    String refer = request.getParameter("refer");
    String content = request.getParameter("content");
    Map<String ,Object> res = new HashMap<>();
    if (self == null){
        success = false;
        res.put("error", "登陆才能发表评论");
    }else {
        Answer answer = new Answer(answer_id);
        if (answer.getId() == null){
            success = false;
            res.put("error", "没有找到对应文章");
        }else {
            if (refer != null){
                Comment refer_comment = new Comment(refer);
                if (refer_comment.getId() == null){
                    success = false;
                    res.put("error", "文章引用有误");
                }else {
                    Comment comment = Comment.addComment(answer, self, content, refer_comment);
                    if (comment == null){
                        success = false;
                        res.put("error", "未知错误");
                    }else {
                        res.put("info", comment.getFields());
                        success = true;
                    }
                }
            }else {
                Comment comment = Comment.addComment(answer, self, content, null);
                if (comment == null){
                    success = false;
                    res.put("error", "未知错误");
                }else {
                    res.put("info", comment.getFields());
                    success = true;
                }
            }
        }
    }
    res.put("success", success);
    out.print(Json.fromCollection(res));
%>
