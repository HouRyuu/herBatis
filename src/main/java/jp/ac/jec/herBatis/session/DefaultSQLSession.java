package jp.ac.jec.herBatis.session;

import jp.ac.jec.herBatis.builder.MapperProxy;
import jp.ac.jec.herBatis.cfg.Configuration;
import jp.ac.jec.herBatis.utils.DataSourceUtils;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

public class DefaultSQLSession implements SQLSession {

    private Configuration configuration;
    private final Connection connection;

    public DefaultSQLSession(Configuration configuration) {
        this.configuration = configuration;
        connection = DataSourceUtils.getConnection(configuration);
    }

    public DefaultSQLSession(Connection connection) {
        this.connection = connection;
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new MapperProxy(configuration.getMapperMap(), connection));
    }

    @Override
    public void commit() {
        try {
            if (connection != null && !connection.getAutoCommit()) {
                connection.commit();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new IOException(e);
            }
        }
    }
}
