package database;

import org.apache.commons.dbcp2.BasicDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DataBasePool {
    private static BasicDataSource ds;

    static {
        ds = new BasicDataSource();
        Properties configs = new Properties();
        String classFilePath = DataBasePool.class.getResource("").getPath();
        String config = "db.properties";
        try (FileInputStream fs = new FileInputStream(classFilePath + config)) {
            configs.load(fs);
            ds.setDriverClassName(configs.getProperty("driver"));
            ds.setUrl(configs.getProperty("url"));
            ds.setUsername(configs.getProperty("username"));
            ds.setPassword(configs.getProperty("password"));
            ds.setInitialSize(5);
            ds.setMaxTotal(50);
            ds.setMinIdle(5);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}

