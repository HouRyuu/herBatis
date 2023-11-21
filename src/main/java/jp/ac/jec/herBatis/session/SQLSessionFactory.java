package jp.ac.jec.herBatis.session;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * SQLSession工場
 */
public interface SQLSessionFactory {

    /**
     * トランザクションなしのSQLSessionを生産する
     *
     * @return SQLSession
     */
    SQLSession openSession();

    SQLSession openSessionWithConfiguration();

    /**
     * トランザクションがコントロールできるSQLSessionを生産する
     *
     * @param autoCommit 自動コミットするかどうか
     * @return SQLSession
     */
    SQLSession openSession(boolean autoCommit);

    /**
     * JDBCのConnectionでSQLSessionを生産する
     *
     * @param connection JDBCのConnection
     * @return SQLSession
     */
    SQLSession openSession(Connection connection);

    /**
     * データベースへの接続情報
     *
     * @return DataSource
     */
    DataSource getDataSource();

}
