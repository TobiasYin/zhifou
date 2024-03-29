package beans;

import database.DataBasePool;
import util.GetUUID;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User implements Entity {
    private String username;
    private String id;
    private String intro;
    private String head;
    private String real_id;
    private String password;
    private String message = "";
    private boolean status = true;

    private void connectDB(String param, String sql) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement statement = c.prepareStatement(sql)) {
            statement.setString(1, param);
            statement.executeQuery();
            ResultSet res = statement.getResultSet();
            if (res.next()) {
                real_id = res.getString(1);
                password = res.getString(2);
                intro = res.getString(3);
                head = res.getString(4);
                username = res.getString(5);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static User getById(String id) {
        User u = new User();
        u.connectDB(id, "select id, password, introduce, head, name from user where id = ?");
        u.id = u.real_id;
        return u;
    }

    public User() {
    }

    public User(String username) {
        this.username = username;
        connectDB(username, "select id,  password, introduce, head, name from user where name = ?");
        id = real_id;
    }

    //使用此构造器后请对比是否是真实用户.
    public User(String username, String id) {
        this.username = username;
        this.id = id;
        connectDB(username, "select id,  password, introduce, head, name from user where name = ?");
    }

    private User(String username, String id, String intro, String head) {
        this.username = username;
        this.id = id;
        real_id = id;
        this.intro = intro;
        this.head = head;
    }

    public static ArrayList<User> getUsersBySearch(String text) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement statement = c.prepareStatement("select id, name, introduce, head from user where name like ?")) {
            statement.setString(1, '%' + text + '%');
//            statement.setInt(2, end);
            ResultSet res = statement.executeQuery();
            ArrayList<User> users = new ArrayList<>();
//            int count = 0;
            while (res.next()) {
//                if (count >= start) {
                    initUser(res, users);
//                }
//                count++;
            }
            return users;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static void initUser(ResultSet res, ArrayList<User> users) throws SQLException {
        String id = res.getString(1);
        String name = res.getString(2);
        String intro = res.getString(3);
        String head = res.getString(4);
        User u = new User(name, id, intro, head);
        users.add(u);
    }

    public static User register(String username, String password) {
        User user = new User(username);
        try (Connection connection = DataBasePool.getConnection();
             PreparedStatement statement = connection.prepareStatement("insert into user(id, name, password) values (?,?,?)")) {
            if (user.id != null) {
                user.message = "用户名已存在";
                user.status = false;
            } else {
                statement.setString(1, GetUUID.getUUID());
                statement.setString(2, username);
                statement.setString(3, password);
                if (statement.executeUpdate() == 1) {
                    user.connectDB(username, "select id,  password, introduce, head, name from user where name = ?");
                    user.id = user.real_id;
                } else {
                    user.message = "未知错误";
                    user.status = false;
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            user.message = "未知错误";
            user.status = false;
        }
        return user;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public boolean isUser() {
        return this.id.equals(real_id);
    }

    public static User login(String username, String password) {
        User u = new User(username);
        if (password.equals(u.password))
            return u;
        else return null;
    }

    public String getUsername() {
        return username;
    }


    public String getId() {
        return id;
    }


    public String getIntro() {
        if (intro != null)
            return intro;
        else return "用户很懒, 还没有自我介绍哟~";
    }

    public boolean setIntro(String intro) {
        try (Connection connection = DataBasePool.getConnection();
             PreparedStatement statement = connection.prepareStatement("update user set introduce = ? where name = ?")) {
            statement.setString(1, intro);
            statement.setString(2, username);
            int res = statement.executeUpdate();
            if (res == 1) {
                this.intro = intro;
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean setPassword(String password) {
        try (Connection connection = DataBasePool.getConnection();
             PreparedStatement statement = connection.prepareStatement("update user set password = ? where name = ?")) {
            statement.setString(1, password);
            statement.setString(2, username);
            int res = statement.executeUpdate();
            if (res == 1) {
                this.password = password;
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public String getHead() {
        if (head != null)
            return head;
        else return "/image/default.jpg";
    }

    public boolean setHead(String head) {
        try (Connection connection = DataBasePool.getConnection();
             PreparedStatement statement = connection.prepareStatement("update user set head = ? where name = ?")) {
            statement.setString(1, password);
            statement.setString(2, username);
            int res = statement.executeUpdate();
            if (res == 1) {
                this.head = head;
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public String toString() {
        return "{\"username\":\"" + username + "\",\"userid\":\"" + id + "\",\"head_sculpture\":\"" + getHead() + "\",\"intro\":\"" + getIntro() + "\"}";
    }

    @Override
    public Map<String, Object> getFields() {
        HashMap<String, Object> res = new HashMap<>();
        res.put("username", username);
        res.put("userid", id);
        res.put("user_id", id);
        res.put("head_sculpture", getHead());
        res.put("intro", getIntro());
        return res;
    }

    public boolean isFollow(User u) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement s = c.prepareStatement("select * from follow where follow_id = ? and followed_id = ?")) {
            s.setString(1, id);
            s.setString(2, u.id);
            return s.executeQuery().next();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public int getAnswerCount() {
        return Answer.getAnswerCountByUser(this);
    }

    public int getFollowCount() {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement s = c.prepareStatement("select count(*) from follow where followed_id = ?")) {
            s.setString(1, id);
            ResultSet res = s.executeQuery();
            if (res.next()) {
                return res.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public ArrayList<User> getFollowees() {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement s = c.prepareStatement("select id, name, introduce, head from user where id in (select followed_id from follow where follow_id = ?)")) {
            return getUsers(s);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public ArrayList<User> getFollowers() {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement s = c.prepareStatement("select id, name, introduce, head from user where id in (select follow_id from follow where followed_id = ?)")) {
            return getUsers(s);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private ArrayList<User> getUsers(PreparedStatement s) throws SQLException {
        s.setString(1, id);
        ResultSet res = s.executeQuery();
        ArrayList<User> ret = new ArrayList<>();
        while (res.next()) {
            initUser(res, ret);
        }
        return ret;
    }

    public boolean follow(User u) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement statement = c.prepareStatement("insert into follow(follow_id, followed_id) values (?, ?)")) {
            statement.setString(1, getId());
            statement.setString(2, u.getId());
            int res = statement.executeUpdate();
            if (res == 1) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean unfollow(User u) {
        try (Connection c = DataBasePool.getConnection();
             PreparedStatement statement = c.prepareStatement("delete from follow where follow_id = ? and followed_id = ?")) {
            statement.setString(1, getId());
            statement.setString(2, u.getId());
            int res = statement.executeUpdate();
            if (res == 1) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean checkPassword(String pass) {
        return password.equals(pass);
    }

    //是否赞同一篇文章, 由于涉及两个实体, 在两个实体中提供相同方法.
    public int isAgree(Answer answer) {
        return answer.isAgree(this);
    }

    public boolean agree(Answer answer) {
        return answer.agree(this);
    }

    public boolean disagree(Answer answer) {
        return answer.disAgree(this);
    }

    public boolean cancelAgree(Answer answer) {
        return answer.cancelAgree(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            User o = (User) obj;
            return o.username.equals(username) && o.id.equals(id) && o.real_id.equals(real_id);
        }
        return false;
    }
}
