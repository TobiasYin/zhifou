package servlet;

import beans.Topic;
import beans.User;
import util.Json;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "sadd_topic", urlPatterns = {"/sapi/add_topic"})
@MultipartConfig
public class add_topic extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean success;
        resp.setContentType("text/html;charset=UTF-8");
        Map<String, Object> res = new HashMap<>();
        User self = (User) req.getAttribute("user");
        if (self == null) {
            success = false;
            res.put("error", "登陆才能发布话题");
        } else {
            String topic_name = req.getParameter("topic_name");
            String topic_desc = req.getParameter("topic_desc");
            Part part = req.getPart("topic_pic");
            if (part.getContentType().equals("image/png") || part.getContentType().equals("image/jpeg")) {
                String path = req.getServletContext().getRealPath("/image/user_head/");
                String name = self.getId() + System.currentTimeMillis() + "/" + part.getContentType().split("/")[1];
                part.write(path + name);
                Topic t = Topic.addTopic(topic_name, topic_desc, name);
                if (t == null) {
                    success = false;
                    res.put("error", "未知错误");
                } else {
                    success = true;
                    res.putAll(t.getFields());
                }
            } else {
                success = false;
                res.put("error", "图片格式不支持");
            }
        }
        res.put("success", success);
        resp.getWriter().print(Json.fromCollection(res));
        super.service(req, resp);
    }
}