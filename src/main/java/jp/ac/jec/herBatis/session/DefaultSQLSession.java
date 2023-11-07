package jp.ac.jec.herBatis.session;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class DefaultSQLSession implements SQLSession {

    private final Connection connection;

    public DefaultSQLSession(Connection connection) {
        this.connection = connection;
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return null;
    }

    @Override
    public void close() throws IOException {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}
