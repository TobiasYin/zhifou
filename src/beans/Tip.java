package beans;

import database.DataBasePool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tip implements Entity {
    // 类型: 1: 回答问题, 2:点赞或反对, 3:评论回答, 4:回复评论
    public static final int ANSWER = 1;
    public static final int AGREE = 2;
    public static final int COMMENT = 3;
    public static final int COMMENT_REFER = 4;
    private int type = 0;
    private int id;
    private String action;
    private User u1;
    private User u2;
    private Answer answer;
    private Question question;
    private Comment comment;
    private Comment other_comment;
    private boolean read;

    private Tip() {
    }

    public static int getTipsCount(User u) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement s = c.prepareStatement("select count(*) from tips where user_id = ? and is_read = false")) {
            s.setString(1, u.getId());
            ResultSet res = s.executeQuery();
            if (res.next()) {
                return res.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public boolean read() {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement read = c.prepareStatement("update tips set is_read = true where id = ?")) {
            read.setInt(1, id);
            if (read.executeUpdate() == 1) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static ArrayList<Tip> getTips(User u) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement s = c.prepareStatement("select type, id, other_user_id, action_name, question_id, answer_id, comment_id, other_comment_id, is_read from tips where user_id = ?");
             PreparedStatement read = c.prepareStatement("update tips set is_read = true where user_id = ?")) {
            s.setString(1, u.getId());
            ResultSet res = s.executeQuery();
            ArrayList<Tip> tips = new ArrayList<>();
            while (res.next()) {
                Tip t = new Tip();
                int type = res.getInt(1);
                t.id = res.getInt(2);
                String o_user_id = res.getString(3);
                t.u1 = u;
                t.u2 = User.getById(o_user_id);
                t.action = res.getString(4);
                switch (type) {
                    case ANSWER:
                        t.question = new Question(res.getString(5));
                        t.answer = new Answer(res.getString(6));
                        break;
                    case AGREE:
                        t.answer = new Answer(res.getString(6));
                        break;
                    case COMMENT:
                        t.answer = new Answer(res.getString(6));
                        t.comment = new Comment(res.getString(7));
                        break;
                    case COMMENT_REFER:
                        t.answer = new Answer(res.getString(6));
                        t.comment = new Comment(res.getString(7));
                        t.other_comment = new Comment(res.getString(8));
                        break;
                }
                t.read = res.getBoolean(9);
                tips.add(t);
            }
            read.setString(1, u.getId());
            read.executeUpdate();
            return tips;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, Object> getFields() {
        Map<String, Object> res = new HashMap<>();
        res.put("other_user_id", u2.getId());
        res.put("other_user_name", u2.getUsername());
        res.put("action", action);
        res.put("is_read", read);
        res.put("type", type);
        Question q;
        switch (type) {
            case ANSWER:
                res.put("answer_id", answer.getId());
                res.put("question_id", question.getId());
                res.put("question_title", question.getQuestion());
                break;
            case AGREE:
                res.put("answer_id", answer.getId());
                q = answer.getQuestion();
                res.put("question_id", q.getId());
                res.put("question_title", q.getQuestion());
                break;
            case COMMENT:
                res.put("answer_id", answer.getId());
                res.put("comment_id", comment.getId());
                q = answer.getQuestion();
                res.put("question_id", q.getId());
                res.put("question_title", q.getQuestion());
                break;
            case COMMENT_REFER:
                res.put("answer_id", answer.getId());
                res.put("comment_id", comment.getId());
                res.put("other_comment_id", comment.getId());
                q = answer.getQuestion();
                res.put("question_id", q.getId());
                res.put("question_title", q.getQuestion());
                break;
        }
        return res;
    }
}
