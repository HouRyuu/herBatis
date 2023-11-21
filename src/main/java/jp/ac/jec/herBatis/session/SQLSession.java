package jp.ac.jec.herBatis.session;

import java.io.Closeable;

public interface SQLSession extends Closeable {

    <T> T getMapper(Class<T> type);

    void commit();
}
