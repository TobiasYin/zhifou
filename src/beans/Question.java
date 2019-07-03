package beans;

import database.DataBasePool;
import util.GetUUID;

import java.sql.*;

public class Question {
    private String id;
    private String user_id;
    private String question;
    private String content;
    private Timestamp time;
    private User user;

    public static void main(String[] args) {
        Question question = new Question("bb105db1adc344ad84ds0de84b0863t4");
        System.out.println(question);
    }

    private void connectDB(String id) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement statement = c.prepareStatement("select id, user_id, question, content, time from question where id = ?")) {
            statement.setString(1, id);
            statement.executeQuery();
            ResultSet res = statement.getResultSet();
            if (res.next()) {
                this.id = res.getString(1);
                user_id = res.getString(2);
                question = res.getString(3);
                content = res.getString(4);
                time = res.getTimestamp(5);
                user = User.getById(user_id);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private Question() {}

    public Question(String id) {
        connectDB(id);
    }

    public static Question addQuestion(User u, String question, String content) {
        try(Connection c = DataBasePool.getConnection();
        PreparedStatement s = c.prepareStatement("insert into question(id, question, content, user_id) values(?, ?, ?, ?)")) {
            s.setString(1, GetUUID.getUUID());
            s.setString(2, question);
            s.setString(3, content);
            s.setString(4, u.getId());
            int res = s.executeUpdate();
            if (res==1){
                Question q = new Question();
                q.content = content;
                q.user_id = u.getId();
                q.user = u;
                q.question = question;
                return q;
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return null;
    }

    public User getUser() {
        return user;
    }

    public String getQuestion() {
        return question;
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

    @Override
    public String toString() {
        return "{" +
                "\"question_id\":\"" + id + '"' +
                ",\"user_id\":\"" + user_id + '"' +
                ",\"question:\"" + question + '"' +
                ",\"content:\"" + content + '"' +
                ",\"time:" + time.getTime() +
                ",\"user:" + user +
                '}';
    }
}
