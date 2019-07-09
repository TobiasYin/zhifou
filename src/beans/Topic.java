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
    private String desc;


    public Topic(String id) {
        connectDB(id);
    }

    private Topic() {
    }

    private Topic(String id, String topic, String head, String desc) {
        this.id = id;
        this.topic = topic;
        this.head = head;
        this.desc = desc;
    }

    public static ArrayList<Topic> getAllTopics(int start, int end) {
        ArrayList<Topic> ret = new ArrayList<>();
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement statement = c.prepareStatement("select id, topic, head, description from topic limit ?")) {
            statement.setInt(1, end);
            statement.executeQuery();
            int counter = 0;
            ResultSet res = statement.getResultSet();
            while (res.next()) {
                if (counter >= start) {
                    String id = res.getString(1);
                    String topic = res.getString(2);
                    String head = res.getString(3);
                    String desc = res.getString(4);
                    ret.add(new Topic(id, topic, head, desc));
                }
                counter++;
            }
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
            String desc = res.getString(4);
            ret.add(new Topic(id, topic, head, desc));
        }
    }

    public static ArrayList<Topic> getTopicsByKeyword(String keyword) {
        ArrayList<Topic> ret = new ArrayList<>();
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement statement = c.prepareStatement("select id, topic, head, description from topic where  topic like ?")) {
            statement.setString(1, '%' + keyword + '%');
            getTopics(ret, statement);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    public static Topic getTopicByName(String name) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement statement = c.prepareStatement("select id, topic, head, description from topic where  topic = ?")) {
            statement.setString(1, name);
            statement.executeQuery();
            ResultSet res = statement.getResultSet();
            if (res.next()) {
                String id = res.getString(1);
                String topic = res.getString(2);
                String head = res.getString(3);
                String desc = res.getString(4);
                return new Topic(id, topic, head, desc);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Topic addTopic(String topic) {
        return addTopic(topic, null, null);
    }

    public static Topic addTopic(String topic, String desc, String head) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement s1 = c.prepareStatement("insert into topic (id, topic, head, description) values (?, ?, ?, ?);");
             PreparedStatement s2 = c.prepareStatement("insert into topic (id, topic, description) values (?, ?, ?);")) {
            PreparedStatement s;
            String id = GetUUID.getUUID();
            if (head == null) {
                s2.setString(1, id);
                s2.setString(2, topic);
                s2.setString(3, desc);
                s = s2;
            } else {
                s1.setString(1, id);
                s1.setString(2, topic);
                s1.setString(3, head);
                s1.setString(4, desc);
                s = s1;
            }
            if (s.executeUpdate() == 1) {
                return new Topic(id, topic, head, desc);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void connectDB(String id) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement statement = c.prepareStatement("select id, topic, head, description from topic where  id = ?")) {
            statement.setString(1, id);
            statement.executeQuery();
            ResultSet res = statement.getResultSet();
            if (res.next()) {
                this.id = res.getString(1);
                topic = res.getString(2);
                head = res.getString(3);
                desc = res.getString(4);
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
        res.put("topic_id", id);
        res.put("head", getHead());
        res.put("topic_pic", getHead());
        res.put("topic_name", topic);
        res.put("topic", topic);
        res.put("description", desc);
        return res;
    }

    public String getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public String getHead() {
        if (head != null)
            return head;
        else return "/image/default.jpg";
    }

    public String getDesc() {
        if (desc == null || desc.equals("")) {
            return "此话题暂时没有描述~";
        }
        return desc;
    }
}
