package jp.ac.jec.herBatis.utils;

import jp.ac.jec.herBatis.cfg.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;

public class DataSourceUtils {

    public static Connection getConnection(Configuration configuration) {
        try {
            Class.forName(configuration.getDriver());
            Connection conn =
                    DriverManager.getConnection(configuration.getUrl(), configuration.getUsername(), configuration.getPassword());
            conn.setAutoCommit(configuration.isAutoCommit());
            return conn;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
