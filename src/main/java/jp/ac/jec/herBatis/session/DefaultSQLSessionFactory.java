package jp.ac.jec.herBatis.session;

import jp.ac.jec.herBatis.cfg.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DefaultSQLSessionFactory implements SQLSessionFactory {

    private DataSource dataSource;

    private Configuration configuration;


    public DefaultSQLSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    public DefaultSQLSessionFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public SQLSession openSession() {
        return openSessionFromDataSource(dataSource, true);
    }

    @Override
    public SQLSession openSessionWithConfiguration() {
        return new DefaultSQLSession(configuration);
    }

    public SQLSession openSessionWithConfiguration(boolean autoCommit) {
        return new DefaultSQLSession(configuration, autoCommit);
    }

    @Override
    public SQLSession openSession(boolean autoCommit) {
        return openSessionFromDataSource(dataSource, autoCommit);
    }

    @Override
    public SQLSession openSession(Connection connection) {
        return new DefaultSQLSession(connection);
    }

    @Override
    public DataSource getDataSource() {
        return this.dataSource;
    }

    private SQLSession openSessionFromDataSource(DataSource dataSource, boolean autoCommit) {
        try {
            Connection conn = dataSource.getConnection();
            conn.setAutoCommit(autoCommit);
            return new DefaultSQLSession(conn);
        } catch (SQLException e) {
            throw new RuntimeException("DataSourceで作られたConnectionはエラーです。");
        }
    }
}
