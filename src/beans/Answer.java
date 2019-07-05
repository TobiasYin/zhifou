package beans;

import database.DataBasePool;
import util.GetUUID;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Answer implements Entity {
    private String id;
    private String user_id;
    private String question_id;
    private String content;
    private Timestamp time;
    private Question question;
    private User user;

    public Answer(String id) {
        connectDB(id);
    }

    private Answer() {
    }

    private Answer(String id, String user_id, String question_id, String content, Timestamp time, Question question, User user) {
        this.id = id;
        this.user_id = user_id;
        this.question_id = question_id;
        this.content = content;
        this.time = time;
        this.question = question;
        this.user = user;
    }

    private void connectDB(String id) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement statement = c.prepareStatement("select id, user_id, question_id, content, time from answer where id = ?")) {
            statement.setString(1, id);
            statement.executeQuery();
            ResultSet res = statement.getResultSet();
            if (res.next()) {
                this.id = res.getString(1);
                user_id = res.getString(2);
                question_id = res.getString(3);
                content = res.getString(4);
                time = res.getTimestamp(5);
                user = User.getById(user_id);
                question = new Question(question_id);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public int getAgreeCount() {
        int agree = 0;
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement statement = c.prepareStatement("select count(agree) from agree where answer_id = ? and agree = true")) {
            statement.setString(1, id);
            ResultSet res = statement.executeQuery();
            if (res.next()) agree = res.getInt(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return agree;
    }

    public int getCommentCount() {
        int comment = 0;
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement statement = c.prepareStatement("select count(*) from comment where answer_id = ?")) {
            statement.setString(1, id);
            ResultSet res = statement.executeQuery();
            if (res.next()) comment = res.getInt(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return comment;
    }

    public int isAgree(User u) {
        int agree = 0;
        if (u == null)
            return agree;
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement statement = c.prepareStatement("select agree from agree where user_id = ? and answer_id = ?")) {
            statement.setString(1, u.getId());
            statement.setString(2, id);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                boolean agr = res.getBoolean(1);
                if (agr) {
                    agree = 1;
                } else {
                    agree = -1;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return agree;
    }

    public boolean agree(User u) {
        return agreeOrDis(u, true);
    }

    public boolean disAgree(User u) {
        return agreeOrDis(u, false);
    }

    public boolean cancelAgree(User u) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement s = c.prepareStatement("delete from agree where answer_id = ? and  user_id = ?")) {
            s.setString(1, id);
            s.setString(2, u.getId());
            if (s.executeUpdate() == 1) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private boolean agreeOrDis(User u, boolean agree) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement s1 = c.prepareStatement("select agree from agree where user_id = ? and answer_id = ?");
             PreparedStatement s2 = c.prepareStatement("update agree set agree = ? where user_id = ? and  answer_id = ?");
             PreparedStatement s3 = c.prepareStatement("insert into agree(user_id, answer_id, agree) values (?, ?, ?)")) {
            s1.setString(1, u.getId());
            s1.setString(2, id);
            ResultSet res = s1.executeQuery();
            if (res.next()) {
                if (res.getBoolean(1) == agree) {
                    return true;
                }
                s2.setBoolean(1, agree);
                s2.setString(2, u.getId());
                s2.setString(3, id);
                if (s2.executeUpdate() == 1) {
                    return true;
                }
            } else {
                s3.setString(1, u.getId());
                s3.setString(2, id);
                s3.setBoolean(3, agree);
                if (s3.executeUpdate() == 1) {
                    return true;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static ArrayList<Answer> getAnswersBySearch(String text, int start, int end){
        //TODO add Search function of answer.
        return null;
    }

    public static Answer addAnswer(Question question, User user, String content) {
        String id = GetUUID.getUUID();
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement statement = c.prepareStatement("insert into answer(id, question_id, user_id, content) values(?, ?, ?, ?)");
             PreparedStatement s = c.prepareStatement("select time from answer where id = ?");) {
            statement.setString(1, id);
            statement.setString(2, question.getId());
            statement.setString(3, user.getId());
            statement.setString(4, content);
            if (statement.executeUpdate() == 1) {
                Answer answer = new Answer();
                answer.content = content;
                answer.id = id;
                answer.question = question;
                answer.question_id = question.getId();
                answer.user = user;
                answer.user_id = user.getId();
                s.setString(1, id);
                ResultSet res = s.executeQuery();
                if (res.next()) {
                    answer.time = res.getTimestamp(1);
                    return answer;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Answer> getHottestAnswers(int start, int end) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement s = c.prepareStatement("select id, question_id, user_id, content, time, agrees from answer natural join (select count(*) as agrees, answer_id as id from agree group by answer_id) as f order by agrees - (now() - time)  / 1800 desc limit ?")) {
            return getAnswersList(start, end, s);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public static ArrayList<Answer> getNewestAnswers(int start, int end) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement s = c.prepareStatement("select id, question_id, user_id, content, time from answer order by time desc limit ?")) {
            return getAnswersList(start, end, s);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static ArrayList<Answer> getAnswersList(int start, int end, PreparedStatement s) throws SQLException {
        s.setInt(1, end);
        ResultSet res = s.executeQuery();
        int count = 0;
        ArrayList<Answer> answers = new ArrayList<>();
        while (res.next()) {
            if (start > count) {
                count++;
                continue;
            }
            Answer item = initAnswer(res);
            item.user = User.getById(item.user_id);
            item.question = new Question(item.question_id);
            answers.add(item);
        }
        return answers;
    }

    public static ArrayList<Answer> getAnswersByQuestion(Question q, int start, int end) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement s = c.prepareStatement("select id, question_id, user_id, content, time from answer where question_id = ? order by time desc limit ?;")) {
            s.setString(1, q.getId());
            s.setInt(2, end);
            ArrayList<Answer> res = new ArrayList<>();
            ResultSet result = s.executeQuery();
            int couter = 0;
            while (result.next()) {
                if (couter >= start) {
                    Answer item = initAnswer(result);
                    item.user = User.getById(item.user_id);
                    item.question = q;
                    res.add(item);
                }
                couter++;
            }
            return res;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public static ArrayList<Answer> getAnswersByUser(User u, int start, int end) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement s = c.prepareStatement("select id, question_id, user_id, content, time from answer where user_id = ? order by time desc limit ?")) {
            s.setString(1, u.getId());
            s.setInt(2, end);
            ArrayList<Answer> res = new ArrayList<>();
            ResultSet result = s.executeQuery();
            int count = 0;
            while (result.next()) {
                if (count >= start) {
                    Answer item = initAnswer(result);
                    item.user = u;
                    item.question = new Question(item.question_id);
                    res.add(item);
                }
                count++;
            }
            return res;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Answer> getFollowingsAnswersList(User u, int start, int end) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement s = c.prepareStatement("select id, question_id, user_id, content, time, agrees from (select id, question_id, user_id, content, time from answer where user_id in (select followed_id from follow where follow_id = ?)) as A natural join (select count(*) as agrees, answer_id as id from agree group by answer_id) as f order by agrees - (now() - time)  / 1800 desc limit ?")) {
            s.setString(1, u.getId());
            s.setInt(2, end);
            return getAnswersList(start, end, s);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static Answer initAnswer(ResultSet result) throws SQLException {
        Answer item = new Answer();
        item.id = result.getString(1);
        item.question_id = result.getString(2);
        item.user_id = result.getString(3);
        item.content = result.getString(4);
        item.time = result.getTimestamp(5);
        return item;
    }

    public boolean deleteAnswer() {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement statement = c.prepareStatement("delete from answer where id = ?")) {
            statement.setString(1, id);
            if (statement.executeUpdate() == 1) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public Map<String, Object> getFields() {
        HashMap<String, Object> res = new HashMap<>();
        res.put("answer_id", id);
        res.put("question_id", question_id);
        res.put("question_title", question.getQuestion());
        res.put("answer_author_id", user_id);
        res.put("answer_author", user.getUsername());
        res.put("answer_author_head_sculpture", user.getHead());
        res.put("question", question.getFields());
        res.put("answer_content", content);
        res.put("agree_count", getAgreeCount());
        res.put("comment_count", getCommentCount());
        res.put("time", time.getTime());
        res.put("user", user.getFields());
        return res;
    }

    @Override
    public String toString() {
        return "{" +
                "\"answer_id\":\"" + id + '"' +
                ",\"question_id\":\"" + question_id + '"' +
                ",\"user_id\":\"" + user_id + '"' +
                ",\"question:\"" + question + '"' +
                ",\"content:\"" + content + '"' +
                ",\"time:" + time.getTime() +
                ",\"user:" + user +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getTime() {
        return time;
    }

    public Question getQuestion() {
        return question;
    }

    public User getUser() {
        return user;
    }
}
