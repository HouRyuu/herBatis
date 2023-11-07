package jp.ac.jec.herBatis.excuter;

public interface Executer<T> {

    T execute(String sql, Object[] args);

}
