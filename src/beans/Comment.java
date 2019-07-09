package beans;

import database.DataBasePool;
import util.GetUUID;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Comment implements Entity {
    private String id;
    private String user_id;
    private User user;
    private String answer_id;
    private Answer answer;
    private String content;
    private Timestamp time;
    private String refer_id;
    private Comment refer = null;

    public Comment(String id) {
        connectDB(id);
    }

    private void connectDB(String id) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement statement = c.prepareStatement("select id, user_id, answer_id, content, refer, time from comment where id = ?")) {
            statement.setString(1, id);
            statement.executeQuery();
            ResultSet res = statement.getResultSet();
            if (res.next()) {
                this.id = res.getString(1);
                user_id = res.getString(2);
                answer_id = res.getString(3);
                content = res.getString(4);
                refer_id = res.getString(5);
                time = res.getTimestamp(6);
                user = User.getById(user_id);
                answer = new Answer(answer_id);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private Comment() {
    }

    private Comment(String id, String user_id, String answer_id, String content, Timestamp time, String refer_id) {
        this.id = id;
        this.user_id = user_id;
        this.answer_id = answer_id;
        this.content = content;
        this.time = time;
        this.refer_id = refer_id;
    }

    public static Comment addComment(Answer answer, User user, String content, Comment refer) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement s1 = c.prepareStatement("insert into comment (id, user_id, answer_id, content, refer) values (?,?,?,?,?)");
             PreparedStatement s2 = c.prepareStatement("insert into comment (id, user_id, answer_id, content) values (?,?,?,?)")) {
            PreparedStatement s;
            String id = GetUUID.getUUID();
            Comment comment = new Comment();
            if (refer == null) {
                s2.setString(1, id);
                s2.setString(2, user.getId());
                s2.setString(3, answer.getId());
                s2.setString(4, content);
                s = s2;
            } else {
                s1.setString(1, id);
                s1.setString(2, user.getId());
                s1.setString(3, answer.getId());
                s1.setString(4, content);
                s1.setString(5, refer.getId());
                comment.refer_id = refer.getId();
                comment.refer = refer;
                s = s1;
            }
            if (s.executeUpdate() == 1) {
                comment.id = id;
                comment.content = content;
                comment.answer = answer;
                comment.user = user;
                comment.user_id = user.getId();
                comment.answer_id = answer.getId();
                comment.time = new Timestamp(System.currentTimeMillis());
                return comment;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Comment> getCommentsByAnswer(Answer answer) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement s = c.prepareStatement("select id, user_id, answer_id, content, refer, time from comment where answer_id = ? order by time desc;")) {
            ArrayList<Comment> ret = new ArrayList<>();
            s.setString(1, answer.getId());
            ResultSet res = s.executeQuery();
            while (res.next()) {
                Comment com = new Comment();
                com.id = res.getString(1);
                com.user_id = res.getString(2);
                com.answer_id = res.getString(3);
                com.content = res.getString(4);
                com.refer_id = res.getString(5);
                com.time = res.getTimestamp(6);
                com.answer = answer;
                com.user = User.getById(com.user_id);
                ret.add(com);
            }
            return ret;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public User getUser() {
        return user;
    }

    public String getAnswer_id() {
        return answer_id;
    }

    public Answer getAnswer() {
        return answer;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getTime() {
        return time;
    }

    public String getRefer_id() {
        return refer_id;
    }

    public Comment getRefer() {
        if (refer != null)
            return refer;
        else return new Comment(refer_id);
    }

    @Override
    public Map<String, Object> getFields() {
        HashMap<String, Object> res = new HashMap<>();
        res.put("comment_id", id);
        res.put("comment_author_id", user_id);
        res.put("comment_author_name", user.getUsername());
        res.put("comment_author_head_sculpture", user.getHead());
        res.put("answer_id", answer_id);
        res.put("content", content);
        res.put("refer", refer_id);
        res.put("time", time.getTime() - 8 * 3600 * 1000);
        res.put("user", user.getFields());
        res.put("answer", answer.getFields());
        return res;
    }

    @Override
    public String toString() {
        return "{" +
                "\"\":\"" + id + '"' +
                ",\"comment_author_id\":\"" + user_id + '"' +
                ",\"comment_author_name\":\"" + user.getUsername() + '"' +
                ",\"comment_author_head_sculpture:\"" + user.getHead() + '"' +
                ",\"content:\"" + content + '"' +
                ",\"time:" + time.getTime() +
                ",\"refer:\"" + refer_id + '"' +
                ",\"answer_id:\"" + answer_id + '"' +
                '}';
    }
}
