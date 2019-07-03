package beans;

import database.DataBasePool;

import java.sql.*;

public class Answer {
    private String id;
    private String user_id;
    private String question_id;
    private String content;
    private Time time;
    private Question question;
    private User user;

    public Answer(String id) {
        connectDB(id);
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
                time = res.getTime(5);
                user = User.getById(user_id);
                question = new Question(question_id);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }



}
