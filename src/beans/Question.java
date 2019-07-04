package beans;

import database.DataBasePool;
import util.GetUUID;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Question implements Entity {
    private String id;
    private String user_id;
    private String question;
    private String content;
    private Timestamp time;
    private User user;
    private ArrayList<Topic> topics;

    public static void main(String[] args) {
        Question question = new Question("bb105db1adc344ad84ds0de84b0863t4");
        System.out.println(question);
    }

    public static ArrayList<Question> getQuestionsByTopic(Topic t){
        ArrayList<Question> ret = new ArrayList<>();
        try(Connection c = DataBasePool.getConnection();
        PreparedStatement s = c.prepareStatement("select question_id from ques_topic where  topic_id = ?")) {
            s.setString(1, t.getId());
            ResultSet res = s.executeQuery();
            while (res.next()){
                String id = res.getString(1);
                ret.add(new Question(id));
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return ret;
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
                topics = Topic.getTopicsByQuestion(this);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private Question() {
    }

    public Question(String id) {
        connectDB(id);
    }

    public static Question addQuestion(User u, String question, String content) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement s = c.prepareStatement("insert into question(id, question, content, user_id) values(?, ?, ?, ?)");
             PreparedStatement st = c.prepareStatement("select time from question where id = ?")) {
            String id = GetUUID.getUUID();
            s.setString(1, id);
            s.setString(2, question);
            s.setString(3, content);
            s.setString(4, u.getId());
            int res = s.executeUpdate();
            if (res == 1) {
                Question q = new Question();
                q.id = id;
                q.content = content;
                q.user_id = u.getId();
                q.user = u;
                q.question = question;
                st.setString(1, id);
                ResultSet ress = st.executeQuery();
                if (ress.next()) {
                    q.time = ress.getTimestamp(1);
                    return q;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Question> getQusetionsByUser(User u) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement s = c.prepareStatement("select id, question, content, user_id, time from question where user_id = ? order by time desc;")) {
            s.setString(1, u.getId());
            ArrayList<Question> res = new ArrayList<>();
            ResultSet result = s.executeQuery();
            while (result.next()) {
                Question item = new Question();
                item.id = result.getString(1);
                item.question = result.getString(2);
                item.content = result.getString(3);
                item.user_id = result.getString(4);
                item.time = result.getTimestamp(5);
                item.user = u;
                res.add(item);
            }
            return res;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public int getAnswerCount() {
        int agree = 0;
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement statement = c.prepareStatement("select count(*) from answer where question_id = ?")) {
            statement.setString(1, id);
            ResultSet res = statement.executeQuery();
            if (res.next()) agree = res.getInt(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return agree;
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

    public ArrayList<Topic> getTopics() {
        return topics;
    }

    @Override
    public Map<String, Object> getFields() {
        HashMap<String, Object> res = new HashMap<>();
        res.put("question_id", id);
        res.put("user_id", user_id);
        res.put("question_title", question);
        res.put("question_desc", content);
        res.put("time", time);
        res.put("user", user.getFields());
        return res;
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

    private boolean addTopic(Topic topics){
        try(Connection c = DataBasePool.getConnection();
        PreparedStatement s = c.prepareStatement("insert into ques_topic(question_id, topic_id) values (?, ?)")) {
            s.setString(1, id);
            s.setString(2, topics.getId());
            if (s.executeUpdate() == 1){
                return true;
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return false;
    }

    public boolean setTopics(String[] topics) {
        if (topics.length + getTopics().size() > 3)
            return false;
        for (String tid :topics){
            Topic topic = new Topic(tid);
            if (topic.getId()!=null){
                if(!addTopic(topic)) return false;
            }
        }
        return true;
    }
}
