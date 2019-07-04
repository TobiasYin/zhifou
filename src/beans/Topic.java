package beans;

import database.DataBasePool;
import util.GetUUID;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Topic implements Entity {
    private String id;
    private String topic;
    private String head;


    public Topic(String id) {
        connectDB(id);
    }

    private Topic() {
    }

    private Topic(String id, String topic, String head) {
        this.id = id;
        this.topic = topic;
        this.head = head;
    }

    public static ArrayList<Topic> getAllTopics() {
        ArrayList<Topic> ret = new ArrayList<>();
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement statement = c.prepareStatement("select id, topic, head from topic")) {
            getTopics((ArrayList<Topic>) ret, statement);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    public static ArrayList<Topic> getTopicsByQuestion(Question q) {
        ArrayList<Topic> ret = new ArrayList<>();
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement s = c.prepareStatement("select topic_id from ques_topic where  question_id = ?")) {
            s.setString(1, q.getId());
            ResultSet res = s.executeQuery();
            while (res.next()) {
                String id = res.getString(1);
                ret.add(new Topic(id));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    private static void getTopics(ArrayList<Topic> ret, PreparedStatement statement) throws SQLException {
        statement.executeQuery();
        ResultSet res = statement.getResultSet();
        while (res.next()) {
            String id = res.getString(1);
            String topic = res.getString(2);
            String head = res.getString(3);
            ret.add(new Topic(id, topic, head));
        }
    }

    public static ArrayList<Topic> getTopicsByKeyword(String keyword) {
        ArrayList<Topic> ret = new ArrayList<>();
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement statement = c.prepareStatement("select id, topic, head from topic where  topic like ?")) {
            statement.setString(1, '%' + keyword + '%');
            getTopics(ret, statement);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    public static Topic getTopicByName(String name) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement statement = c.prepareStatement("select id, topic, head from topic where  topic = ?")) {
            statement.setString(1, name);
            statement.executeQuery();
            ResultSet res = statement.getResultSet();
            if (res.next()) {
                String id = res.getString(1);
                String topic = res.getString(2);
                String head = res.getString(3);
                return new Topic(id, topic, head);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Topic addTopic(String topic) {
        return addTopic(topic, null);
    }

    public static Topic addTopic(String topic, String head) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement s1 = c.prepareStatement("insert into topic (id, topic, head) values (?, ?, ?);");
             PreparedStatement s2 = c.prepareStatement("insert into topic (id, topic) values (?, ?);")) {
            PreparedStatement s;
            String id = GetUUID.getUUID();
            if (head == null) {
                s2.setString(1, id);
                s2.setString(2, topic);
                s = s2;
            } else {
                s1.setString(1, id);
                s1.setString(2, topic);
                s1.setString(3, head);
                s = s1;
            }
            if (s.executeUpdate() == 1) {
                return new Topic(id, topic, head);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void connectDB(String id) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement statement = c.prepareStatement("select id, topic, head from topic where  id = ?")) {
            statement.setString(1, id);
            statement.executeQuery();
            ResultSet res = statement.getResultSet();
            if (res.next()) {
                this.id = res.getString(1);
                topic = res.getString(2);
                head = res.getString(3);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean setHead(String nHead) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement statement = c.prepareStatement("update topic set head = ? where id = ?")) {
            statement.setString(1, nHead);
            statement.setString(2, id);
            if (statement.executeUpdate() == 1) {
                this.head = head;
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public Map<String, Object> getFields() {
        Map<String, Object> res = new HashMap<>();
        res.put("id", id);
        res.put("head", head);
        res.put("topic", topic);
        return null;
    }

    public String getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public String getHead() {
        return head;
    }

}
